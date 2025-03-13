package emu.grasscutter.game.entity.create_config;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.binout.config.ConfigEntityGadget;
import emu.grasscutter.data.excels.GadgetData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.entity.gadget.content.GadgetContent;
import emu.grasscutter.game.entity.gadget.platform.BaseRoute;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.CampTargetType;
import emu.grasscutter.game.world.SpawnDataEntry;
import emu.grasscutter.scripts.EntityControllerScriptManager;
import emu.grasscutter.scripts.data.controller.EntityController;
import emu.grasscutter.utils.Position;
import lombok.*;
import lombok.experimental.Accessors;
import org.anime_game_servers.gi_lua.models.scene.group.SceneBossChest;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGadget;
import org.anime_game_servers.multi_proto.gi.messages.ability.action.AbilityActionCreateGadget;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtCreateGadgetNotify;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.CreateEntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter @Setter @AllArgsConstructor @Accessors(chain = true)
public class CreateGadgetEntityConfig extends CreateEntityConfig<CreateGadgetEntityConfig> {
    private int gadgetId;
    private int campId = 0;
    private int requestedEId = 0;
    private int campType = 0;
    private int roomId = 0;
    private int sceneId = 0;
    private boolean isStartRoute = false;
    private BaseRoute routeConfig = null;
    private GadgetContent content = null;
    private int pointType;
    private Player playerOwner = null;
    private GameEntity<?> owner;
    private boolean isPersistent = false;
    private boolean enableInteract = true;
    private int draftId = 0;
    private Set<Integer> worktopOptions;
    private boolean worktopIsPersistent;
    private int interactId = 0;
    private int gadgetState;
    private int chestDropId;
    private boolean chestShowCutscene;
    private GadgetData gadgetData = null;
    private ConfigEntityGadget configEntityGadget = null;
    private List<Integer> arguments = new ArrayList<>();
    private int targetEntityId = 0;
    private List<Integer> targetEntityIds = new ArrayList<>();
    private GameItem item = null;
    private boolean shareItem = true;
    private EntityController luaController = null;
    private BossChestInfo bossChestInfo = null;


    public CreateGadgetEntityConfig(int gadgetId){
        super(gadgetId);
        this.gadgetId = gadgetId;
        initBaseData();
    }

    public CreateGadgetEntityConfig(GameEntity<?> parent, int gadgetId){
        super(parent);
        this.gadgetId = gadgetId;
        initBaseData();
    }

    public CreateGadgetEntityConfig(CreateEntityInfo requestedConfig, int gadgetId){
        super(requestedConfig);
        this.gadgetId = gadgetId;
        val gadgetInfo = requestedConfig.getEntityCreateInfo() instanceof CreateEntityInfo.EntityCreateInfo.Gadget ? ((CreateEntityInfo.EntityCreateInfo.Gadget) requestedConfig.getEntityCreateInfo()).getValue() : null;
        // todo should we handle gadgetInfo.borntype?
        if(gadgetInfo!=null && gadgetInfo.getChest() != null){
            this.chestDropId = gadgetInfo.getChest().getChestDropId();
            this.chestShowCutscene = gadgetInfo.getChest().isShowCutscene();
        }
        initBaseData();
    }

    public CreateGadgetEntityConfig(SceneGadget gadget){
        super(gadget);
        this.gadgetId = gadget.getGadgetId();
        this.routeConfig = BaseRoute.fromSceneGadget(gadget);
        this.pointType = gadget.getPointType();
        this.isPersistent = gadget.isPersistent();
        this.draftId = gadget.getDraftId();
        this.enableInteract = gadget.isEnableInteract();
        this.interactId = gadget.getInteractId();
        this.gadgetState = gadget.getState();
        if(gadget.getWorktopConfig() != null) {
            this.worktopOptions = gadget.getWorktopConfig().getInitOptions();
            this.worktopIsPersistent = gadget.getWorktopConfig().isPersistent();
        }
        this.chestDropId = gadget.getChestDropId();
        this.chestShowCutscene = gadget.isShowCutscene();
        this.bossChestInfo = BossChestInfo.fromSceneBossChest(gadget.getBossChest());
        this.arguments = gadget.getArguments();
        this.isStartRoute = gadget.isStartRoute();
        initBaseData();
    }

