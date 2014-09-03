package com.gamelanlabs.chimple2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.gamelanlabs.chimple2.monkeys.Monkey;

/**
 * Represents a trace, storing Monkeys (which store their own
 * values and parameters), a dictionary of their names, and
 * the internal cost (but not the external cost).
 * 
 * @author BenL
 * @author AllenC
 *
 */
public final class MonkeyCage {
	/**
	 * Please do not abuse the monkeys.
	 */
	private final ArrayList<Monkey<?>> monkeys;
	
	/**
	 * Treat them as you would your own children.
	 */
	private final MonkeyMap monkeynames;
	
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
	
	/**
	 * Constructor
	 */
	public MonkeyCage() {
		monkeys = new ArrayList<Monkey<?>>();
		monkeynames = new MonkeyMap();
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
		monkeynames.remove(m);
	}
	
	/**
	 * Is Bob here?
	 * 
	 * @param 	n			Name of monkey
	 * @return	maaaaaybe	Whether or not monkey with given name exists
	 */
	public boolean has(String n) {
		return monkeynames.containsKey(n);
	}
	
	/**
	 * Get me Bob.
	 * 
	 * @param 	n		Name of monkey
	 * @return	bob		Monkey with given name
	 */
	public Monkey<?> get(String n) {
		return monkeynames.get(n);
	}
	
	/**
	 * Get me Bob (the unfriendly version).
	 * 
	 * @param 	n		Index of monkey
	 * @return	bob		Monkey with given name
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
	 * Gets the list of Monkey objects.
	 * 
	 * @return	monkeys
	 */
	public List<Monkey<?>> getList() {
		return monkeys;
	}
	
	/**
	 * Gets all the monkeys that correspond to a given prefix. Performed in place.
	 * 
	 * @param	prefix		All monkeys with this as the prefix will be found
	 * @return	monkeys		The corresponding monkeys
	 */
	public ArrayList<Monkey<?>> getPrefixMonkeys(String prefix) {
		return monkeynames.getPrefixMonkeys(prefix);
	}
	
	/**
	 * Gets all the monkeys that correspond to a given prefix. In this case,
	 * the prefix has been already split up into non-delimited pieces (if there
	 * are delimiters in the pieces, an error will be spit out). Performed in
	 * place.
	 * 
	 * @param	split		An array with all of the pieces of the desired prefix
	 * @return	monkeys		Monkeys corresponding to this sequence of prefixes
	 */
	public ArrayList<Monkey<?>> getPrefixMonkeys(String[] split) {
		return monkeynames.getPrefixMonkeys(split);
	}
	/**
	 *  Gets the names of all the monkeys associated with a given prefix
	 */
	public Set<String> getPrefixNames(String prefix) {
		return monkeynames.getPrefixNames(prefix);
	}
	
	/**
	 * Gets the names of all monkeys associated with a presplit prefix set
	 * TODO: complete this appropriately as above
	 */
	
	public ArrayList<String> getPrefixNames(String[] split) {
		return null;
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
