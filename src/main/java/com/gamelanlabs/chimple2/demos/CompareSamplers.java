package com.gamelanlabs.chimple2.demos;

import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.visualization.Histogram;

/**
 * Demo that visualizes the prior and posterior distributions
 * produced by the random coin generative model, using various
 * solvers.
 * 
 * @author BenL
 *
 */
public class CompareSamplers extends Demo {
	/**
	 * Histogram to display the comparison on
	 */
	protected static Histogram hist;
			
	/**
	 * Entry point
	 * 
	 * @param	args
	 */
    public static void main(String[] args) {
    	Demo program = new RandomCoin();
    	
		// Sample prior
		ArrayList<Object> results_pr = Demo.runDemo(program,
				new String[] {"forward"});
		
		// Sample traceMH
		ChimpleProgram.tic();
		ArrayList<Object> results_mh = Demo.runDemo(program,
				new String[] {"tracemh"});
		double mh_elapsed = ChimpleProgram.toc();
		
		// Generate x values, Binomial draw probabilities, and closed-form Beta-Binomial solution
		XYSeries y_actual = new XYSeries("Asymptotic, Bin(20, 0.75)");
		XYSeries y_sol = new XYSeries("True solution, BetaBin(20, 16, 6)");
		
		for(int i = 0; i <= 20; i++) {
			y_actual.add(i, CombinatoricsUtils.binomialCoefficient(20, i)
					*Math.pow(0.75, i)*Math.pow(0.25, 20-i));
			y_sol.add(i, betabin(20, i, 16, 6));
		}
		
		// Tally up prior and traceMH results
		XYSeries y_pr = toXYSeries("Prior", tallyResults(results_pr));
		XYSeries y_mh = toXYSeries("TraceMH", tallyResults(results_mh));
		
		// Create histogram and add the distributions.
		hist = new Histogram("RandomCoin", "Sampler Comparison",
				"# Heads", "Probability", Histogram.LINE);
		hist.add(y_pr);
		hist.add(y_mh);
		hist.add(y_sol);
		hist.add(y_actual);
		
		// Some styling
		XYItemRenderer r = hist.getRenderer();
		r.setSeriesStroke(3, new BasicStroke(5));
		
		System.out.printf("Time elapsed:\ntraceMH - %f s\n",
				mh_elapsed);
    }
    
	/**
	 * Utility function to convert a list of observations to an array
	 * of frequencies.
	 * 
	 * @param	results		ArrayList of observations
	 * @return	frequencies	Array of frequencies
	 */
	public static double[] tallyResults(ArrayList<Object> results) {
		int size = results.size();
		double isize = 1.0/size;
		double[] y = new double[21];
		for(int i = 0; i < size; i++) {
			int outcome = (((int[]) results.get(i))[0]);
			y[outcome] += isize;
	    }
	    return y;
	}
	
	/**
	 * Utility function to convert a double[] to an XYSeries.
	 * 
	 * @param	id		Passed to the XYSeries constructor
	 * @param	data	Data
	 * @return	series	XYSeries
	 */
	public static XYSeries toXYSeries(Comparable<?> id, double[] data) {
		XYSeries series = new XYSeries(id);
		for(int i = 0; i < data.length; i++) {
			series.add(i, data[i]);
		}
		return series;
	}
   	
	/**
	 * Evaluate the BetaBin(n, alpha, beta) distribution at k.
	 * 
	 * @param	n
	 * @param	k
	 * @param	alpha
	 * @param	beta
	 * @return	r
	 */
   	public static double betabin(int n, int k, int alpha, int beta) {
   		return
				CombinatoricsUtils.binomialCoefficientDouble(n, k)
			*
				(CombinatoricsUtils.factorialDouble(k+alpha-1)*
				CombinatoricsUtils.factorialDouble(n-k+beta-1)*
				CombinatoricsUtils.factorialDouble(alpha+beta-1))
			/
				(CombinatoricsUtils.factorialDouble(n+alpha+beta-1)*CombinatoricsUtils.factorialDouble(alpha-1)
				*CombinatoricsUtils.factorialDouble(beta-1));
   	}

	/**
	 * Even though this class extends Demo (in order to make it
	 * runnable inside DemoChooser), it is not a probabilistic program.
	 * 
	 * @param	results
	 */
	@Override
	@Deprecated
	public void display(ArrayList<Object> results) { }

	/**
	 * Even though this class extends Demo (in order to make it
	 * runnable inside DemoChooser), it is not a probabilistic program.
	 */
	private static boolean messageShown = false;

	/**
	 * Even though this class extends Demo (in order to make it
	 * runnable inside DemoChooser), it is not a probabilistic program.
	 * 
	 * @return	result
	 */
	@Override
	@Deprecated
	public Object run(Object... args) {
		if(!messageShown) {
			messageShown = true;
			JOptionPane.showMessageDialog(null, "This demo is not a probabilistic program itself!");
		}
		return null;
	}
	
	/**
	 * This program can be run from a button in the settings panel.
	 * 
	 * @return	panel
	 */
	@Override
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("<html><body style='width: 100px'>" +
				"This demo is not a probabilistic program itself!" +
				"<br /><br />Your solver choice will have no effect on output. " +
				"<br /><br />To run, click the Compare button.</html></body>"));
		JButton btn = new JButton("Compare");
		btn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				(new SwingWorker<Void, Void>() {
					@Override
					public Void doInBackground() {
						if(hist != null) {
							hist.setVisible(false);
							hist.dispose();
						}
						main(new String[] {});
						return null;
					}
				}).execute();
			}
		});
		panel.add(btn);
		return panel;
	}
	
	/**
	 * Clean up GUI objects.
	 */
	@Override
	public void cleanup() {
		super.cleanup();
		if(hist != null) {
			hist.setVisible(false);
			hist.dispose();
		}
	}
}  