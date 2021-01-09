package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.Operator;
import eu.ha3.mc.haddon.Utility;

public abstract class HaddonImpl implements Haddon {

    private Utility utility;
    private Operator operator;

    @Override
    public Utility getUtility() {
        return utility;
    }

    @Override
    public void setUtility(Utility util) {
        utility = util;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Operator> T getOperator() {
        return (T) operator;
    }

    @Override
    public void setOperator(Operator op) {
        operator = op;
    }

    /**
     * Convenience shortener for getUtility()
     */
    public Utility util() {
        return getUtility();
    }

    /**
     * Convenience shortener for getCaster()
     */
    public <T extends Operator> T op() {
        return this.<T>getOperator();
    }
}
