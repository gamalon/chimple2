package com.gamelanlabs.chimple2.monkeys;


/**
 * Bernoulli distribution ERP. Returns 0 (tails) or
 * 1 (heads).
 * 
 * @author BenL
 *
 */
public class ChimpFlip extends Monkey<Integer> {
	protected double weight;
	
	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer generate() {
		if (getRandom().nextDouble() < weight) {
			value = 1;
		} else {
			value = 0;
		}
		return getValue();
	}
	
	/**
	 * Generates a proposal from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer propose() {
		value = 1 - value;
		return getValue();
	}

	/**
	 * Returns negative log-likelihood of the current state.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		if (getValue() == 1) {
			return -Math.log(weight);
		} else {
			return -Math.log(1-weight);
		}
	}
	
	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(Integer v) {
		// Transitions are deterministic.
		return 0;
	}
	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	public Object[] getParams() {
		return new Object[] {weight};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return weight != (double) newparams[0];
	}
	
	/**
	 * Tells the monkey how to make Bananas.
	 * 
	 * Makes a safe copy of the instructions (ie. not
	 * by reference).
	 * 
	 * @param	params		Parameters
	 */
	@Override
	public void setParams(Object... params) {
		weight = (double) params[0];
	}
}
