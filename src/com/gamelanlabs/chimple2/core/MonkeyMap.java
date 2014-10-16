package com.gamelanlabs.chimple2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.gamelanlabs.chimple2.monkeys.Monkey;

/**
 * HashMap Implementation of a Name -> Monkey data structure that groups
 * monkeys based on prefix. Replaces the current HashMap<String, Monkey>
 * structure that existed.
 * 
 * Disadvantages: More overhead from storage of many hash tables
 * Advantages: Allows to find all monkeys with a given prefix.
 * 
 * Some (Incomplete) Speed Tests / 1000 monkeys / 1000 iters:
 * HashMap:   9.21s mean  /  1.05s standard deviation / 35 runs
 * MonkeyMap: 8.48s mean  /  1.03s standard deviation / 40 runs  /  1 depth
 * MonkeyMap: 7.52s mean  /  0.70s standard deviation / 33 runs  /  2 depth
 * 
 * TODO: Implement a structure with an index heap and an arraylist of names
 *       (may be faster)
 *       
 * WARNING: The keys and values are not ordered when they are returned. If 
 * you want them to be ordered, then we have to implement the hash table as 
 * a linkedhashset instead; this will have a guarantee on ordering. Not an 
 * issue for now, but will be if we attempt to resample deterministically.
 * 
 * @author AllenC
 *
 */
public class MonkeyMap {
	/**
	 * Delimiter that tells MonkeyCage when to go down a layer of the heap,
	 * i.e. a chimpRand called "group1-random-12" would go into group1->random
	 * in the heap.
	 */
	public static String delimiter = "-";
	
	/**
	 * Children of the current map; the characters that do not have a hyphen will
	 * be read into the root node rather than into the array, as branching is quite
	 * expensive.
	 */
	private ArrayList<MonkeyMap> children;
	
	/**
	 * Maps one-to-one with the children; each child has a prefix that determines
	 * what the children end up having as their monkeys.
	 */
	private ArrayList<String> prefixes;
	
	/**
	 * The place where monkeys that belong at this level are stored.
	 */
	private HashMap<String, Monkey<?>> monkeynames;
	
	/**
	 * Constructor is fairly standard - just initialize all the structures that need
	 * to be initialized to not be null.
	 */
	public MonkeyMap() {
		this.monkeynames = new HashMap<String, Monkey<?>>();
		this.children = new ArrayList<MonkeyMap>();
		this.prefixes = new ArrayList<String>();
	}
	
	/**
	 * Puts a name/monkey into the MonkeyMap.
	 * 
	 * @param	name	Name of the monkey that we are putting into the map.
	 * @param	m		Monkey that we are putting into the map
	 */
	public void put(String name, Monkey<?> m) {
		String[] split = name.split(delimiter);
		
		if(split.length == 1) {
			monkeynames.put(name, m);
		} else {
			insert(split, m, 1);
		}
	}
	
	/**
	 * Removes a monkey from the HashMap. Don't do this step too often if possible; 
	 * is quite expensive for this kind of structure.
	 * 
	 * Note: Would be much faster if we knew a name, but this would involve more 
	 * changes to Chimple's architecture than necessary.
	 * 
	 * @param	m			Monkey to be removed
	 * @return	success		Whether or not we were successful in removing said monkey
	 */
	public boolean remove(Monkey<?> m) {
		if(monkeynames.containsValue(m)) {
			monkeynames.remove(m);
			return true;
		} else {
			for(MonkeyMap child : children){
				if(child.remove(m)) {
					return true;
				}
			}
		}
		
		// Just to make sure that the user knows if the operation failed.
		System.out.println("Warning: Remove didn't work.");
		return false;
	}
	
