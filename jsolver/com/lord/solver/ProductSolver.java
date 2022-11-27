package com.lord.solver;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ProductSolver extends JPanel {
	private static final long serialVersionUID = 1L;

	protected MainWindow parent;
	
	private JButton auto;
	private JTextArea text;
	
	private double average = 0.0, ntries = 0.0, runs = 0.0;
	
	public ProductSolver(final int width, final int height, MainWindow _parent) {
		super();
		
		parent = _parent;
		
		this.setPreferredSize(new Dimension(width, height));
		
		text = new JTextArea(1, 5);
		text.setEditable(false);
		text.setFont(new Font("Arial", Font.PLAIN, 48));
		this.add(text);
		
		auto = new JButton("Auto-solve");
		auto.setPreferredSize(new Dimension(150, 75));
		auto.addActionListener(event -> auto());
		this.add(auto);
		
		response = new byte[5];
		answer = new byte[5];
		maxFreq = new int[5];
		ansFreq = new HashMap<Character, Byte>();
		
		sorter = new WordSorter();
	}
	
	private boolean justReset = false;
	
	private Thread automaton;
	private boolean auto_running = false;
	
	private void auto_run() {
		
		ntries = 0;
		++runs;
		
		while(auto_running) {
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(predict()) {
				auto_running = false;
				auto_stop();
			} 
			
			++ntries;
		}
		
		average = ((runs - 1) * average + ntries) / runs;
		
		System.out.println("Auto-solving completed.");
		auto.setEnabled(true);
		reset();
		justReset = true;
		System.out.println("Current average: " + average);
	}
	
	private synchronized void auto_stop() {
		if(!auto_running) return;
		auto_running = false;
		try {
			automaton.join();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void auto() {
		if(auto_running) return;
		auto_running = true;
		automaton = new Thread(() -> auto_run());
		auto.setEnabled(false);
		automaton.start();
		System.out.println("Auto-solving...");
	}
	
	private synchronized void reset() {
		text.setText("");
		Arrays.fill(response, (byte)'0');
		Arrays.fill(answer, (byte)0);
		parent.array.possible.clear();
		parent.array.possible.addAll(parent.array.words);
		justReset = true;
		System.out.println("Reset.");
	}
	
	private byte response[], answer[];
	private int maxFreq[];
	
	private class WordSorter implements Comparator<SWord> {

		@Override
		public int compare(SWord o1, SWord o2) {
			return Double.compare(o1.score, o2.score);
		}
		
	}
	
	private WordSorter sorter;
	private Map<Character, Byte> ansFreq;
	
	public boolean predict() {
		
		if(parent.out.get(0) == (byte)'0' && !justReset) {
			parent.out.get(1, answer);
			parent.out.get(6, response);
			
			boolean _finished = true;
			for(int i = 0; i < 5; ++i) if(response[i] != (byte)'2') _finished = false;
			if(_finished) {
				return true;
			}
			
			for(char c = 'A'; c <= 'Z'; ++c) ansFreq.put(c, (byte)0);
			
			for(int i = 0; i < 5; ++i) {
				byte lb = ansFreq.get((char)answer[i]);
				ansFreq.put((char)answer[i], (byte)Math.max(response[i], lb));
			}
			
			for(int i = 0; i < parent.array.possible.size(); ++i) {
				String word = parent.array.possible.get(i);
				for(int j = 0; j < 5; ++j) {
					if(response[j] == (byte)'2') {
						if(word.charAt(j) != (char)answer[j]) {
							parent.array.possible.remove(i);
							--i;
							break;
						}
					} else if(response[j] == (byte)'1') {
						if(word.indexOf(answer[j]) == j) {
							parent.array.possible.remove(i);
							--i;
							break;
						}
					} else if(response[j] == (byte)'0') {
						if(ansFreq.get((char)answer[j]) == (byte)'0') {
							if(word.indexOf(answer[j]) != -1) {
								parent.array.possible.remove(i);
								--i;
								break;
							}
						}
					}
				}
			}
		}
		
		if(justReset) justReset = false;
		
		if(parent.array.possible.size() <= 0) {
			reset();
			justReset = true;
			return false;
		}
		
		List<SWord> swords = new ArrayList<SWord>();
		
		for(char c = 'A'; c <= 'Z'; ++c) {
			Arrays.fill(parent.array.frequency.get(c), 0);
		}
		
		for(String word : parent.array.possible) {
			for(int i = 0; i < 5; ++i) {
				int frq[] = parent.array.frequency.get(word.charAt(i));
				++frq[i];
			}
		}
		
		Arrays.fill(maxFreq, 0);
		
		for(char c = 'A'; c <= 'Z'; ++c) {
			int frq[] = parent.array.frequency.get(c);
			for(int i = 0; i < 5; ++i) {
				if(maxFreq[i] < frq[i]) {
					maxFreq[i] = frq[i];
				}
			}
		}
		
		for(String word : parent.array.possible) {
			double score = 1.0;
			for(int i = 0; i < 5; ++i) {
				double val = (parent.array.frequency.get(word.charAt(i))[i] - maxFreq[i]);
				score *= (1.0 + val * val);
			}
			swords.add(new SWord(word, score));
		}
		
		Collections.sort(swords, sorter);
		parent.array.possible.remove(swords.get(0).word);
		
		parent.out.put(0, (byte)'1');
		parent.out.put(1, swords.get(0).word.getBytes());
		text.setText(swords.get(0).word);
		return false;
	}
	
}
