package eu.ha3.mc.convenience;

public class Ha3KeyHolding implements Ha3KeyActions {
    private final Ha3HoldActions holdActions;
    private final int tippingPoint;

    private boolean isHolding;

    public Ha3KeyHolding(Ha3HoldActions holdActions, int tippingPoint) {
        this.holdActions = holdActions;
        this.tippingPoint = tippingPoint;
    }

    @Override
    public void doBefore() {
        holdActions.beginPress();
    }

    @Override
    public void doDuring(int curTime) {
        if (curTime >= tippingPoint && !isHolding) {
            isHolding = true;
            holdActions.beginHold();
        }
    }

    @Override
    public void doAfter(int curTime) {
        if (curTime < tippingPoint) {
            holdActions.shortPress();
        } else if (isHolding) {
            isHolding = false;
            holdActions.endHold();
        }
        holdActions.endPress();
    }

}