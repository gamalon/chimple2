package com.gamelanlabs.chimple2.solvers;

import java.util.ArrayList;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.core.EndCondition;
import com.gamelanlabs.chimple2.core.MonkeyCage;
import com.gamelanlabs.chimple2.core.MonkeyFactory;
import com.gamelanlabs.chimple2.monkeys.Monkey;
import com.gamelanlabs.chimple2.util.MHUtils;

/**
 * (Correct) traceMH solver. This class is very well documented, so see the comments
 * for algorithm details.
 * 
 * @author BenL
 *
 */
public class MetropolisHastingsSolver extends Solver {
	/**
	 * Energy of last accepted sample.
	 */
	public double lastenergy = Double.POSITIVE_INFINITY;
	
	/**
	 * Result set of last accepted sample.
	 */
	public Object lastresult = null;
	
	/**
	 * Print messages on each traceMH step describing which monkey was resampled,
	 * what the energy & penalized energy of the proposal cage is, what the last
	 * energy was, what the acceptance ratio is, and whether this proposal was
	 * accepted or rejected.
	 */
	public boolean verbose_tracemh_steps = false;
	
	/**
	 * The temperature is set by some subclasses. It controls how much the solver is
	 * willing to "float around" looking for solutions (and not strictly descend the
	 * energy gradient).
	 * 
	 * Final sampling should always be done at temperature=1.0 to ensure the correct
	 * posterior distribution (unless you only care about the modes).
	 */
	public double temperature = 1.0;
	
	/**
	 * Constructor
	 * 
	 * @param	p	ChimpleProgram whose posterior will be sampled
	 * @param	a	Arguments to the program
	 * @param	cf	Cost function
	 */
	public MetropolisHastingsSolver(ChimpleProgram p, Object[] a, CostFunction cf) {
		super(p, a, cf);
	}
	
	/**
	 * Returns the MonkeyFactory that this solver wants to give the
	 * ChimpleProgram.
	 * 
	 * @return	factory
	 */
	@Override
	protected MonkeyFactory makeMonkeyFactory() {
		return new MHUtils.WeakMonkeyFactory(zookeeper);
	}
	
	/**
	 * Solve using traceMH.
	 * 
	 * @param	burnin			Number of "burn-in" samples to throw away initially. These
	 * 							won't be recorded in results[].
	 * @param	samples			Number of samples.
	 * @param	spacing			Number of samples to throw away between saved samples.
	 */
	public void solve(int burnin, int samples, int spacing) {
		// INITIAL SAMPLE
		lastresult = null;
		lastenergy = Double.POSITIVE_INFINITY;
		
		try {
			// BURN IN
			for(int i = 0; i < burnin; i++) {
				step(false);
			}
			
			// SAMPLE
			for(int i = 0; i < samples; i++) {
				step(true);
				
				// SPACING
				if(i != samples-1) {
					for(int j = 0; j < spacing; j++) {
						step(false);
					}
				}
			}
		} catch(EndCondition e) {
			save(e.getSample());
		}
	}
	
	@Override
	public void solve() {
		// Default {burnin, samples, spacing} values
		solve(500, 1000, 5);
	}
	
	/**
	 * Perform a traceMH step.
	 * 
	 * @param	save	Whether or not to save the resultset from this run into results[].
	 */
	protected void step(boolean save) throws EndCondition {
		// Backup the cage, in case we reject. (Deep copies all the monkeys as well.)
		MonkeyCage oldcage = zookeeper.cage.clone();
		
		// If this isn't the first sample, create a proposal by picking a monkey uniformly
		// and sampling from its conditional proposal kernel (regenerate()).
		// TODO: support passing a list of probabilities for choosing which monkey to mutate.
		if(lastresult != null) {
			pointMutation();
		}
		
		// Run the program
		zookeeper.resetTrackers();
		Object result = program.run(arguments);
		zookeeper.killUntouched();
		
		// Calculate energy
		double energy = energy(result, zookeeper.cage);
		double penalizedenergy = energy + penalty(zookeeper.cage, oldcage);
		
		// Acceptance ratio
		Double rand = zookeeper.random.nextDouble();
		Double acceptanceratio = Math.exp((-penalizedenergy+lastenergy)/temperature);
		
		// Talk to the user
		if(verbose_tracemh_steps) {
			System.out.printf("Energy: %f, Penalized energy: %f, Last energy: %f\n", energy, penalizedenergy, lastenergy);
			System.out.printf("Accept if %f <= %f (Hastings ratio)\n", rand, acceptanceratio);
		}
		
		// Accept/reject
		if(rand <= acceptanceratio) {
			// Accept
			accept(save);
			lastenergy = energy;
			lastresult = result;
			
			if(verbose_tracemh_steps) System.out.println("Accept\n");
		} else {
			// Reject
			reject(save);
			zookeeper.cage = oldcage;
			
			if(verbose_tracemh_steps) System.out.println("Reject\n");
		}
		
		// Throw signal if end condition was met.
		if(zookeeper.end) {
			throw new EndCondition(lastresult);
		}
		
		// Save results
		if(save) {
			save(lastresult);
		}
	}
	