    public CreateGadgetEntityConfig(EvtCreateGadgetNotify evtCreateGadgetNotify){
        super(evtCreateGadgetNotify);
        this.gadgetId = evtCreateGadgetNotify.getConfigId();
        this.requestedEId = evtCreateGadgetNotify.getEntityId();
        setBornPos(new Position(evtCreateGadgetNotify.getInitPos()));
        setBornRot(new Position(evtCreateGadgetNotify.getInitEulerAngles()));
        this.campId = evtCreateGadgetNotify.getCampId();
        this.campType = evtCreateGadgetNotify.getCampType();
        val targetIds = evtCreateGadgetNotify.getTargetEntityIdList();
        if(targetIds.isEmpty()){
            this.targetEntityIds.addAll(targetIds);
        }
        if(evtCreateGadgetNotify.getTargetEntityId() != 0){
            this.targetEntityId = evtCreateGadgetNotify.getTargetEntityId();
        }
        initBaseData();
    }

    public CreateGadgetEntityConfig(AbilityModifier.AbilityModifierAction action, AbilityActionCreateGadget createGadgetInfo){
        super(action);
        this.gadgetId = action.gadgetID;
        setBornPos(new Position(createGadgetInfo.getPos()));
        setBornRot(new Position(createGadgetInfo.getRot()));
        this.campId = action.campID;
        this.campType = CampTargetType.getTypeByName(action.campTargetType).getValue();
        initBaseData();
    }

    public CreateGadgetEntityConfig(CreateEntityInfo requestedConfig, ItemData itemData){
        this(requestedConfig, itemData, 1);
    }
    public CreateGadgetEntityConfig(CreateEntityInfo requestedConfig, ItemData itemData, int count){
        this(requestedConfig, itemData.getGadgetId());
        this.item = new GameItem(itemData, count);
    }
    public CreateGadgetEntityConfig(ItemData itemData, int count){
        super(itemData);
        this.gadgetId = itemData.getGadgetId();
        this.item = new GameItem(itemData, count);
        initBaseData();
    }
    public CreateGadgetEntityConfig(SpawnDataEntry spawnDataEntry){
        super(spawnDataEntry);
        this.gadgetId = spawnDataEntry.getGadgetId();
        if (spawnDataEntry.getGadgetState() > 0) {
            this.gadgetState = spawnDataEntry.getGadgetState();
        }
        initBaseData();
    }

    private void initBaseData(){
        initGadgetData(gadgetId);
        initController();
    }

    private void initGadgetData(int gadgetId){
        this.gadgetData = GameData.getGadgetDataMap().get(gadgetId);
        if (gadgetData == null) {
            return;
        }
        if (gadgetData.getJsonName()!=null) {
            this.configEntityGadget = GameData.getGadgetConfigData().get(gadgetData.getJsonName());
        }
        if(campId == 0){
            campId = gadgetData.getCampID();
        }
    }

    private void initController(){
        if(GameData.getGadgetMappingMap().containsKey(gadgetId)) {
            String controllerName = GameData.getGadgetMappingMap().get(gadgetId).getServerController();
            luaController = EntityControllerScriptManager.getGadgetController(controllerName);
            if(luaController == null) {
                Grasscutter.getLogger().warn("Gadget controller {} not found", controllerName);
            }
        }
    }

    @AllArgsConstructor @Data
    public static class BossChestInfo {
        private int lifeTime;
        private int monsterConfigId;
        private int resin;
        private int takeNum;
        static BossChestInfo fromSceneBossChest(SceneBossChest sceneBossChest){
            if (sceneBossChest == null) {
                return null;
            }
            return new BossChestInfo(sceneBossChest.getLifeTime(), sceneBossChest.getMonsterConfigId(),
                sceneBossChest.getResin(), sceneBossChest.getTakeNum());
        }
    }
}
