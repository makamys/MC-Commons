package eu.ha3.mc.haddon.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

@Mixin(Minecraft.class)
public interface IMinecraft {
    @Accessor("timer")
    Timer getTimer();
}
