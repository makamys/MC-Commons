package eu.ha3.mc.haddon.implem;

import java.io.File;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.lwjgl.input.Keyboard;

import eu.ha3.mc.haddon.Client;
import eu.ha3.mc.haddon.Utility;

public abstract class HaddonUtilityImpl implements Utility {
    private static final int WORLD_HEIGHT = 256;
    private static final NullInstantiator NULL_INSTANTIATOR = new NullInstantiator();

    private static final HaddonClientImpl client = new HaddonClientImpl();

    protected long ticksRan;
    protected File mcFolder;
    protected File modsFolder;

    /**
     * Initialise reflection (Call the static constructor)
     */
    public HaddonUtilityImpl() {

    }

    @Override
    public boolean isPresent(String className) {
        return NULL_INSTANTIATOR.lookupClass(className) != null;
    }

    @Override
    public <E> Instantiator<E> getInstantiator(String className, Class<?>... types) {
        return NULL_INSTANTIATOR.getOrCreate(className, types);
    }

    @Override
    public int getWorldHeight() {
        return WORLD_HEIGHT;
    }

    @Override
    public Object getCurrentScreen() {
        return client.unsafe().currentScreen;
    }

    @Override
    public boolean isCurrentScreen(final Class<?> classtype) {
        Object current = getCurrentScreen();
        if (classtype == null)
            return current == null;
        if (current == null)
            return false;
        return classtype.isInstance(current);
    }

    @Override
    public void displayScreen(Object screen) {
        client.unsafe().displayGuiScreen((GuiScreen) screen);
    }

