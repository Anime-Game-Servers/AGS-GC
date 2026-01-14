package emu.grasscutter.server.packet.scene.notify;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.LuaEnvironmentEffectNotify;

import java.util.List;

public class PacketLuaEnvironmentEffectNotify extends BaseTypedPacket<LuaEnvironmentEffectNotify> {

	public PacketLuaEnvironmentEffectNotify(int typeIndex, String key, List<Float> floatParams, List<Integer> intParams) {
		super(new LuaEnvironmentEffectNotify(key, floatParams, intParams, typeIndex));
	}
}
