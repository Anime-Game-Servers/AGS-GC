package emu.grasscutter.game.entity.create_config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.anime_game_servers.gi_lua.models.scene.group.SceneNPC;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.CreateEntityInfo;

@Getter @Setter @AllArgsConstructor @Accessors(chain = true)
public class CreateNpcEntityConfig extends CreateEntityConfig<CreateNpcEntityConfig> {
    private int npcId;
    private int roomId = 0;
    private int questId = 0;

    public CreateNpcEntityConfig(SceneNPC npc){
        super(npc);
        this.npcId = npc.getNpc_id();
    }

    public CreateNpcEntityConfig(CreateEntityInfo requestedConfig, int npcID){
        super(requestedConfig);
        this.npcId = npcID;
        this.roomId = requestedConfig.getRoomId();
    }
}
