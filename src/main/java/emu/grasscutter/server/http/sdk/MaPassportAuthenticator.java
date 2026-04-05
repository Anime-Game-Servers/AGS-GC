package emu.grasscutter.server.http.sdk;

import at.favre.lib.crypto.bcrypt.BCrypt;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.Account;
import emu.grasscutter.server.http.objects.*;
import emu.grasscutter.server.http.objects.BaseLoginResponseData.*;
import lombok.val;

import java.util.ArrayList;

import static emu.grasscutter.config.Configuration.ACCOUNT;

public class MaPassportAuthenticator {
    private MaPassportAuthenticator() {
        /* This utility class should not be instantiated */
    }

    public static LoginByPasswordResponseJson appLoginByPassword(LoginByPasswordRequestJson request) {
        Grasscutter.getLogger().info("ma-passport login req detected");

        if (request == null) {
            Grasscutter.getLogger().error("Request is null");
            return createLoginErrorResponse(-1, "Invalid request");
        }

        if (request.account == null || request.password == null) {
            Grasscutter.getLogger().error("Missing credentials");
            return createLoginErrorResponse(-1, "Missing credentials");
        }

        // decrypt acc
        String username;
        try {
            username = RSADecryptionUtil.decrypt(request.account);
        } catch (Exception e) {
            Grasscutter.getLogger().error("Unable to decrypt account ", e);
            return createLoginErrorResponse(-10, "Unable to decrypt account");
        }

        // decrypt password next
        String password;
        try {
            password = RSADecryptionUtil.decrypt(request.password);
        } catch (Exception e) {
            Grasscutter.getLogger().error("Unable to decrypt password", e);
            return createLoginErrorResponse(-10, "Unable to decrypt password");
        }

        try {
            Account account = DatabaseHelper.getAccountByName(username);

            if (account == null && ACCOUNT.autoCreate) { // This account has been created AUTOMATICALLY. There will be no permissions added.
                if (password.length() >= 8) {
                    account = DatabaseHelper.createAccountWithUid(username, 0);

                    if (account != null) {
                        account.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                        account.save();
                        // Log the creation.
                        Grasscutter.getLogger().info("Account {} created", username);
                    }
                }
            }

            if (account == null) {
                Grasscutter.getLogger().info("Account not found: {}", username);
                return createLoginErrorResponse(-101, "Account or password error");
            }

            if (!account.verifyPassword(password)) {
                Grasscutter.getLogger().info("Password verification failed");
                return createLoginErrorResponse(-101, "Account or password error");
            }


            Grasscutter.getLogger().info("Generating session key");
            String sessionKey = account.getSessionKey();
            if (sessionKey == null || !sessionKey.startsWith("v2_")) {
                account.generateV2SessionKey();
            } else {
                Grasscutter.getLogger().info("Using existing key");
            }

            Grasscutter.getLogger().info("User {} has successfully logged in", username);
            return createLoginSuccessResponse(account);

        } catch (Exception e) {
            Grasscutter.getLogger().error("Exception: ", e);
            return createLoginErrorResponse(-1, "Internal server error: " + e.getMessage());
        }
    }

    public static VerifySTokenResponseJson verifySToken(VerifySTokenRequestJson request) {
        try {
            Grasscutter.getLogger().debug("Ma-passport token verification for mid: {}", request.mid);

            // get acc by id in db
            Account account = DatabaseHelper.getAccountById(request.mid);
            if (account == null) {
                Grasscutter.getLogger().info("Account not found for mid: {}", request.mid);
                return createTokenErrorResponse(-101, "For account safety, please log in again");
            }

            // Check if the session key matches the provided stoken
            String accountSessionKey = account.getSessionKey();
            if (accountSessionKey == null || !accountSessionKey.equals(request.stoken)) {
                Grasscutter.getLogger().info("Invalid session token for account: {}", account.getUsername());
                return createTokenErrorResponse(-101, "For account safety, please log in again");
            }

            Grasscutter.getLogger().info("Ma-Passport token verification successful for: {}", account.getUsername());
            return createTokenSuccessResponse(account);

        } catch (Exception e) {
            Grasscutter.getLogger().error("Error in ma-passport token verification", e);
            return createTokenErrorResponse(-1, "Internal server error");
        }
    }

    private static LoginByPasswordResponseJson createLoginSuccessResponse(Account account) {
        LoginByPasswordResponseJson response = new LoginByPasswordResponseJson();
        response.retcode = 0;
        response.message = "OK";

        val tokenData = new TokenData(1, account.getSessionKey());
        val userInfo = UserInfoData.fromAccount(account);

        val extUserInfoData = new ExtUserInfoData("", "0");

        response.data = new LoginByPasswordResponseJson.LoginData(tokenData, userInfo, extUserInfoData,
            "","");

        return response;
    }

    private static LoginByPasswordResponseJson createLoginErrorResponse(int retcode, String message) {
        LoginByPasswordResponseJson response = new LoginByPasswordResponseJson();
        response.retcode = retcode;
        response.message = message;
        response.data = null;
        return response;
    }

    private static VerifySTokenResponseJson createTokenSuccessResponse(Account account) {
        VerifySTokenResponseJson response = new VerifySTokenResponseJson();
        response.retcode = 0;
        response.message = "OK";

        val tokens = new ArrayList<TokenData>();
        val tokenData = new TokenData(1, account.getSessionKey());
        tokens.add(tokenData);
        val userInfo = UserInfoData.fromAccount(account);

        val extUserInfoData = new ExtUserInfoData("", "0");

        response.data = new VerifySTokenResponseJson.VerifyData(userInfo, tokens, extUserInfoData);

        return response;
    }

    private static VerifySTokenResponseJson createTokenErrorResponse(int retcode, String message) {
        VerifySTokenResponseJson response = new VerifySTokenResponseJson();
        response.retcode = retcode;
        response.message = message;
        response.data = null;
        return response;
    }
}
