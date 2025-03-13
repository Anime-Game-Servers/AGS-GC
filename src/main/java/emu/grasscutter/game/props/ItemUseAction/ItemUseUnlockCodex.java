package emu.grasscutter.game.props.ItemUseAction;

import emu.grasscutter.game.props.ItemUseOp;
import emu.grasscutter.server.packet.send.PacketCodexDataUpdateNotify;

public class ItemUseUnlockCodex extends ItemUseInt {
    @Override
    public ItemUseOp getItemUseOp() {
        return ItemUseOp.ITEM_USE_UNLOCK_CODEX;
    }

    public ItemUseUnlockCodex(String[] useParam) {
        super(useParam);
    }

    @Override
    public boolean useItem(UseItemParams params) {
        params.player.getCodex().checkBook(this.i);
        return true;
    }
}
