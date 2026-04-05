package emu.grasscutter.server.packet.recv;

import com.google.gson.GsonBuilder;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.Account;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.TypedPacketPairHandler;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.game.GameSession.SessionState;
import emu.grasscutter.utils.ByteHelper;
import emu.grasscutter.utils.Crypto;
import emu.grasscutter.utils.Utils;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.GetPlayerTokenReq;
import org.anime_game_servers.multi_proto.gi.messages.player.GetPlayerTokenRsp;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.crypto.Cipher;

import static emu.grasscutter.config.Configuration.ACCOUNT;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.security.Signature;

public class HandlerGetPlayerTokenReq extends TypedPacketPairHandler<GetPlayerTokenReq, GetPlayerTokenRsp> {

    public static class VerifyResult {
        public int retcode;
        public VerifyResultData data;
    }
    public static class VerifyResultIpInfo {
        public String country_code;
    }
    public static class VerifyResultData {
        public boolean guest;
        public int account_type;
        public int account_uid;
        public VerifyResultIpInfo ip_info;
    }

    @Override
    public boolean handle(GameSession session, byte[] header, GetPlayerTokenReq req, GetPlayerTokenRsp rsp) {

        // Authenticate
        String accountId = null;
        var reservedUid = 0;
        var isAccountBanned = false;
        val sdkServer= Grasscutter.config.account.sdkServer;
        if(sdkServer != null && !sdkServer.isBlank()){
            // TODO - implement verify of combo toke from sdk server
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
            HttpClient httpclient = new HttpClient(sslContextFactory);
            try {
                httpclient.start();
                val request = httpclient.POST(sdkServer);
                request.content(new StringContentProvider("{\"combo_token\": \"" + req.getAccountToken() + "\", \"open_id\": \"" + req.getAccountUid() + "\"}"));

                val response = request.send();
                val body = response.getContentAsString();
                val gson = new GsonBuilder().create();
                val comboTokenResJson = gson.fromJson(body, VerifyResult.class);
                if (comboTokenResJson.retcode != 0) {
                    session.close();
                    return false;
                }
                accountId = Integer.toString(comboTokenResJson.data.account_uid);
            } catch (Exception ex){
                session.close();
                return false;
            }
        } else {
            Account account = DatabaseHelper.getAccountById(req.getAccountUid());
            // Set account
            session.setAccount(account);
            if (account == null || !account.getToken().equals(req.getAccountToken())) {
                session.close();
                return false;
            }
            accountId = account.getId();
            isAccountBanned = account.isBanned();
            reservedUid = account.getReservedPlayerUid();
        }

        if(accountId == null || !accountId.equals(req.getAccountUid())){
            // TODO, we should close the session or inform the client about an error
            return false;
        }
        session.setAccountId(accountId);
        session.setSessionToken(req.getAccountToken());

        // Check if player object exists in server
        // NOTE: CHECKING MUST SITUATED HERE (BEFORE getPlayerByUid)! because to save firstly ,to load secondly !!!
        // TODO - optimize
        boolean kicked = false;
        Player exists = Grasscutter.getGameServer().getPlayerByAccountId(accountId);
        if (exists != null) {
            GameSession existsSession = exists.getSession();
            if (existsSession != session) {// No self-kicking
                exists.onLogout();//must save immediately , or the below will load old data
                existsSession.close();
                Grasscutter.getLogger().warn("Player {} with id {} was kicked due to duplicated login", exists.getNickname(), accountId);
                kicked = true;
            }
        }

        //NOTE: If there are 5 online players, max count of player is 5,
        // a new client want to login by kicking one of them ,
        // I think it should be allowed
        if (!kicked) {
            // Max players limit
            if (ACCOUNT.maxPlayer > -1 && Grasscutter.getGameServer().getPlayers().size() >= ACCOUNT.maxPlayer) {
                session.close();
                return false;
            }
        }

        // Call creation event.
        PlayerCreationEvent event = new PlayerCreationEvent(session, Player.class);
        event.call();

        // Get player.
        Player player = DatabaseHelper.getPlayerByAccount(accountId, event.getPlayerClass());

        if (player == null) {
            int nextPlayerUid = DatabaseHelper.getNextPlayerId(reservedUid);

            // Create player instance from event.
            try {
                player = event.getPlayerClass().getDeclaredConstructor(GameSession.class).newInstance(session);
            } catch (Exception e) {
                session.close();
                throw new RuntimeException(e);
            }

            // Save to db
            DatabaseHelper.generatePlayerUid(player, nextPlayerUid);
        }

        // Set player object for session
        session.setPlayer(player);

        rsp.setUid(player.getUid());
        rsp.setProficientPlayer(player.getAvatars().getAvatarCount() > 0);
        rsp.setRegPlatform(3);
        rsp.setCountryCode("US");
        rsp.setClientIpStr(session.getAddress().getAddress().getHostAddress());

        // Checks if the player is banned
        if (isAccountBanned) {
            session.setState(SessionState.ACCOUNT_BANNED);
            rsp.setRetCode(Retcode.RET_BLACK_UID);
            rsp.setMsg("FORBID_CHEATING_PLUGINS");
            rsp.setBlackUidEndTime(session.getAccount().getBanEndTime());
            return true;
        }

        // Load player from database
        player.loadFromDatabase();

        // Set session state
        session.setUseSecretKey(true);
        session.setState(SessionState.WAITING_FOR_LOGIN);

        rsp.setToken(session.getSessionToken());
        rsp.setAccountType(1);
        rsp.setSecretKeySeed(Crypto.ENCRYPT_SEED);
        rsp.setSecurityCmdBuffer(Crypto.ENCRYPT_SEED_BUFFER);
        rsp.setPlatformType(3);
        rsp.setChannelId(1);
        rsp.setClientVersionRandomKey("c25-314dd05b0b5f");

        // Only >= 2.7.50 has this
        if (req.getKeyId() > 0) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, Crypto.CUR_SIGNING_KEY);

                var client_seed_encrypted = Utils.base64Decode(req.getClientRandKey());
                var client_seed = ByteBuffer.wrap(cipher.doFinal(client_seed_encrypted))
                    .getLong();

                byte[] seed_bytes = ByteBuffer.wrap(new byte[8])
                    .putLong(Crypto.ENCRYPT_SEED ^ client_seed)
                    .array();

                cipher.init(Cipher.ENCRYPT_MODE, Crypto.EncryptionKeys.get(req.getKeyId()));
                var seed_encrypted = cipher.doFinal(seed_bytes);

                Signature privateSignature = Signature.getInstance("SHA256withRSA");
                privateSignature.initSign(Crypto.CUR_SIGNING_KEY);
                privateSignature.update(seed_bytes);

                rsp.setServerRandKey(Utils.base64Encode(seed_encrypted));
                rsp.setSign(Utils.base64Encode(privateSignature.sign()));
            } catch (Exception ignore) {
                // Only UA Patch users will have exception
                byte[] clientBytes = Utils.base64Decode(req.getClientRandKey());
                byte[] seed = ByteHelper.longToBytes(Crypto.ENCRYPT_SEED);
                Crypto.xor(clientBytes, seed);

                String base64str = Utils.base64Encode(clientBytes);

                rsp.setServerRandKey(base64str);
                rsp.setSign("bm90aGluZyBoZXJl");
            }
        }
        return true;
    }

    @Override
    public void sendRsp(GameSession session, GetPlayerTokenRsp response) {
        val packet = new BaseTypedPacket<>(response, true) {};
        packet.setUseDispatchKey(true);
        session.send(packet);
    }
}
