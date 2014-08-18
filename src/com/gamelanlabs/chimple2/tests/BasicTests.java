package com.gamelanlabs.chimple2.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.Zookeeper;
import com.gamelanlabs.chimple2.monkeys.ChimpBeta;
import com.gamelanlabs.chimple2.monkeys.ChimpDirichlet;
import com.gamelanlabs.chimple2.monkeys.ChimpDiscrete;
import com.gamelanlabs.chimple2.monkeys.ChimpFlip;
import com.gamelanlabs.chimple2.monkeys.ChimpGamma;
import com.gamelanlabs.chimple2.monkeys.ChimpNormal;
import com.gamelanlabs.chimple2.monkeys.ChimpRand;
import com.gamelanlabs.chimple2.monkeys.Monkey;
import com.gamelanlabs.chimple2.solvers.MetropolisHastingsSolver;
import com.gamelanlabs.chimple2.solvers.PriorSolver;

/**
 * Some basic unit tests. Run with JUnit4 in Eclipse.
 * 
 * @author BenL
 * @author dicai
 *
 */
public class BasicTests {
	/**
	 * Instantiates all ERPs and make sure their generate(),
	 * regenerate(), energy(), and transitionEnergy() functions
	 * are reasonable.
	 */
	@SuppressWarnings("unused")
	@Test
	public void instantiateAllChimps() {
		Zookeeper z = new Zookeeper();
		
		Monkey<?>[] monkeys = new Monkey[] {
				new ChimpBeta() {{setParams(new Double[] {2.0, 1.0});}},
				new ChimpDirichlet() {{setParams(new double[] {0.2, 0.2, 0.2, 0.2, 0.2});}},
				new ChimpDiscrete() {{setParams(new double[] {1, 90, 9});}},
				new ChimpFlip() {{setParams(0.1);}},
                new ChimpGamma() {{setParams(1.0, 1.0);}},
				new ChimpNormal() {{setParams(new Double[] {0.0, 1.0, 0.1});}},
				new ChimpRand()
		};
		
		for(Monkey<?> m : monkeys) {
			m.setZookeeper(z);
			Object value = m.generate();
			double energy = m.energy();
			Object value2 = m.propose();
			double tenergy = getTransitionEnergy(m, value);
			
			assertTrue(m.getValue() != null);

			assertFalse(Double.isNaN(energy));
			assertFalse(Double.isNaN(tenergy));

//          TODO: investigate this
//			Monkey<?> m2 = m.clone();
//			assertTrue(m2.getValue() != null);
		}
	}
	
	/**
	 * Helper function to get around generic type casting.
	 * 
	 * @param	m
	 * @param	value
	 * @return	energy
	 */
	@SuppressWarnings("unchecked")
	private <Banana> double getTransitionEnergy(Monkey<Banana> m, Object value) {
		return m.transitionEnergy((Banana) value);
	}
	
	/**
	 * Very basic test program for inference.
	 * 
	 * @author BenL
	 *
	 */
	private class BasicTestChimpleProgram extends ChimpleProgram {
		@Override
		public Object run(Object... args) {
			int flip = chimpFlip("flip", 0.5);
			double normal;
			if(flip == 1) {
				normal = chimpNormal("normal", 0, 1);
			} else {
				normal = chimpNormal("normal", 1000, 1);
			}
			double normal2 = chimpNormal("normal2", normal, 100);
			addEnergy((normal2-1000)*(normal2-1000));
			return flip;
		}
	}
	
	/**
	 * Run basic test program forward.
	 */
	@Test
	public void runBasicTestProgramForward() {
		PriorSolver s = new PriorSolver(new BasicTestChimpleProgram(),
				new Object[] {}, null);
		s.solve(1);
	}
	
	/**
	 * Do inference on basic test program.
	 */
	@Test
	public void runBasicTestProgramInference() {
		MetropolisHastingsSolver s = new MetropolisHastingsSolver(new BasicTestChimpleProgram(),
				new Object[] {}, null);
		s.solve(100, 1000, 1);
		
		int sum = 0;
		for(Object out : s.getResults()) {
			sum += (int) out;
		}
		double posterior_weight = ((double)sum)/s.getResults().size();
		assertTrue(posterior_weight <= 0.4);
	}
}
