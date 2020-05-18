package eu.ha3.mc.haddon.supporting.event;

import net.minecraft.block.Block;

public class BlockChangeEvent {
    public int x;
    public int y;
    public int z;
    public Block oldBlock;
    public Block newBlock;
    
    public BlockChangeEvent(int x, int y, int z, Block oldBlock, Block newBlock) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }
}