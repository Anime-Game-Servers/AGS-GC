package emu.grasscutter.server.packet.battle;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.LuaOptionType;
import org.anime_game_servers.multi_proto.gi.messages.battle.LuaSetOptionNotify;

public class PacketLuaSetOptionNotify extends BaseTypedPacket<LuaSetOptionNotify> {

	public PacketLuaSetOptionNotify(String key, LuaOptionType optionType) {
		super(new LuaSetOptionNotify(key, optionType));
	}
}
