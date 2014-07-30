package com.gamelanlabs.chimple2.monkeys;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Discrete distribution ERP. Returns values from 0 to
 * probs.length-1 (the MATLAB wrapper changes this range
 * to 1:probs.length by, well, adding one).
 * 
 * @author BenL
 *
 */
public class ChimpDiscrete extends Monkey<Integer> {
	public double[] probs;
	
	/**
	 * Constructor
	 * 
	 * @param	z	The zookeeper
	 * @param	p	An array of weights (does not have to be
	 * 				normalized)
	 */
	public ChimpDiscrete(Zookeeper z, double[] p) {
		super(z);
		probs = p;
		generate();
	}
	
	/**
	 * Helper function that sums probs[].
	 * 
	 * @return	total	The sum of all entries in probs[]
	 */
	protected double sum() {
		double total = 0;
		for(double prob : probs) {
			total += prob;
		}
		return total;
	}
	
	/**
	 * Generates a value from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer generate() {
		double rand = getRandom().nextDouble()*sum();
		int value = probs.length - 1;
		for(int i = 0; i < probs.length; i++) {
			rand -= probs[i];
			if (rand < 0) {
				value = i;
				break;
			}
		}
		setValue(value);
		return value;
	}
	
	/**
	 * Generates a value from the proposal kernel (ie. from
	 * the given array of weights, minus the weight of
	 * the current value).
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer propose() {
		int value = getValue();
		double sum = sum() - probs[value];
		double rand = getRandom().nextDouble()*sum;
		
		// Initialize with the last element
		if(value != probs.length - 1) {
			value = probs.length - 1;
		} else {
			value = probs.length - 2;
		}
		
		// Draw from the discrete distribution,
		// skipping the current value.
		for(int i = 0; i < probs.length; i++) {
			if(i == getValue()) {
				continue;
			}
			rand -= probs[i];
			if (rand < 0) {
				value = i;
				break;
			}
		}
		setValue(value);
		return value;
	}

	/**
	 * Returns negative log-likelihood of the current state.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		return -Math.log(probs[getValue()]/sum());
	}
	
	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(Integer fromvalue) {
		double total = sum() - probs[fromvalue];
		return -Math.log(probs[getValue()]/total);
	}
	
	/**
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpDiscrete clone() {
		ChimpDiscrete c = new ChimpDiscrete(zookeeper, probs);
		c.setValue(getValue());
		return c;
	}
}
