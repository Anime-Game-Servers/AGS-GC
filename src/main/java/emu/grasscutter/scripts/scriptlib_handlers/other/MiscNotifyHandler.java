package emu.grasscutter.scripts.scriptlib_handlers.other;

import emu.grasscutter.Loggers;
import emu.grasscutter.data.GameData;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.ability.PacketPerformOperationNotify;
import emu.grasscutter.server.packet.activity.PacketCommonPlayerTipsNotify;
import emu.grasscutter.server.packet.activity.PacketSetLimitOptimizationNotify;
import emu.grasscutter.server.packet.battle.PacketLuaSetOptionNotify;
import emu.grasscutter.server.packet.scene.notify.*;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.script_lib.handler.other.AssignPlayerShowTemplateReminderParams;
import org.anime_game_servers.gi_lua.script_lib.handler.other.AssignPlayerUidOpNotifyParams;
import org.anime_game_servers.gi_lua.script_lib.handler.other.ScenePlaySoundParams;
import org.anime_game_servers.multi_proto.gi.messages.battle.LuaOptionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class MiscNotifyHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.MiscNotifyScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int notifyAllPlayerPerformOperation(@NotNull GroupEventLuaContext context, int teamEntityId, int type, int effectIndex, @NotNull Vector hunterPos, @NotNull Vector hunterRot) {
        logger.debug("[LUA] Call setEnvironmentEffectState with {} {} {} {} {}", teamEntityId, type, effectIndex, hunterPos, hunterRot);
        val packet = new PacketPerformOperationNotify(teamEntityId,effectIndex, type, hunterPos, hunterRot);
        context.getSceneScriptManager().getScene().broadcastPacket(packet);
        return 0;
    }

    @Override
    public int setEnvironmentEffectState(@NotNull GroupEventLuaContext context, int index, @NotNull String key, @NotNull List<Float> floatParams, @NotNull List<Integer> intParams) {
        logger.debug("[LUA] Call setEnvironmentEffectState with {} {} {} {}", index, key, floatParams, intParams);
        val packet = new PacketLuaEnvironmentEffectNotify(index, key,floatParams, intParams);
        context.getSceneScriptManager().getScene().broadcastPacket(packet);
        return 0;
    }

    @Override
    public int setLimitOptimization(@NotNull GroupEventLuaContext context, int uid, boolean isLimitOptimization) {
        logger.debug("[LUA] Call setLimitOptimization with {} {}", uid, isLimitOptimization);
        val packet = new PacketSetLimitOptimizationNotify(isLimitOptimization);
        context.getSceneScriptManager().getScene().sendPacketTargeted(packet, uid);
        return 0;
    }

    @Override
    public int setPlayerInteractOption(@NotNull GroupEventLuaContext context, @NotNull String key) {
        logger.debug("[LUA] Call setPlayerInteractOption with {}", key);
        val packet = new PacketLuaSetOptionNotify(key, LuaOptionType.LUA_OPTION_PLAYER_INPUT);
        context.getSceneScriptManager().getScene().broadcastPacket(packet);
        return 0;
    }

    @Override
    public int assignPlayerUidOpNotify(@NotNull GroupEventLuaContext context, @NotNull AssignPlayerUidOpNotifyParams params) {
        logger.debug("[LUA] Call assignPlayerUidOpNotify with {}", params);
        val packet = new PacketNormalUidOpNotify(params.getDuration(), params.getParamIndex(), params.getParamList(), params.getParamUidList());
        context.getSceneScriptManager().getScene().sendPacketTargeted(packet, params.getTargetUidList());
        return 0;
    }

    @Override
    public int scenePlaySound(@NotNull GroupEventLuaContext context, @NotNull ScenePlaySoundParams soundInfo) {
        logger.debug("[LUA] Call ScenePlaySound with {}", soundInfo);
        val soundName = soundInfo.getSoundName();
        val playType = soundInfo.getPlayType();
        val playPosition = soundInfo.getPlayPos();

        val scene = context.getSceneScriptManager().getScene();
        val packet = new PacketScenePlayerSoundNotify(new Position(playPosition), soundName, playType);
        if (soundInfo.isBroadcast())
            scene.broadcastPacket(packet);
        else if (context.uid() != 0) {
            context.getSceneScriptManager().getScene().sendPacketTargeted(packet, context.uid());
        } else {
            scene.sendPacketHost(packet);
        }
        return 0;
    }

    @Override
    public int sendServerMessageByLuaKey(@NotNull GroupEventLuaContext context, @NotNull String messageKey, @NotNull List<Integer> targets) {
        logger.debug("[LUA] Call sendServerMessageByLuaKey with {} {}", messageKey, targets);
        // todo move to resource
        val index = switch (messageKey){
            case "GUEST_CHALLENGING" -> 1;
            case "LEVEL_NOT_SETTLED" -> 2;
            case "LEVEL_SETTLING" -> 3;
            case "HOST_ONLY_CHALLENGE" -> 4;
            case "PLAYER_IN_CHALLENGE" -> 5;
            case "ROGUE_FREE_TRIGGER_RUNE" -> 6;
            case "ROGUE_FREE_REFRESH_CARD" -> 7;
            case "ROGUE_DISABLE_HALO" -> 8;
            case "HOMEOWRLD_DUPLICATE_GALLERY" -> 9;
            default -> -1;
        };

        if(index == -1){
            return -1;
        }
        val packet = new PacketServerMessageNotify(index);
        context.getSceneScriptManager().getScene().sendPacketTargeted(packet, targets);
        return 0;
    }

    @Override
    public int showReminderByUid(@NotNull GroupEventLuaContext context, @NotNull List<Integer> uidList, int reminderId) {
        logger.debug("[LUA] Call showReminderByUid with {} {}", uidList, reminderId);
        val packet = new PacketDungeonShowReminderNotify(reminderId);
        context.getSceneScriptManager().getScene().sendPacketTargeted(packet, uidList);
        return 0;
    }

    @Override
    public int stopReminder(@NotNull GroupEventLuaContext context, int reminderId) {
        logger.debug("[LUA] Call stopReminder with {}", reminderId);
        val packet = new PacketStopReminderNotify(reminderId);
        context.getSceneScriptManager().getScene().broadcastPacket(packet);
        return 0;
    }

    @Override
    public int showReminder(GroupEventLuaContext context, int reminderId) {
        logger.debug("[LUA] Call showReminder with {}", reminderId);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketDungeonShowReminderNotify(reminderId));
        return 0;
    }

    @Override
    public int showReminderRadius(@NotNull GroupEventLuaContext context, int reminderId, @NotNull Vector position, int radius) {
        logger.debug("[LUA] Call unimplemented showReminderRadius with {} {} {}", reminderId, position, radius);
        // todo get players in range and send PacketDungeonShowReminderNotify
        return 0;
    }

    @Override
    public int showTemplateReminder(@NotNull GroupEventLuaContext context, int reminderId, @NotNull List<Integer> timerInfo) {
        logger.debug("[LUA] Call showTemplateReminder with {} {}", reminderId, timerInfo);
        val packet = new PacketShowTemplateReminderNotify(reminderId, timerInfo);
        context.getSceneScriptManager().getScene().broadcastPacket(packet);
        return 0;
    }

    @Override
    public int assignPlayerShowTemplateReminder(@NotNull GroupEventLuaContext context, int reminderId, @NotNull AssignPlayerShowTemplateReminderParams params) {
        logger.debug("[LUA] Call assignPlayerShowTemplateReminder with {} {}", reminderId, params);
        val packet = new PacketShowTemplateReminderNotify(reminderId, params.getParamVec(), params.getParamUidVec(), params.isNeedCache());
        context.getSceneScriptManager().getScene().sendPacketTargeted(packet, params.getUidVec());
        return 0;
    }

    @Override
    public int revokePlayerShowTemplateReminder(@NotNull GroupEventLuaContext context, int reminderId, @NotNull List<Integer> uidList) {
        logger.debug("[LUA] Call revokePlayerShowTemplateReminder with {} {}", reminderId, uidList);
        val packet = new PacketShowTemplateReminderNotify(reminderId, true);
        context.getSceneScriptManager().getScene().sendPacketTargeted(packet, uidList);
        return 0;
    }

    @Override
    public int showClientGuide(@NotNull GroupEventLuaContext context, @NotNull String guideName) {
        logger.debug("[LUA] Call showClientGuide with {}", guideName);
        // TODO official doesn't seem to check this
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
        logger.debug("[LUA] Call showClientTutorial with {} {}", tutorialId, uidList);
        val packet = new PacketShowClientTutorialNotify(tutorialId);
        if(uidList.isEmpty()){
            return context.getSceneScriptManager().getScene().sendPacketHost(packet) ? 0 : -1;
        } else {
            context.getSceneScriptManager().getScene().sendPacketTargeted(packet, uidList);
        }
        return 0;
    }

    @Override
    public int showCommonPlayerTips(@NotNull GroupEventLuaContext context, int type, @NotNull List<String> keys) {
        logger.debug("[LUA] Call showCommonPlayerTips with {} {}", type, keys);
        val packet = new PacketCommonPlayerTipsNotify(type, keys);
        return context.getSceneScriptManager().getScene().sendPacketHost(packet) ? 0 : -1;
    }

    @Override
    public int sendShowCommonTipsToClient(GroupEventLuaContext context, @NotNull String title, @NotNull String content, int closeTime) {
        logger.debug("[LUA] Call sendShowCommonTipsToClient with {}, {}, {}", title, content, closeTime);
        val packet = new PacketShowCommonTipsNotify(title, content, closeTime);
        context.getSceneScriptManager().getScene().sendPacketHost(packet);
        return 0;
    }

    @Override
    public int sendCloseCommonTipsToClient(GroupEventLuaContext context) {
        logger.debug("[LUA] Call sendCloseCommonTipsToClient");
        val packet = new PacketCloseCommonTipsNotify();
        context.getSceneScriptManager().getScene().sendPacketHost(packet);
        return 0;
    }
}
