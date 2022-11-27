package com.lord.solver;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RandomPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected MainWindow parent;
	
	private JButton button;
	private JTextArea text;
	private Random rng;
	
	public RandomPanel(final int width, final int height, MainWindow _parent) {
		super();
		
		parent = _parent;
		rng = new Random();
		
		this.setPreferredSize(new Dimension(width, height));
		
		text = new JTextArea(1, 5);
		text.setEditable(false);
		text.setFont(new Font("Arial", Font.PLAIN, 48));
		this.add(text);
		
		button = new JButton("Predict");
		button.setPreferredSize(new Dimension(150, 75));
		button.addActionListener(event -> predict());
		this.add(button);
	}
	
	private void predict() {
		String randomWord = parent.array.words.get(rng.nextInt(parent.array.words.size()));
		parent.out.put((byte)'1');
		parent.out.put(randomWord.getBytes());
		text.setText(randomWord);
		parent.out.clear();
	}
	
}
