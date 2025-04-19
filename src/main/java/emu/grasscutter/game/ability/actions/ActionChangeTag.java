package emu.grasscutter.game.ability.actions;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.ChangeTag)
public class ActionChangeTag extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        target.changeTag(action.tag, action.isAdd);
        return true;
    }
}
