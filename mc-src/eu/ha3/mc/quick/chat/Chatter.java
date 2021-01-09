package eu.ha3.mc.quick.chat;

import eu.ha3.mc.haddon.Haddon;
import net.minecraft.util.EnumChatFormatting;

/**
 * Chat printer for sending formatted chat messages on behalf of the assigned
 * mod.
 */
public class Chatter {
    private final Haddon mod;
    private final String prefix;

    public Chatter(Haddon mod, String prefix) {
        this.mod = mod;
        this.prefix = prefix;
    }

    public void printChat(Object... args) {
        Object[] dest = new Object[args.length + 2];
        dest[0] = EnumChatFormatting.WHITE;
        dest[1] = prefix;
        System.arraycopy(args, 0, dest, 2, args.length);
        mod.getUtility().printChat(dest);
    }

    public void printChatShort(Object... args) {
        mod.getUtility().printChat(args);
    }
}
