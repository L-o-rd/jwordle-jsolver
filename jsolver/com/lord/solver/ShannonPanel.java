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

public class ShannonPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	protected MainWindow parent;
	
	private JButton button, reset, auto;
	private JTextArea text;
	
	public ShannonPanel(final int width, final int height, MainWindow _parent, final boolean flag) {
		super();
		
		parent = _parent;
		
		this.setPreferredSize(new Dimension(width, height));
		
		text = new JTextArea(1, 5);
		text.setEditable(false);
		text.setFont(new Font("Arial", Font.PLAIN, 48));
		this.add(text);
		
		button = new JButton("Predict");
		button.setPreferredSize(new Dimension(150, 75));
		button.addActionListener(event -> shannon_predict());
		button.setEnabled(false);
		this.add(button);
		
		reset = new JButton("Reset");
		reset.setPreferredSize(new Dimension(150, 75));
		reset.addActionListener(event -> reset());
		reset.setEnabled(false);
		this.add(reset);
		
		auto = new JButton("Auto-solve");
		auto.setPreferredSize(new Dimension(150, 75));
		auto.addActionListener(event -> auto());
		this.add(auto);
		
		response = new byte[5];
		answer = new byte[5];
		ansFreq = new HashMap<Character, Byte>();
		pattern_freq = new int[26];
		
		sorter = new WordSorter();
		
		probs = new ArrayList<Double>();
		lookup = new HashMap<String, HashMap<String, ArrayList<String>>>();
		for(String word : parent.array.words) {
			lookup.put(word, new HashMap<String, ArrayList<String>>());
			for(String pattern : parent.array.all_patterns) {
				lookup.get(word).put(pattern, new ArrayList<String>());
			}
		}
		
		auto_opt = flag; if(flag) System.out.println("First guess chosen.");
	}
	
	private HashMap<String, HashMap<String, ArrayList<String>>> lookup;
	private ArrayList<Double> probs;
	
	private boolean justReset = false;
	
	private Thread automaton;
	private boolean auto_running = false, auto_opt = false;
	
	private void auto_run() {
		
		ntries = 0;
		++runs;
		
		while(auto_running) {
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			clean_array();
			
			if(shannon_predict()) {
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
		if(auto_opt) {
			parent.out.put(0, (byte)'1');
			parent.out.put(1, "TAREI".getBytes());
			text.setText("TAREI");
			parent.array.possible.remove("TAREI");
		}
		automaton = new Thread(() -> auto_run());
		auto.setEnabled(false);
		automaton.start();
		System.out.println("Auto-solving...");
	}
	
	private synchronized void reset() {
		text.setText("");
		Arrays.fill(response, (byte)'3');
		Arrays.fill(answer, (byte)0);
		parent.array.possible.clear();
		parent.array.possible.addAll(parent.array.words);
		justReset = true;
		System.out.println("Reset.");
	}
	
	private byte response[], answer[];
	
	private double average = 0.0, ntries = 0.0, runs = 0.0;
	
	private class WordSorter implements Comparator<SWord> {

		@Override
		public int compare(SWord o1, SWord o2) {
			return Double.compare(o1.score, o2.score);
		}
		
	}
	
	private WordSorter sorter;
	private Map<Character, Byte> ansFreq;
	private int pattern_freq[];
	
	private double entropy(final List<Double> probs) {
		double s = 0.0;
		for(int i = 0; i < probs.size(); ++i) {
			Double d = probs.get(i) / 11454.0;
			if(d != 0.0) s += -d * Math.log(d) / Math.log(2.0);
		}
		return s;
	}
	
	private String pattern(final String test, final String query) {
		Arrays.fill(pattern_freq, 0);
		for(int i = 0; i < 5; ++i) ++pattern_freq[query.charAt(i) - 'A'];
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < 5; ++i) {
			if(test.charAt(i) == query.charAt(i)) {
				sb.append('2');
				--pattern_freq[test.charAt(i) - 'A'];
				continue;
			} else {
				if(pattern_freq[test.charAt(i) - 'A'] > 0) {
					sb.append('1');
					--pattern_freq[test.charAt(i) - 'A'];
					continue;
				}
				
				sb.append('0');
			}
		}
		return sb.toString();
	}
	
	private synchronized void clean_array() {
		if(parent.out.get(0) == (byte)'0') {
			parent.out.get(1, answer);
			parent.out.get(6, response);
			
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
	}
	
	private synchronized boolean shannon_predict() {
		
		boolean _finished = true;
		for(int i = 0; i < 5; ++i) if(response[i] != (byte)'2') _finished = false;
		if(_finished) {
			return true;
		}
		
		if(parent.array.possible.size() <= 0) {
			reset();
			justReset = true;
			return false;
		}
		
		if(justReset) justReset = false;
		
		for(String word : parent.array.possible) {
			for(String pattern : parent.array.all_patterns) {
				lookup.get(word).get(pattern).clear();
			}
		}
		
		for(String word : parent.array.possible) {
			for(String query : parent.array.possible) {
				String pattern = pattern(word, query);
				lookup.get(word).get(pattern).add(query);
			}
		}
		
		List<SWord> swords = new ArrayList<SWord>();
		
		for(String word : parent.array.possible) {
			probs.clear();
			for(String pattern : parent.array.all_patterns) {
				probs.add((double)lookup.get(word).get(pattern).size());
			}
			swords.add(new SWord(word, entropy(probs)));
		}
		
		Collections.sort(swords, sorter);
		SWord best = swords.get(swords.size() - 1);
		parent.array.possible.remove(best.word);
		parent.out.put(0, (byte)'1');
		parent.out.put(1, best.word.getBytes());
		text.setText(best.word);
		return false;
	}
	
}