	/**
	 * Does the MonkeyMap contain a given name?
	 * 
	 * @param	name	The name of the monkey we are querying
	 * @return	maybe	Whether or not the monkey exists
	 */
	public boolean containsKey(String name) {
		if(monkeynames.containsKey(name)) {
			return true;
		} else {
			for(MonkeyMap child : children) {
				if(child.containsKey(name)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the monkey with the given name. Returns null if no such
	 * monkey exists.
	 * 
	 * @param	name	name of the monkey that we want to find
	 * @return	m		Monkey that corresponds to this name
	 */
	public Monkey<?> get(String name) {
		Monkey<?> m = monkeynames.get(name);
		
		// look in all of the children until the function doesn't return null
		// runs a DFS-style search through this heap
		if(m == null) {
			for(MonkeyMap child : children) {
				m = child.get(name);
				if(m != null) {
					return m;
				}
			}
		}
		
		return m;
	}
	
	/**
	 * Gets all of the keys (monkey names) that exist in the heap. Note that this
	 * does work in place - do not perform actions on the strings.
	 * 
	 * @return	names	Names of all of the monkeys inside the map
	 */
	public Set<String> keySet() {
		Set<String> total = new HashSet<String>();
		total.addAll(this.monkeynames.keySet());
		
		for(MonkeyMap child : children) {
			total.addAll(child.keySet());
		}
		
		return total;
	}
	
	/**
	 * Gets all of the monkeys that exist in the heap. This works in place, so the monkeys
	 * can be operated on to change them; please do not abuse the monkeys with this info.
	 * 
	 * @return	allMonkeys	All the monkeys that exist in the heap.
	 */
	public ArrayList<Monkey<?>> values() {
		ArrayList<Monkey<?>> total = new ArrayList<Monkey<?>>();
		total.addAll(this.monkeynames.values());
		
		for(MonkeyMap child : children) {
			total.addAll(child.values());
		}
		
		return total;
	}
	
	/**
	 * Gets all the monkeys that correspond to a given prefix. Performed in place.
	 * 
	 * If no such monkeys exist, the result will be an empty ArrayList of monkeys.
	 * In particular, it will NOT return null.
	 * 
	 * @param	prefix		All monkeys with this as the prefix will be found
	 * @return	monkeys		The corresponding monkeys
	 */
	public ArrayList<Monkey<?>> getPrefixMonkeys(String prefix) {
		String[] split = prefix.split(delimiter);
		
		if(prefix.contains(delimiter)) {
			return getPrefixMonkeys(split, 0);
		} else {
			return getPrefixMonkeys(split);
		}
	}
	
	/**
	 * Gets all the monkeys that correspond to a given prefix. In this case,
	 * the prefix has been already split up into non-delimited pieces (if there
	 * are delimiters in the pieces, an error will be spit out). Performed in
	 * place.
	 * 
	 * If no such monkeys exist, the result will be an empty ArrayList of monkeys.
	 * In particular, it will NOT return null.
	 * 
	 * @param	split		An array with all of the pieces of the desired prefix
	 * @return	monkeys		Monkeys corresponding to this sequence of prefixes
	 */
	public ArrayList<Monkey<?>> getPrefixMonkeys(String[] split) {
		if(split.length == 1) {
			// Since we consciously chose not to branch the monkeys with no delimiters,
			// we need to find the ones that we need
			ArrayList<Monkey<?>> monkeys = new ArrayList<Monkey<?>>();
			for(String val : this.monkeynames.keySet()) {
				if(val.startsWith(split[0])) {
					monkeys.add(get(val));
				}
			}
			return monkeys;
			
		} else {
			return getPrefixMonkeys(split, 0);
		}
	}
	
	/**
	 * Return the keyset of the HashMap corresponding to the prefix
	 * @param prefix
	 * @return
	 */
	public Set<String> getPrefixNames(String prefix){
		Set<String> names = new HashSet<String>();
		if (prefixes.contains(prefix)){
			names.addAll(this.children.get(prefixes.indexOf(prefix)).keySet());
		}
		else {
			for (MonkeyMap child : children)
				names.addAll(child.getPrefixNames(prefix));
		}
		return names;
			
	}
	
	/**
	 * Gets all of the entries in the hash table that we have. This only passes
	 * the pointer, doesn't make a copy of each entry. 
	 * 
	 * @return	total	All of the entries in each hash table
	 */
	public Set<Entry<String, Monkey<?>>> entrySet() {
		// Initialize new memory and begin adding entries to it
		Set<Entry<String, Monkey<?>>> total = new HashSet<Entry<String, Monkey<?>>>();
		total.addAll(this.monkeynames.entrySet());
		
		for(MonkeyMap child : children) {
			total.addAll(child.entrySet());
		}
		
		return total;
	}
	
	/**
	 * Inserts the string into the heap, but doesn't perform checks and passes
	 * which layer we are at. So, its set to private to prevent someone from generating
	 * an error by using it.
	 * 
	 * @param	split	A split array of any input string
	 * @param 	m		The monkey we are inserting
	 * @param 	val		Value of which layer we are at
	 */
	private void insert(String[] split, Monkey<?> m, int val) {
		// end condition for when we insert a given string
		if(split.length == val) {
			monkeynames.put(StringUtils.join(split, delimiter), m);
		} else {
			int index = prefixes.indexOf(split[val-1]);
			if(index == -1) {
				// if we can't find a prefix, create a new child to add to the array
				// index sure we do not end up with an error/faster bugfixing
				prefixes.add(split[val-1]);
				MonkeyMap child = new MonkeyMap();
				this.children.add(child);
				child.insert(split, m, ++val);
			} else {
				this.children.get(index).insert(split, m, ++val);
			}
		}
	}
	
	/**
	 * Gets all of the monkeys that correspond to a given prefix. This doesn't perform
	 * checks and passes information about which layer we are at, so is set to a private
	 * method
	 * 
	 * @param	split		A split array of any input string
	 * @param 	val			Layer that we are at
	 * @return	monkeys		All of the monkeys that have prefix that is the input string
	 */
	private ArrayList<Monkey<?>> getPrefixMonkeys(String[] split, int val) {
		// end condition for the root of the nodes that we are getting
		if(split.length == val) {
			return values();
		} else {
			int index = prefixes.indexOf(split[val]);
			if(index == -1) {
				// TODO: Implement a new exception that handles missing prefixes and other things
				return new ArrayList<Monkey<?>>();
			} else {
				return this.children.get(index).getPrefixMonkeys(split, ++val);
			}
		}
	}
	
	/**
	 * Utility function that flattens a HashMap into a monkey array.
	 * 
	 * @param	map			HashMap that we want to turn into an array
	 * @return	monkeys		The array version that has all the values of the HashMap
	 */
	protected ArrayList<Monkey<?>> flatten(HashMap<String, Monkey<?>> map) {
		ArrayList<Monkey<?>> output = new ArrayList<Monkey<?>>();
		output.addAll(map.values());
		return output;
	}
	
}
