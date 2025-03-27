package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.interfaces.StringAbilityEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.world.World;

import java.util.Collection;

public class EntityTeam extends MetaGameEntity implements StringAbilityEntity {

    private Player player;

    public EntityTeam(Player player) {
        super(player.getScene());
        this.player = player;
        initAbilities();
        this.id = player.getWorld().getNextEntityId(EntityIdType.TEAM);
    }

    @Override
    public AbilityManager getAbilityTargetManager() {
        return getWorld().getHost().getAbilityManager();
    }

    @Override
    public Collection<String> getAbilityData() {
        return GameData.getConfigGlobalCombat().getDefaultAbilities().getDefaultTeamAbilities();
    }

    @Override
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public int getEntityTypeId() {
        return EntityIdType.TEAM.getId();
    }

}
