package emu.grasscutter.game.entity.create_config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.anime_game_servers.gi_lua.models.scene.group.SceneRegion;

@Getter @Setter @AllArgsConstructor @Accessors(chain = true)
public class CreateRegionEntityConfig extends CreateEntityConfig<CreateRegionEntityConfig> {

    public CreateRegionEntityConfig(SceneRegion region){
        super(region);
    }
}
