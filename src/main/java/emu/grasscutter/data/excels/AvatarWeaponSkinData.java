package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.props.WeaponType;
import lombok.Getter;

@Getter
@ResourceType(name = "AvatarWeaponSkinExcelConfigData.json")
public class AvatarWeaponSkinData extends GameResource {
    private int weaponSkinId;
    private int itemId;
    private WeaponType weaponType;

    @Override
	public int getId() {
		return this.weaponSkinId;
	}
}
