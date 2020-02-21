package eu.ha3.mc.haddon.forge;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.service.MixinService;

import java.util.Map;

// Based on https://github.com/zeroeightysix/KAMI/blob/master/src/main/java/me/zeroeightsix/kami/mixin/MixinLoaderForge.java

public class MixinLoaderForge implements IFMLLoadingPlugin {

    private static boolean isObfuscatedEnvironment = false;

    private static final String MIXIN_CORE_JSON_NAME = "haddon_core.mixin.json";
    private static final String MIXIN_HADDON_JSON_NAME = "haddon.mixin.json";
    
    private static boolean initializedSuccessfully;
    
    public MixinLoaderForge() {
        System.out.println("Initializing Haddon mixins");
        
        if(MixinService.getService().getResourceAsStream(MIXIN_CORE_JSON_NAME) == null) {
            System.out.println("Couldn't find Haddon core mixin (" + MIXIN_CORE_JSON_NAME + "). The Haddon mod will be disabled!");
            initializedSuccessfully = false;
        } else {
            MixinBootstrap.init();
            Mixins.addConfiguration(MIXIN_CORE_JSON_NAME);
            
            boolean addonHasMixin = MixinService.getService().getResourceAsStream(MIXIN_HADDON_JSON_NAME) != null;
            
            if(addonHasMixin) {
                Mixins.addConfiguration(MIXIN_HADDON_JSON_NAME);
            }
            
            MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
            
            initializedSuccessfully = true;
            
            System.out.println("Initialized Haddon mixins for core" + (addonHasMixin ? " and addon" : ""));
        }
    }
    
    public static boolean hasInitializedSuccessfully() {
        return initializedSuccessfully;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (boolean) (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
