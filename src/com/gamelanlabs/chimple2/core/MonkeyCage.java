package com.gamelanlabs.chimple2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.gamelanlabs.chimple2.monkeys.Monkey;

/**
 * Represents a trace, storing Monkeys (which store their own
 * values and parameters), a dictionary of their names, and
 * the internal cost (but not the external cost).
 * 
 * @author BenL
 *
 */
public class MonkeyCage {
	/* Please do not abuse the monkeys. */
	public ArrayList<Monkey<?>> monkeys;
	private final HashMap<String, Monkey<?>> monkeynames;
	
	/**
	 * Energy of internal cost functions. This is from calling addCost()
	 * inside ChimpleProgram::run(), as opposed to the external cost
	 * function which you would use by defining a custom CostFunction subclass.
	 */
	public double internalenergy;
	
	/**
	 * Solvers can attach arbitrary tags to the cages they own.
	 * The format of these tags is defined by the specific Solver.
	 * For example, traceMH-derived solvers need to remember the
	 * name of the Monkey that got mutated.
	 */
	public Object tag;
	
	public MonkeyCage() {
		monkeys = new ArrayList<Monkey<?>>();
		monkeynames = new HashMap<String, Monkey<?>>();
		internalenergy = 0;
		tag = null;
	}
	
	/**
	 * Adds a monkey to the cage.
	 * 
	 * @param 	n	Name of monkey
	 * @param	m	Monkey object to add
	 */
	public void add(String n, Monkey<?> m) {
		monkeys.add(m);
		monkeynames.put(n, m);
	}
	
	/**
	 * Removes a monkey from the cage.
	 * 
	 * @param	m	Monkey object to remove
	 */
	public void del(Monkey<?> m) {
		monkeys.remove(m);
		monkeynames.values().remove(m);
	}
	
	/**
	 * Is Bob here?
	 * 
	 * @param 	n	Name of monkey
	 * @return		Whether or not monkey with given name exists
	 */
	public boolean has(String n) {
		return monkeynames.containsKey(n);
	}
	
	/**
	 * Get me Bob.
	 * 
	 * @param 	n	Name of monkey
	 * @return		Monkey with given name
	 */
	public Monkey<?> get(String n) {
		return monkeynames.get(n);
	}
	
	/**
	 * Get me Bob (the unfriendly version).
	 * 
	 * @param 	n	Index of monkey
	 * @return		Monkey with given name
	 */
	public Monkey<?> get(int n) {
		return monkeys.get(n);
	}
	
	/**
	 * How many monkeys are jumping on the bed?
	 * 
	 * @return	n	Number of monkeys in the cage
	 */
	public int size() {
		return monkeys.size();
	}
	
	/**
	 * Monkey manifest.
	 * 
	 * @return	names	Set of all monkey names.
	 */
	public Set<String> getNames() {
		return monkeynames.keySet();
	}
	
	/**
	 * Clones every monkey in the cage, and clones the physical cage.
	 * 
	 * @return	cage	Cloned MonkeyCage
	 */
	@Override
	public MonkeyCage clone() {
		MonkeyCage newcage = new MonkeyCage();
		for(Entry<String, Monkey<?>> entry : monkeynames.entrySet()) {
			Monkey<?> newmonkey = entry.getValue().clone();
			newcage.add(entry.getKey(), newmonkey);
		}
		return newcage;
	}
}
