package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Identity;

public class DisabledHaddon extends HaddonImpl {
    private final String _name;
    private final Identity identity;

    public DisabledHaddon() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > 0) {
            _name = stack[stack.length - 1].getClassName() + " " + stack[stack.length - 1].getMethodName();
        } else {
            _name = "DisabledHaddon";
        }
        identity = new HaddonIdentity(_name, 0, "0.0.0", "http://example.org");
    }

    @Override
    public void onLoad() {
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

}
