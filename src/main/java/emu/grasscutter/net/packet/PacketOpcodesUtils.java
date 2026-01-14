package emu.grasscutter.net.packet;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import emu.grasscutter.GameConstants;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.JsonUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;

public class PacketOpcodesUtils {

    public static final Set<String> BANNED_PACKETS = Set.of(
        "WindSeedClientNotify",
        "PlayerLuaShellNotify"
    );

    public static final Set<String> LOOP_PACKETS = Set.of(
        "PingReq",
        "PingRsp",
        "WorldPlayerRTTNotify",
        "UnionCmdNotify",
        "QueryPathReq",
        "QueryPathRsp",

        // Satiation sends these every tick
        "PlayerTimeNotify",
        "PlayerGameTimeNotify",
        "AvatarPropNotify",
        "AvatarSatiationDataNotify"
    );

    public static String getOpcodeName(int opcode, GameSession session) {
        if (opcode <= 0) return "UNKNOWN";
        val provider = session.getPackageIdProvider();
        if (provider != null) {
            val name = provider.getPacketName(opcode);
            if (name != null) return name;
        }
        return "UNKNOWN";
    }
}
