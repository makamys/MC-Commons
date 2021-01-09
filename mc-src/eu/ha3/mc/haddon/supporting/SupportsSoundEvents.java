package eu.ha3.mc.haddon.supporting;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

public interface SupportsSoundEvents {

    public boolean onSound(ISound sound, String name, SoundManager manager);

}
