package emu.grasscutter.server.packet.scene.notify;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.notify.ShowTemplateReminderNotify;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketShowTemplateReminderNotify extends BaseTypedPacket<ShowTemplateReminderNotify> {

    public PacketShowTemplateReminderNotify(int reminderId, @NotNull List<Integer> paramList) {
        super(new ShowTemplateReminderNotify());
        proto.setTemplateReminderId(reminderId);
        proto.setParamList(paramList);
    }

    public PacketShowTemplateReminderNotify(int reminderId, @NotNull List<Integer> paramList, @NotNull List<Integer> uidParamsList,
                                            boolean isNeedCache) {
        super(new ShowTemplateReminderNotify());
        proto.setTemplateReminderId(reminderId);
        proto.setParamList(paramList);
        proto.setParamUidList(uidParamsList);
        proto.setNeedCache(isNeedCache);
    }

    public PacketShowTemplateReminderNotify(int reminderId, boolean isRevoke) {
        super(new ShowTemplateReminderNotify());
        proto.setTemplateReminderId(reminderId);
        proto.setRevoke(isRevoke);
    }
}
