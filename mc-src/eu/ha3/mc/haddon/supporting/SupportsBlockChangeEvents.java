package eu.ha3.mc.haddon.supporting;

import eu.ha3.mc.haddon.supporting.event.BlockChangeEvent;

public interface SupportsBlockChangeEvents {

    enum ClickType {
        RIGHT_CLICK_AIR, LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK
    }

    void onBlockChanged(BlockChangeEvent event);

}
