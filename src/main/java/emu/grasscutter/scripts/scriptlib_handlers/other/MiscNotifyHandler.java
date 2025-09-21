package emu.grasscutter.scripts.scriptlib_handlers.other;

import emu.grasscutter.Loggers;
import emu.grasscutter.data.GameData;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.script_lib.handler.other.AssignPlayerShowTemplateReminderParams;
import org.anime_game_servers.gi_lua.script_lib.handler.other.AssignPlayerUidOpNotifyParams;
import org.anime_game_servers.gi_lua.script_lib.handler.other.ScenePlaySoundParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class MiscNotifyHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.MiscNotifyScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int notifyAllPlayerPerformOperation(@NotNull GroupEventLuaContext context, int teamEntityId, int type, int effectIndex, @NotNull Vector hunterPos, @NotNull Vector hunterRot) {
        return 0;
    }

    @Override
    public int setEnvironmentEffectState(@NotNull GroupEventLuaContext context, int index, @NotNull String key, @NotNull List<Float> floatParamTable, @NotNull List<Integer> intParam) {
        return 0;
    }

    @Override
    public int setLimitOptimization(@NotNull GroupEventLuaContext context, int uid, boolean isLimitOptimization) {
        return 0;
    }

    @Override
    public int setPlayerInteractOption(@NotNull GroupEventLuaContext context, @NotNull String key) {
        // TODO send LuaSetOptionNotify
        return 0;
    }

    @Override
    public int assignPlayerUidOpNotify(@NotNull GroupEventLuaContext context, @NotNull AssignPlayerUidOpNotifyParams params) {
        return 0;
    }

    @Override
    public int scenePlaySound(@NotNull GroupEventLuaContext context, @NotNull ScenePlaySoundParams soundInfo) {
        logger.debug("[LUA] Call unimplemented ScenePlaySound with {}", soundInfo);
        val soundName = soundInfo.getSoundName();
        val playType = soundInfo.getPlayType();
        val playPosition = soundInfo.getPlayPos();

        val scene = context.getSceneScriptManager().getScene();
        val packet = new PacketScenePlayerSoundNotify(new Position(playPosition), soundName, playType);
        if (soundInfo.isBroadcast())
            scene.broadcastPacket(packet);
        else if (context.uid() != 0) {
            scene.getPlayers().stream().filter(player -> player.getUid() == context.uid()).forEach(
                player -> player.sendPacket(packet)
            );
        } else {
            scene.getWorld().getHost().sendPacket(packet);
        }
        return 0;
    }

    @Override
    public int sendServerMessageByLuaKey(@NotNull GroupEventLuaContext context, @NotNull String messageKey, @NotNull List<Integer> targets) {
        return 0;
    }

    @Override
    public int showReminderByUid(@NotNull GroupEventLuaContext context, @NotNull List<Integer> uidList, int reminderId) {
        val packet = new PacketDungeonShowReminderNotify(reminderId);
        context.getSceneScriptManager().getScene().getPlayers().stream()
            .filter(player -> uidList.contains(player.getUid()))
            .forEach(player -> player.sendPacket(packet));
        return 0;
    }

    @Override
    public int stopReminder(@NotNull GroupEventLuaContext context, int reminderId) {
        return 0;
    }

    @Override
    public int showReminder(GroupEventLuaContext context, int reminderId) {
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketDungeonShowReminderNotify(reminderId));
        return 0;
    }

    @Override
    public int showReminderRadius(@NotNull GroupEventLuaContext context, int reminderId, @NotNull Vector position, int radius) {
        return 0;
    }

    @Override
    public int showTemplateReminder(@NotNull GroupEventLuaContext context, int reminderId, @NotNull List<Integer> timerInfo) {
        return 0;
    }

    @Override
    public int assignPlayerShowTemplateReminder(@NotNull GroupEventLuaContext context, int reminderId, @NotNull AssignPlayerShowTemplateReminderParams params) {
        return 0;
    }

    @Override
    public int revokePlayerShowTemplateReminder(@NotNull GroupEventLuaContext context, int reminderId, @NotNull List<Integer> uidList) {
        return 0;
    }

    @Override
    public int showClientGuide(@NotNull GroupEventLuaContext context, @NotNull String guideName) {
        logger.debug("[LUA] Call unimplemented ShowClientGuide with {}", guideName);
        if (GameData.getGuideTriggerDataStringMap().get(guideName) != null) {
            // if should handle by open state, dont send packet here
            // not entirely sure what return value is about
            // probably not needing this check statement here since the value comes from
            // the lua script
            return 1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowClientGuideNotify(guideName));
        return 0;
    }

    @Override
    public int showClientTutorial(@NotNull GroupEventLuaContext context, int tutorialId, @NotNull List<Integer> uidList) {
        return 0;
    }

    @Override
    public int showCommonPlayerTips(@NotNull GroupEventLuaContext context, int type, @NotNull List<String> keys) {
        return 0;
    }

    @Override
    public int sendShowCommonTipsToClient(GroupEventLuaContext context, @NotNull String title, @NotNull String content, int closeTime) {
        logger.debug("[LUA] Call sendShowCommonTipsToClient with {}, {}, {}", title, content, closeTime);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowCommonTipsNotify(title, content, closeTime));
        return 0;
    }

    @Override
    public int sendCloseCommonTipsToClient(GroupEventLuaContext context) {
        logger.debug("[LUA] Call unimplemented sendCloseCommonTipsToClient");
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCloseCommonTipsNotify());
        return 0;
    }
}
