package com.gamelanlabs.chimple2.util;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * Captures an OutputStream (such as STDOUT).
 * 
 * @author BenL
 * 
 */
public class StreamCapture extends OutputStream {

	private final StringBuilder buffer;
	private final String prefix;
	private final JTextArea consumer;
	private final PrintStream old;

	public StreamCapture(String prefix, JTextArea consumer, PrintStream old) {
		this.prefix = prefix;
		buffer = new StringBuilder(128);
		buffer.append("[").append(prefix).append("] ");
		this.old = old;
		this.consumer = consumer;
	}

	@Override
	public void write(int b) throws IOException {
		char c = (char) b;
		String value = Character.toString(c);
		buffer.append(value);
		if (value.equals("\n")) {
			appendText(buffer.toString());
			buffer.delete(0, buffer.length());
			buffer.append("[").append(prefix).append("] ");
		}
		old.print(c);
	}

	public void appendText(final String text) {
		if (EventQueue.isDispatchThread()) {
			consumer.append(text);
			consumer.setCaretPosition(consumer.getText().length());
		} else {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					appendText(text);
				}
			});
		}
	}
}