    @Override
    public void closeCurrentScreen() {
        displayScreen(null);
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void pauseSounds(boolean pause) {
        if (pause) {
            client.unsafe().getSoundHandler().pauseSounds();
        } else {
            client.unsafe().getSoundHandler().resumeSounds();
        }
    }

    @Override
    public boolean isGamePaused() {
        Object current = getCurrentScreen();
        return current != null && (((GuiScreen) current).doesGuiPauseGame() && isSingleplayer());
    }

    @Override
    public boolean isSingleplayer() {
        return client.unsafe().isSingleplayer() && !client.unsafe().getIntegratedServer().getPublic();
    }

    @Override
    public void printChat(Object... args) {
        if (client.getPlayer() == null)
            return;

        ChatComponentText message = new ChatComponentText("");
        ChatStyle style = null;
        for (Object o : args) {
            if (o instanceof EnumChatFormatting) {
                EnumChatFormatting code = (EnumChatFormatting) o;
                if (style == null) {
                    style = new ChatStyle();
                }
                switch (code) {
                case OBFUSCATED:
                    style.setObfuscated(true);
                    break;
                case BOLD:
                    style.setBold(true);
                    break;
                case STRIKETHROUGH:
                    style.setStrikethrough(true);
                    break;
                case UNDERLINE:
                    style.setUnderlined(true);
                    break;
                case ITALIC:
                    style.setItalic(true);
                    break;
                case RESET:
                    style = null;
                    break;
                default:
                    style.setColor(code);
                }
            } else if (o instanceof ClickEvent) {
                if (style == null)
                    style = new ChatStyle();
                style.setChatClickEvent((ClickEvent) o);
            } else if (o instanceof HoverEvent) {
                if (style == null)
                    style = new ChatStyle();
                style.setChatHoverEvent((HoverEvent) o);
            } else if (o instanceof IChatComponent) {
                if (style != null) {
                    ((IChatComponent) o).setChatStyle(style);
                    style = null;
                }
                message.appendSibling((IChatComponent) o);
            } else if (o instanceof ChatStyle) {
                if (!((ChatStyle) o).isEmpty()) {
                    if (style != null) {
                        inheritFlat((ChatStyle) o, style);
                    }
                    style = ((ChatStyle) o);
                }
            } else {
                IChatComponent line = o instanceof String ? new ChatComponentTranslation((String) o)
                        : new ChatComponentText(String.valueOf(o));
                if (style != null) {
                    line.setChatStyle(style);
                    style = null;
                }
                message.appendSibling(line);
            }
        }

        client.getPlayer().addChatMessage(message); // XXX is this correct?
    }

    /**
     * Merges the given child ChatStyle into the given parent preserving
     * hierarchical inheritance.
     *
     * @param parent The parent to inherit style information
     * @param child  The child style who's properties will override those in the
     *               parent
     */
    private void inheritFlat(ChatStyle parent, ChatStyle child) {
        if ((parent.getBold() != child.getBold()) && child.getBold()) {
            parent.setBold(true);
        }
        if ((parent.getItalic() != child.getItalic()) && child.getItalic()) {
            parent.setItalic(true);
        }
        if ((parent.getStrikethrough() != child.getStrikethrough()) && child.getStrikethrough()) {
            parent.setStrikethrough(true);
        }
        if ((parent.getUnderlined() != child.getUnderlined()) && child.getUnderlined()) {
            parent.setUnderlined(true);
        }
        if ((parent.getObfuscated() != child.getObfuscated()) && child.getObfuscated()) {
            parent.setObfuscated(true);
        }

        Object temp;
        if ((temp = child.getColor()) != null) {
            parent.setColor((EnumChatFormatting) temp);
        }
        if ((temp = child.getChatClickEvent()) != null) {
            parent.setChatClickEvent((ClickEvent) temp);
        }
        if ((temp = child.getChatHoverEvent()) != null) {
            parent.setChatHoverEvent((HoverEvent) temp);
        }
        /*
         * if ((temp = child.getInsertion()) != null) { // 1.12.2 only?
         * parent.setInsertion((String)temp); }
         */
    }

    @Override
    public boolean areKeysDown(int... args) {
        for (int arg : args) {
            if (!Keyboard.isKeyDown(arg))
                return false;
        }
        return true;
    }

    private ScaledResolution drawString_scaledRes = null;
    private int drawString_screenWidth;
    private int drawString_screenHeight;
    private int drawString_textHeight;

    @Override
    public void prepareDrawString() {
        drawString_scaledRes = new ScaledResolution(client.unsafe(), client.unsafe().displayWidth,
                client.unsafe().displayHeight);
        drawString_screenWidth = drawString_scaledRes.getScaledWidth();
        drawString_screenHeight = drawString_scaledRes.getScaledHeight();
        drawString_textHeight = client.getFontRenderer().FONT_HEIGHT;
    }

    @Override
    public void drawString(String text, float px, float py, int offx, int offy, char alignment, int cr, int cg, int cb,
            int ca, boolean hasShadow) {
        if (drawString_scaledRes == null)
            prepareDrawString();

        FontRenderer font = client.getFontRenderer();

        int xPos = (int) Math.floor(px * drawString_screenWidth) + offx;
        int yPos = (int) Math.floor(py * drawString_screenHeight) + offy;

        if (alignment == '2' || alignment == '5' || alignment == '8') {
            xPos = xPos - font.getStringWidth(text) / 2;
        } else if (alignment == '3' || alignment == '6' || alignment == '9') {
            xPos = xPos - font.getStringWidth(text);
        }

        if (alignment == '4' || alignment == '5' || alignment == '6') {
            yPos = yPos - drawString_textHeight / 2;
        } else if (alignment == '1' || alignment == '2' || alignment == '3') {
            yPos = yPos - drawString_textHeight;
        }

        int color = ca << 24 | cr << 16 | cg << 8 | cb;

        if (hasShadow) {
            font.drawStringWithShadow(text, xPos, yPos, color);
        } else {
            font.drawString(text, xPos, yPos, color);
        }
    }

    @Override
    public File getModsFolder() {
        if (modsFolder == null) {
            modsFolder = new File(getMcFolder(), "mods");
        }
        return modsFolder;
    }

    @Override
    public File getMcFolder() {
        if (mcFolder == null) {
            mcFolder = client.unsafe().mcDataDir;
        }
        return mcFolder;
    }
}
