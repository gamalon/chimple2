package com.gamelanlabs.chimple2.visualization;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Histogram visualization tool that uses JFreeChart.
 * 
 * @author BenL
 *
 */
public class Histogram extends JFrame {
	private static final long serialVersionUID = 1880276366917111721L;

	/**
	 * Scatter plot
	 */
	public static final int SCATTER = 0x0000;
	
	/**
	 * Line plot
	 */
	public static final int LINE = 0x0001;
	
	/**
	 * Bar chart
	 */
	public static final int BAR = 0x0002;
	
	/////////////////////////////////////////////////
	
	/**
	 * Use powers of 10 on the Y-axis.
	 */
	public static final int LOGAXIS = 0x0100;
	
	/////////////////////////////////////////////////
	
	/**
	 * Mode
	 */
	protected static int mode;
	
	/**
	 * The internal dataset object.
	 */
	protected Dataset data;
	
	/**
	 * The internal JFreeChart object.
	 */
	protected JFreeChart chart;
	
	/**
	 * Constructor
	 * 
	 * @param	windowtitle	Title of the JFrame
	 * @param	charttitle	Title of the chart
	 * @param	xlabel		X-axis label
	 * @param	ylabel		Y-axis label
	 * @param	type		Flags
	 */
	public Histogram(String windowtitle, String charttitle, String xlabel, String ylabel, int type) {
		super(windowtitle);
		mode = type & 0x00FF;
		
		// Make chart
		switch(mode) {
		case SCATTER:
			data = new XYSeriesCollection();
			chart = ChartFactory.createScatterPlot(charttitle, xlabel, ylabel,
					(XYDataset) data, PlotOrientation.VERTICAL,
					true, false, false);
			break;
		case LINE:
			data = new XYSeriesCollection();
			chart = ChartFactory.createXYLineChart(charttitle, xlabel, ylabel,
					(XYDataset) data);
			break;
		case BAR:
			data = new DefaultCategoryDataset();
			chart = ChartFactory.createBarChart(charttitle, xlabel, ylabel,
					(CategoryDataset) data);
			break;
		default:
			throw new RuntimeException("Unknown chart type!");
		}
		
		// Interpret flags
		if((type & LOGAXIS) != 0) {
			// Use a LogAxis
			XYPlot plot = chart.getXYPlot();
			ValueAxis old = plot.getRangeAxis();
			LogAxis yAxis = new LogAxis(ylabel);
			
			yAxis.setBase(10);
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			yAxis.setNumberFormatOverride(new DecimalFormat("###,###,###,##0.######"));
			yAxis.setLabelFont(old.getLabelFont());
			yAxis.setTickLabelFont(old.getTickLabelFont());
			yAxis.setLabelPaint(old.getLabelPaint());
			yAxis.setTickLabelPaint(old.getTickLabelPaint());
			
			plot.setRangeAxis(yAxis);
		}
		
		// Create ChartPanel
		ChartPanel panel = new ChartPanel(chart);
		getContentPane().add(panel);
		
		// Set size bounds (so that when the frame is resized,
		// the ChartPanel also resizes)
		panel.setMinimumDrawWidth(0);
		panel.setMinimumDrawHeight(0);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		panel.setMaximumDrawWidth(screen.width);
		panel.setMaximumDrawHeight(screen.height);

		// Set up and show JFrame
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Default constructor
	 */
	public Histogram() {
		this("Histogram", "Histogram", "X axis", "Y axis", SCATTER);
	}
	
	/**
	 * Gets renderer
	 * 
	 * @return	renderer	The XYItemRenderer
	 */
	public XYItemRenderer getRenderer() {
		return chart.getXYPlot().getRenderer();
	}
	
	/**
	 * Gets data (by reference, so modifications will change the plot!)
	 * 
	 * @return	data
	 */
	public Dataset getData() {
		return data;
	}
	
	/**
	 * Adds data.
	 * 
	 * @param	xys	Data to add
	 */
	public void add(XYSeries xys) {
		((XYSeriesCollection) data).addSeries(xys);
	}
	
	/**
	 * Adds data.
	 * 
	 * @param	value	Value
	 * @param	rowKey	Axis label
	 * @param	colKey	Legend label
	 */
	public void add(Number value, Comparable<?> rowKey, Comparable<?> colKey) {
		((DefaultCategoryDataset) data).addValue(value, rowKey, colKey);
	}
	
	/**
	 * Adds data.
	 * 
	 * @param	value	Value
	 * @param	rowKey	Axis label
	 */
	public void add(Number value, Comparable<?> rowKey) {
		((DefaultCategoryDataset) data).addValue(value, "Value", rowKey);
	}
}
