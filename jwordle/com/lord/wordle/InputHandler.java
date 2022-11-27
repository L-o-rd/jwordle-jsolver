package com.lord.wordle;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener {

	public boolean keys[] = new boolean[65536];
	public boolean mouse_buttons[] = new boolean[2];
	public double mousex, mousey;
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousex = (double)e.getX() * 0.25;
		mousey = (double)e.getY() * 0.25;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) mouse_buttons[0] = true;
		else if(e.getButton() == MouseEvent.BUTTON3) mouse_buttons[1] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) mouse_buttons[0] = false;
		else if(e.getButton() == MouseEvent.BUTTON3) mouse_buttons[1] = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void focusGained(FocusEvent e) {}

	@Override
	public void focusLost(FocusEvent e) {
		for(int i = 0; i < keys.length; ++i) keys[i] = false;
		mouse_buttons[0] = false;
		mouse_buttons[1] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code > 0 && code < keys.length) keys[code] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code > 0 && code < keys.length) keys[code] = false;
	}

}
