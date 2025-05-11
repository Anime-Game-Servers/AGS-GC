
package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.world.SceneGroupInstance;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.gi_lua.models.constants.FlowSuiteOperatePolicy;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.RefreshGroupParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class GroupManagementScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.GroupManagementScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int goToGroupSuite(@NotNull GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call GoToGroupSuite with {},{}",
            groupId,suite);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val scriptManager = context.getSceneScriptManager();
        SceneGroup group = getGroupOrCurrent(context, groupId);
        SceneGroupInstance groupInstance = scriptManager.getGroupInstanceById(actualGroupId);
        if (group == null || groupInstance == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

		/*for(var suiteItem : group.suites){
			if(suiteData == suiteItem){
				continue;
			}
			this.getSceneScriptManager().removeGroupSuite(group, suiteItem);
		}*/
        if(groupInstance.getActiveSuiteId() == 0 || groupInstance.getActiveSuiteId() != suite) {
            groupInstance.getDeadEntities().clear();
            scriptManager.addGroupSuite(groupInstance, suiteData);
            groupInstance.setActiveSuiteId(suite);
        }

        return 0;
    }

    @Override
    public int getGroupSuite(@NotNull GroupEventLuaContext context, int groupId) {
        //logger.warn("[LUA] Call GetGroupSuite with {}", groupID);
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        var instance = context.getSceneScriptManager().getGroupInstanceById(actualGroupId);
        if(instance != null) return instance.getActiveSuiteId();
        return 0;
    }


    @Override
    public int addExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call AddExtraGroupSuite with {},{}",
            groupId,suite);
        val scriptManager = context.getSceneScriptManager();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        SceneGroup group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 1;
        }

        SceneGroupInstance groupInstance = scriptManager.getGroupInstanceById(actualGroupId);
        if (groupInstance == null) {
            return 1;
        }

        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            logger.warn("trying to get suite that doesn't exist: {} {}", actualGroupId, suite);
            return 1;
        }
        scriptManager.addGroupSuite(groupInstance, suiteData);

        return 0;
    }

    @Override
    public int removeExtraGroupSuite(@NotNull GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call RemoveExtraGroupSuite with {},{}",
            groupId,suite);

        SceneGroup group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

        context.getSceneScriptManager().removeGroupSuite(group, suiteData);

        return 0;
    }

    @Override
    public int killExtraGroupSuite(@NotNull GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call KillExtraGroupSuite with {},{}",
            groupId,suite);

        SceneGroup group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

        context.getSceneScriptManager().killGroupSuite(group, suiteData);

        return 0;
    }

    /* Flow suite */

    @Override
    public int goToFlowSuite(@NotNull GroupEventLuaContext context, int groupId, int suite) {
        return handleUnimplemented(groupId, suite);
    }

    @Override
    public int setFlowSuite(@NotNull GroupEventLuaContext context, int groupId, int suite) {
        return handleUnimplemented(groupId, suite);
    }

    @Override
    public int addExtraFlowSuite(@NotNull GroupEventLuaContext context, int groupId, int suiteId, @NotNull FlowSuiteOperatePolicy flowSuitePolicy) {
        return handleUnimplemented(groupId, suiteId, flowSuitePolicy.name());
    }

    @Override
    public int removeExtraFlowSuite(@NotNull GroupEventLuaContext context, int groupId, int suiteId, @NotNull FlowSuiteOperatePolicy flowSuitePolicy) {
        return handleUnimplemented(groupId, suiteId, flowSuitePolicy.name(), flowSuitePolicy);
    }

    @Override
    public int killExtraFlowSuite(@NotNull GroupEventLuaContext context, int groupId, int suiteId, @NotNull FlowSuiteOperatePolicy flowSuitePolicy) {
        return handleUnimplemented(groupId, suiteId, flowSuitePolicy.name(), flowSuitePolicy);
    }

    /* group link bundle */

    @Override
    public int activateGroupLinkBundle(@NotNull GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int activateGroupLinkBundleByBundleId(@NotNull GroupEventLuaContext context, int bundleId) {
        return handleUnimplemented(bundleId);
    }

    @Override
    public int deactivateGroupLinkBundle(@NotNull GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int deactivateGroupLinkBundleByBundleId(@NotNull GroupEventLuaContext context, int bundleId) {
        return handleUnimplemented(bundleId);
    }

    @Override
    public int finishGroupLinkBundle(@NotNull GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    /* group variables*/
    private static int getGroupVariableValue(SceneScriptManager sceneScriptManager, int groupId, String varName){
        val variables = sceneScriptManager.getVariables(groupId);
        return variables != null ? variables.getOrDefault(varName, 0) : 0;
    }

    private static int modifyGroupVariableValue(SceneScriptManager sceneScriptManager, int groupId, String varName, int value,
                                                boolean isSet){
        val variables = sceneScriptManager.getVariables(groupId);
        if (variables == null) {
            logger.warn("[LUA] trying to modify variables of unloaded/unknown group {}", groupId);
            return 1;
        }

        val old = variables.getOrDefault(varName, value);
        val newValue = isSet ? value : old + value;
        variables.put(varName, newValue);
        sceneScriptManager.callEvent(
            new ScriptArgs(groupId, EventType.EVENT_VARIABLE_CHANGE, newValue, old)
                .setEventSource(varName)
        );
        return 0;
    }

    @Override
    public int createGroupVariable(GroupEventLuaContext context, @NotNull String varName, int value){
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return modifyGroupVariableValue(context.getSceneScriptManager(), groupId, varName, value, true);
    }

    @Override
    public int getGroupVariableValue(GroupEventLuaContext context, @NotNull String varName) {
        logger.debug("[LUA] Call GetGroupVariableValue with {}", varName);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return getGroupVariableValue(context.getSceneScriptManager(), groupId, varName);
    }

    @Override
    public int getGroupVariableValueByGroup(@NotNull GroupEventLuaContext context, @NotNull String name, int groupId) {
        logger.debug("[LUA] Call GetGroupVariableValueByGroup with {},{}",
            name,groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return getGroupVariableValue(context.getSceneScriptManager(), actualGroupId, name);
    }

    @Override
    public int setGroupVariableValue(GroupEventLuaContext context, @NotNull String varName, int value) {
        logger.debug("[LUA] Call SetGroupVariableValue with {},{}",
            varName, value);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return modifyGroupVariableValue(context.getSceneScriptManager(), groupId, varName, value, true);
    }

    @Override
    public int setGroupVariableValueByGroup(@NotNull GroupEventLuaContext context, @NotNull String key, int value, int groupId) {
        logger.debug("[LUA] Call SetGroupVariableValueByGroup with {},{},{}",
            key,value,groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return modifyGroupVariableValue(context.getSceneScriptManager(), actualGroupId, key, value, true);
    }

    @Override
    public int changeGroupVariableValue(GroupEventLuaContext context, @NotNull String varName, int value) {
        logger.debug("[LUA] Call ChangeGroupVariableValue with {},{}",
            varName, value);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return modifyGroupVariableValue(context.getSceneScriptManager(), groupId, varName, value, false);
    }

    @Override
    public int changeGroupVariableValueByGroup(@NotNull GroupEventLuaContext context, @NotNull String name, int value, int groupId) {
        logger.debug("[LUA] Call ChangeGroupVariableValueByGroup with {},{}",
            name,groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return modifyGroupVariableValue(context.getSceneScriptManager(), actualGroupId, name, value, false);
    }

    /* Group temp value */
    @Override
    public int setGroupTempValue(@NotNull GroupEventLuaContext context, @NotNull String name, int value, int groupId) {
        return handleUnimplemented(name, value, groupId);
    }

    @Override
    public int getGroupTempValue(@NotNull GroupEventLuaContext context, @NotNull String name, int groupId) {
        return handleUnimplemented(name, groupId);
    }

    @Override
    public int changeGroupTempValue(@NotNull GroupEventLuaContext context, @NotNull String name, int diff, int groupId) {
        return handleUnimplemented(name, diff, groupId);
    }

    /* misc */
    @Override
    public int refreshGroup(@NotNull GroupEventLuaContext context, @NotNull RefreshGroupParams params) {
        logger.debug("[LUA] Call RefreshGroup with {}", params);
        // Kill and Respawn?
        val groupId = getGroupIdOrCurrentId(context, params.getGroupId());
        val suite = params.getSuiteId();
        // TODO handle other optional params

        SceneGroupInstance groupInstance = context.getSceneScriptManager().getGroupInstanceById(groupId);

        if (groupInstance == null) {
            logger.warn("[LUA] trying to refresh unloaded group {}", groupId);
            return 1;
        }

        context.getSceneScriptManager().refreshGroup(groupInstance, suite, false);

        return 0;
    }

    @Override
    public int setGroupReplaceable(@NotNull GroupEventLuaContext context, int groupId, boolean value) {
        logger.warn("[LUA] Call SetGroupReplaceable with {} {}", groupId, value);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        var group = context.getSceneScriptManager().getCachedGroupInstanceById(actualGroupId);
        if(group != null && group.isReplaceable() != null) {
            group.setReplaceable(value);
            return 0;
        }
        return 1;
    }

    @Override
    public int createGroupTrigger(@NotNull GroupEventLuaContext context, @NotNull String triggerName) {
        return handleUnimplemented(triggerName);
    }

    @Override
    public int setGroupDead(@NotNull GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int isGroupRegisteredInCurScene(@NotNull GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int unfreezeGroupLimit(@NotNull GroupEventLuaContext context, int forceId) {
        return handleUnimplemented(forceId);
    }

    /* group lua execution*/
    @Override
    public int executeActiveGroupLua(@NotNull GroupEventLuaContext groupEventLuaContext, int groupId, @NotNull String functionName,
                                     @NotNull List<Integer> callParams) {
        return handleUnimplemented(groupId, functionName, callParams);
    }

    @Override
    public int executeGroupLua(@NotNull GroupEventLuaContext groupEventLuaContext, int groupId, @NotNull String functionName,
                               @NotNull List<Integer> callParams) {
        return handleUnimplemented(groupId, functionName, callParams);
    }
}
