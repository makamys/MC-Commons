package eu.ha3.mc.haddon;

public interface Haddon {
	/**
	 * Triggered during the addon loading process.
	 */
	void onLoad();

	/**
	 * Returns the utility object dedicated to this haddon.
	 */
	Utility getUtility();

	/**
	 * Sets the utility object dedicated to this haddon.
	 */
	void setUtility(Utility utility);

	/**
	 * Returns the caster object dedicated to this haddon.
	 */
	<T extends Operator> T getOperator();

	/**
	 * Sets the caster object dedicated to this haddon.
	 */
	void setOperator(Operator operator);

	/**
	 * Returns the identity of this Haddon.
	 */
	Identity getIdentity();
}
