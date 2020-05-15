package eu.ha3.mc.haddon.supporting;

import net.minecraft.block.Block;

public interface SupportsBlockChangeEvents {
    
    enum ClickType {
        RIGHT_CLICK_AIR,
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK
    }
    
    void onBlockChanged(int x, int y, int z, Block oldBlock, Block newBlock);
    
}