	/**
	 * Hook for subclasses.
	 */
	protected void accept(boolean save) {}
	
	/**
	 * Hook for subclasses.
	 */
	protected void reject(boolean save) {}
	
	/**
	 * Derive a traceMH proposal move from the current cage.
	 */
	protected void pointMutation() {
		ArrayList<String> names = new ArrayList<String>(zookeeper.cage.getNames());
		
		// Pick an active monkey to re-trace from. Tag the cage with the name of this monkey.
		String name = names.get(Math.abs(zookeeper.random.nextInt()) % zookeeper.cage.size());
		zookeeper.cage.tag = name;
		
		if(verbose_tracemh_steps) {
			System.out.printf("Regenerating %s\n", name);
		}

		// Regenerate monkey
		zookeeper.cage.get(name).propose();
	}
	
	/**
	 * Add Occam's razor (model complexity) penalty and Hastings (detailed balance)
	 * penalty (more detail below).
	 * 
	 * @param	proposalcage	The proposed move.
	 * @param	oldcage			The current position.
	 * @return	penalty			An energy penalty.
	 */
	protected double penalty(MonkeyCage proposalcage, MonkeyCage oldcage) {
		if(oldcage == null || oldcage.size() == 0) return 0;
		double penalty = 0;
		
		// Penalize more variables (part of Hastings term, actually)
		penalty += Math.log(((double) proposalcage.size())/oldcage.size());

		/**
		 * Satisfy detailed balance.
		 * 
		 * All Monkeys above the branch point will be the same, so their Hastings ratio
		 * will be 1 (they can be safely ignored, as old chimple does).
		 * 
		 * The Monkey at the branch point will have a Hastings term (old chimple
		 * does this correctly).
		 * 
		 * However, Monkeys below the branch point that have parameters changed
		 * will now have a nonunity Hastings ratio. Old chimple doesn't do this!
		 * 
		 * tl;dr: old chimple does not satisfy detailed balance for most nontrivial
		 * probabilistic programs and this is the fix.
		 */
		// Get name of branch point monkey
		String chosen = (String) proposalcage.tag;
		
		// Compute energy of proposing the move proposalcage --> oldcage
		for(String name : oldcage.getNames()) {
			if(name.equals(chosen)) {
				Monkey<?> oldmonkey = oldcage.get(chosen);
				Monkey<?> newmonkey = proposalcage.get(chosen);
				penalty += getTransitionEnergy(newmonkey, oldmonkey);
			} else {
				penalty += oldcage.get(name).energy();
			}
		}
		// Compute energy of proposing the move oldcage --> proposalcage
		for(String name : proposalcage.getNames()) {
			if(name.equals(chosen)) {
				Monkey<?> oldmonkey = oldcage.get(chosen);
				Monkey<?> newmonkey = proposalcage.get(chosen);
				penalty -= getTransitionEnergy(oldmonkey, newmonkey);
			} else {
				penalty -= proposalcage.get(name).energy();
			}
		}
		// TODO: make this more efficient by storing the monkeys in evaluation order, and skipping
		// energy() calls until we reach the branch point monkey?
		
		return penalty;
	}
	
	/**
	 * Check monkey types, in case two traces of the program have different
	 * types on a monkey with the same name (which is an error on the
	 * program writer's part).
	 * 
	 * For example, a = Monkey<String> to a = Monkey<Integer> is invalid --
	 * it shouldn't be possible for the monkey named "a" to transform
	 * from a String monkey to an Integer monkey when it is chosen as the
	 * branch point.
	 * 
	 * @param	from	Monkey whose value we pass to transitionEnergy
	 * @param	to		Monkey to call transitionEnergy on
	 * @return	tenergy	Transition energy
	 */
	@SuppressWarnings("unchecked")
	protected <Type1, Type2> double getTransitionEnergy(
			Monkey<Type1> from, Monkey<Type2> to) {
		try {
			return to.transitionEnergy((Type2) from.getValue());
		} catch(ClassCastException e) {
			throw new RuntimeException(
					"Two traces of the program have different types "+
					"on a monkey with the same name. Please check your program!");
		}
	}

	/**
	 * Return the names of arguments to solve() that this solver takes.
	 * 
	 * @return	names			Friendly names of arguments
	 */
	public static String[] getArgumentNames() {
		return new String[] {"Burnin", "Samples", "Spacing"};
	}
	
	/**
	 * Return default arguments to solve() that this solver takes.
	 * 
	 * @return	defaultargs		Default arguments
	 */
	public static Object[] getDefaultArguments() {
		return new Object[] {500, 1000, 5};
	}
}
