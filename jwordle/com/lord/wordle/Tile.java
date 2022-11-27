package com.lord.wordle;

public enum Tile {
	EMPTY(0, 0),
	WRONG_SPOT(2, 0),
	RIGHT_SPOT(3, 0),
	WRONG_CHAR(1, 0),
	SELECTED(0, 1);
	
	public int tx, ty;
	
	private Tile(final int _tx, final int _ty) {
		tx = _tx * 14; ty = _ty * 16;
	}
	
}
