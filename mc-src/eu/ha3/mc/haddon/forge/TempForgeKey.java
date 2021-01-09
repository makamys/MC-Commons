package eu.ha3.mc.haddon.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.client.registry.ClientRegistry;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorKeyer;

public class TempForgeKey extends ForgeBase implements OperatorKeyer {
    public TempForgeKey(Haddon haddon) {
        super(haddon);
    }

    @Override
    public void addKeyBinding(KeyBinding bind) {
        ClientRegistry.registerKeyBinding(bind);
    }

    @Override
    public void removeKeyBinding(KeyBinding bind) {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils
                .removeElement(Minecraft.getMinecraft().gameSettings.keyBindings, bind);
    }
}
