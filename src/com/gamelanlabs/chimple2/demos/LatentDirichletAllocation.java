package com.gamelanlabs.chimple2.demos;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Generative model for documents.
 * 
 * @author Rachel Cao
 * @author BenL
 * 
 */
public class LatentDirichletAllocation extends Demo 
{
	private final int n_docs, n_topics, n_vocab, avg_doc_length;
	private final double alpha_topic[];
	private final double alpha_vocab[];
	public ArrayList<int[]> topic = new ArrayList<int[]>();
	public ArrayList<int[]> doc = new ArrayList<int[]>();

	/**
	 * Constructor
	 * 
	 * @param	n_docs
	 * @param	n_topics
	 * @param	n_vocab
	 * @param	avg_doc_length
	 */
	public LatentDirichletAllocation(int n_docs, int n_topics, int n_vocab, int avg_doc_length) 
	{
		this.n_docs = n_docs;
		this.n_topics = n_topics;
		this.n_vocab = n_vocab;
		this.avg_doc_length = avg_doc_length;
		alpha_topic = new double[n_topics];
		alpha_vocab = new double[n_vocab];
		for(int i = 0; i < n_topics; i++) {
			alpha_topic[i]= 1;
		}
		for(int i = 0; i < n_vocab; i++) {
			alpha_vocab[i]= 1;
		}
	}
	
	public LatentDirichletAllocation() {
		this(250, 10, 1000, 50);
	}

	/**
	 * Probabilistic program
	 */
	@Override
	public Object run(Object ... args)
	{
		int temp_topic[];
		int temp_doc[];
		double vocab_dist[][];
		double topic_dist[][];
		int doc_length[];
		vocab_dist = new double[n_topics][n_vocab];
		for (int i = 0; i < n_topics; i++) {
			vocab_dist[i] = chimpDirichlet("vocab"+i, alpha_vocab);
		}

		topic_dist = new double[n_docs][n_topics];
		doc_length = new int[n_docs];
		Random gaussian_var = new Random();
		for (int i = 0; i < n_docs; i++) {
			topic_dist[i] = chimpDirichlet("topics"+i, alpha_topic);

			doc_length[i] = Math.max(1,(int)(gaussian_var.nextGaussian()*avg_doc_length/3+avg_doc_length));
			temp_topic = new int[doc_length[i]];
			temp_doc = new int[doc_length[i]];
			for (int l = 0; l < doc_length[i]; l++) {
				temp_topic[l] = chimpDiscrete("topic"+i+"_"+l, topic_dist[i]);
				temp_doc[l] = chimpDiscrete("doc"+i+"_"+l, vocab_dist[temp_topic[l]]);
			}
			topic.add(temp_topic);
			doc.add(temp_doc);
		}
		Object[] results = new Object[] {vocab_dist, topic_dist, doc_length, topic, doc};
		return results;
	}
	
	/**
	 * Harness
	 * 
	 * @param	args
	 */
	public static void main(String[] args)
	{
		int burnin = 0;
		int samples = 1;
		int spacing = 0;
		
		//LatentDirichletAllocation m = new LatentDirichletAllocation(250, 10, 1000, 50);
		LatentDirichletAllocation m = new LatentDirichletAllocation(20000, 100, 10000, 50);

		tic();
		Object[] results = MHQuery(burnin, samples, spacing, m);
		tocPrint();
		
		@SuppressWarnings("unchecked")
		ArrayList<int[]> doc = (ArrayList<int[]>) (((Object[]) results[0])[4]);
		
		try {
			PrintWriter writer = new PrintWriter("document.txt");
			for (int[] item : doc) {
				for (int i = 0; i < item.length; i++) {
					writer.print(item[i]);
					writer.print(" ");
				}
				writer.print("\n");
			}
			writer.close();
			System.out.println("Successfully wrote output to file!");
		} catch(FileNotFoundException e) {
			System.out.println("Couldn't open file for output!");
		}
	}
	
	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	@Override
	public void display(ArrayList<Object> results) { }
	
	/**
	 * Returns a settings panel.
	 * 
	 * @return	panel
	 */
	@Override
	public JPanel getSettingsPanel() {
		// Outer panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		// Info
		panel.add(new JLabel("<html><body style='width: 125px'>" +
				"This is a <b>forward-only</b> model. " + 
				"Please run with PriorSolver.<br />"+
				"</html></body>"));
		
		// Inner panel
		JPanel innerPanel = new JPanel();
		JPanel middlePanel = new JPanel();
		middlePanel.add(innerPanel);
		panel.add(middlePanel);
		GroupLayout layout = new GroupLayout(innerPanel);
		innerPanel.setLayout(layout);
		
		// Auto-margin, auto-padding
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		// Setting pairs
		JLabel lblNDocs = new JLabel("# docs");
		JTextField txtNDocs = new JTextField("250", 10);
		txtNDocs.setEnabled(false);
		
		JLabel lblNTopics = new JLabel("# topics");
		JTextField txtNTopics = new JTextField("10", 10);
		txtNTopics.setEnabled(false);
		
		JLabel lblNVocab = new JLabel("Vocab size");
		JTextField txtNVocab = new JTextField("1000", 10);
		txtNVocab.setEnabled(false);
		
		JLabel lblAvgDocLength = new JLabel("Avg. len.");
		JTextField txtAvgDocLength = new JTextField("50", 10);
		txtAvgDocLength.setEnabled(false);
		
		innerPanel.add(lblNDocs);
		innerPanel.add(txtNDocs);
		innerPanel.add(lblNTopics);
		innerPanel.add(txtNTopics);
		innerPanel.add(lblNVocab);
		innerPanel.add(txtNVocab);
		innerPanel.add(lblAvgDocLength);
		innerPanel.add(txtAvgDocLength);
		
		// Configure layout
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblNDocs)
								.addComponent(txtNDocs)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblNTopics)
								.addComponent(txtNTopics)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblNVocab)
								.addComponent(txtNVocab)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblAvgDocLength)
								.addComponent(txtAvgDocLength)
					)
			);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(lblNDocs)
								.addComponent(lblNTopics)
								.addComponent(lblNVocab)
								.addComponent(lblAvgDocLength)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(txtNDocs)
								.addComponent(txtNTopics)
								.addComponent(txtNVocab)
								.addComponent(txtAvgDocLength)
					)
			);
		
		return panel;
	}
}
