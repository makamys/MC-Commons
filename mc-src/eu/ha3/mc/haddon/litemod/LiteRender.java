package eu.ha3.mc.haddon.litemod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import com.mumfrey.liteloader.RenderListener;
import com.mumfrey.liteloader.util.ModUtilities;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorRenderer;
import eu.ha3.mc.haddon.supporting.SupportsRenderEvents;

/**
 * Boilerplate to distance Ha3 code from the LiteLoader codebase.
 *
 */
public class LiteRender extends LiteBase implements OperatorRenderer, RenderListener {
	public LiteRender(Haddon haddon) {
		super(haddon);
	}

	@Override
	public void onRender() {
		((SupportsRenderEvents) haddon).onRender();
	}

	@Override
	public void onRenderGui(GuiScreen currentScreen) {
		((SupportsRenderEvents) haddon).onRenderGui(currentScreen);
	}

	@Override
	public void onSetupCameraTransform() {
		((SupportsRenderEvents) haddon).onSetupCameraTransform();
	}

	@Override
	public void addRenderable(Class<?> renderClass, Object renderable) {
		ModUtilities.addRenderer((Class<Entity>)renderClass, (Render) renderable);
	}

}
