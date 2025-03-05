package emu.grasscutter.game.entity;

import emu.grasscutter.game.entity.create_config.CreateNpcEntityConfig;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;
import org.anime_game_servers.gi_lua.models.scene.group.SceneNPC;

import java.util.List;
@Getter
public class EntityNPC extends GameEntity<CreateNpcEntityConfig> {
    private final int npcId;
    private final int roomId;
    private final int questId;
    @Getter(onMethod = @__(@Override))
    private final Position position;
    @Getter(onMethod = @__(@Override))
    private final Position rotation;
    private final Position bornPos;
    private final Position bornRot;

    public EntityNPC(Scene scene, CreateNpcEntityConfig metaNPC) {
        super(scene, metaNPC);
        this.id = getScene().getWorld().getNextEntityId(EntityIdType.NPC);
        this.npcId = metaNPC.getNpcId();
        this.position = metaNPC.getPos();
        this.rotation = metaNPC.getRot();
        this.bornPos = metaNPC.getBornPos();
        this.bornRot = metaNPC.getBornRot();
        this.roomId = metaNPC.getRoomId();
        this.questId = metaNPC.getQuestId();
    }

    @Override
    public int getEntityTypeId() {
        return npcId;
    }

    @Override
    public Int2FloatMap getFightProperties() {
        return null;
    }

    @Override
    public SceneEntityInfo toProto() {
        val protoBornPos = getBornPos().toProto();
        val protoPos = getPosition().toProto();
        val protoRot = getRotation().toProto();
        val aiInfo = new SceneEntityAiInfo(true, protoBornPos);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, protoBornPos);

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_NPC, getId());
        entityInfo.setMotionInfo(new MotionInfo(protoPos, protoRot, new Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);


        val npc = new SceneNpcInfo(npcId);
        npc.setBlockId(getBlockId());
        npc.setRoomId(getRoomId());
        npc.setQuestId(getQuestId());
        entityInfo.setEntity(new SceneEntityInfo.Entity.Npc(npc));

        return entityInfo;
    }
}
