package com.gamelanlabs.chimple2.diagnostics;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.solvers.Solver;
import com.gamelanlabs.chimple2.visualization.Histogram;

/**
 * Plots a histogram of the runtime for a block of code
 * (passed in as a Runnable).
 * 
 * @author BenL
 *
 */
public class RuntimePlotter {
	/**
	 * Plot runtimes for a Runnable.
	 * 
	 * @param	command		Command to run
	 * @param	title		Plot title
	 * @param	n			Number of runs
	 * @param	h			Histogram to plot on
	 */
	public static Histogram plot(Runnable command, String title, int n, Histogram h) {
		XYSeries times = new XYSeries(title);
		h.add(times);
		
		for(int i = 0; i < n; i++) {
			ChimpleProgram.tic();
			command.run();
			times.add(i, ChimpleProgram.toc());
		}
		return h;
	}
	
	/**
	 * Plot runtimes for a Runnable.
	 * 
	 * @param	command		Command to run
	 * @param	title		Plot title
	 * @param	n			Number of runs
	 * @param	hTitle		Histogram title
	 */
	public static Histogram plot(Runnable command, String title, int n, String hTitle) {
		Histogram h = new Histogram(hTitle, hTitle, "Sample number", "Wallclock time", Histogram.SCATTER|Histogram.LOGAXIS);
		return plot(command, title, n, h);
	}
	
	/**
	 * Makes a bar chart after all runtimes have been plotted on the scatter chart.
	 * 
	 * @param	scatter
	 * @return	bar
	 */
	public static Histogram makeBarChart(Histogram scatter) {
		XYDataset data = (XYDataset) scatter.getData();
		Histogram bar = new Histogram("Summary", "Summary", "Solvers", "Wallclock time", Histogram.BAR);
		
		int seriesCt = data.getSeriesCount();
		for(int i = 0; i < seriesCt; i++) {
			int itemCt = data.getItemCount(i);
			double total = 0;
			double sqtotal = 0;
			for(int j = 0; j < itemCt; j++) {
				double value = data.getYValue(i, j);
				total += value;
				sqtotal += value*value;
			}
			double avg = total/itemCt;
			double var = sqtotal/itemCt - avg*avg;
			Comparable<?> key = data.getSeriesKey(i);
			bar.add(avg, "Mean", key);
			bar.add(var, "Variance", key);
		}
		
		return bar;
	}
	
	/**
	 * Convenience function to construct a Runnable from
	 * a few parameters (basically a harness description).
	 * 
	 * @param	programClass
	 * @param	solverClass
	 * @param	programCf
	 * @param	programArgs
	 * @param	solveArgs
	 * @return	runnable
	 */
	public static Runnable makeRunnable(
			final Class<?> programClass, final Class<?> solverClass,
			final CostFunction programCf, final Object[] programArgs,
			final Object[] solveArgs) {
		return new Runnable() {
			@Override
			public void run() {
					try {
						ChimpleProgram p = (ChimpleProgram) programClass.newInstance();
						Solver s = (Solver) ConstructorUtils.invokeConstructor(solverClass,
								new Object[] {p, programArgs, programCf});
						MethodUtils.invokeMethod(s, "solve", solveArgs);
						s.cleanup();
						s = null;
						p.cleanup();
						p = null;
					} catch (InstantiationException | IllegalAccessException |
							NoSuchMethodException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
			}
		};
	}
}
