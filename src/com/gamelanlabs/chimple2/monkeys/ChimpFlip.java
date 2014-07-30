package com.gamelanlabs.chimple2.monkeys;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Bernoulli distribution ERP. Returns 0 (tails) or
 * 1 (heads).
 * 
 * @author BenL
 *
 */
public class ChimpFlip extends Monkey<Integer> {
	public double weight;
	
	/**
	 * Constructor
	 * 
	 * @param	z	The zookeeper
	 * @param	w	The coin weight (0.0 to 1.0)
	 */
	public ChimpFlip(Zookeeper z, double w) {
		super(z);
		weight = w;
		generate();
	}
	
	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer generate() {
		if (getRandom().nextDouble() < weight) {
			setValue(1);
		} else {
			setValue(0);
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
		setValue(1-getValue());
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
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpFlip clone() {
		ChimpFlip c = new ChimpFlip(zookeeper, weight);
		c.setValue(getValue());
		return c;
	}
}
