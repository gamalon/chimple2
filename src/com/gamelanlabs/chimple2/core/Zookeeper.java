package com.gamelanlabs.chimple2.core;

import java.util.Iterator;

import com.gamelanlabs.chimple2.monkeys.Monkey;
import com.gamelanlabs.chimple2.util.RandomPlus;

/**
 * The zookeeper keeps track of a cage of monkeys -- it is basically the
 * middle manager between the solver and the monkeys.
 * 
 * Cages are exchangeable. If a program is run with a fresh cage, each monkey will
 * be re-evaluated. If a program is run with a cage that already contains some monkeys,
 * we will reuse an old monkey if the parameter is the same. (See the chimp* methods
 * in ChimpleProgram.)
 * 
 * @author BenL
 * 
 */
public class Zookeeper {
	public MonkeyCage cage;
	public RandomPlus random;
	
	/**
	 * Used by ChimpleProgram to signal an end condition to the solver (if it supports this).
	 * Effect is solver-dependent.
	 */
	public boolean end = false;
	
	/**
	 * Constructor
	 */
	public Zookeeper() {
		cage = new MonkeyCage();
		random = new RandomPlus();
	}
	
	/**
	 * Resets the property "touched" on each monkey.
	 * 
	 * Used to determine which monkeys to remove after regenerating one monkey and re-running the
	 * program (see killUntouched).
	 */
	public void resetTrackers() {
		for(Monkey<?> m : cage.monkeys) {
			m.touched = false;
		}
	}
	
	/**
	 * Kills all monkeys that don't have the "touched" property set.
	 */
	public void killUntouched() {
		Iterator<Monkey<?>> i = cage.monkeys.iterator();
		while(i.hasNext()) {
			if(!i.next().touched) {
				i.remove();
			}
		}
	}
}
