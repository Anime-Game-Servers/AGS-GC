package emu.grasscutter.scripts.scriptlib_handlers.player;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.enums.QuestState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class QuestScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.player.QuestScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public int addQuestProgress(GroupEventLuaContext context, String eventNotifyName) {
        logger.debug("[LUA] Call AddQuestProgress with {}",
            eventNotifyName);

        for(var player : context.getSceneScriptManager().getScene().getPlayers()){
            player.getQuestManager().queueEvent(QuestCond.QUEST_COND_LUA_NOTIFY, eventNotifyName);
            player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_LUA_NOTIFY, eventNotifyName);
        }

        return 0;
    }

    @Override
    public QuestState getHostQuestState(GroupEventLuaContext context, int questId) {
        val player = context.getSceneScriptManager().getScene().getWorld().getHost();

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE;
        }

        return quest.getState();
    }

    @Override
    public QuestState getQuestState(GroupEventLuaContext context, int entityId, int questId) {
        val player = context.getSceneScriptManager().getScene().getWorld().getHost();

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE;
        }

        return quest.getState();
    }

    @Override
    public @NotNull QuestState getQuestStateByUid(@NotNull GroupEventLuaContext context, int uid, int questId) {
        val player = context.getSceneScriptManager().getScene().getWorld().getPlayers().stream()
            .filter(p -> p.getUid() == uid)
            .findFirst().orElse(null);

        if(player == null){
            return QuestState.QUEST_STATE_UNKNOWN;
        }

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE;
        }

        return quest.getState();
    }
}
