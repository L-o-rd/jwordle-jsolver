package com.lord.wordle;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class Game {
	
	public static final int XTILES = 5, YTILES = 3;
	
	private InputHandler input;
	public StringBuffer typed;
	public boolean shouldType, finished;
	public int xtile = 0, ytile = 0;
	public Tile tiles[];
	public String history[];
	
	public WordArray wordArray;
	public String answer, tries;
	public int ntries;
	
	private final boolean playing;
	
	public Random rng;
	public MappedByteBuffer out;
	
	public Game(InputHandler in, final boolean _playing, RandomAccessFile mmap) {
		
		if(mmap != null) {
			try {
				out = mmap.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 11);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		input = in;
		rng = new Random();
		
		playing = _playing;
		
		bytes = new byte[5]; shouldGet = true; response = new byte[5];
		checkFreq = new int[26];
		typed = new StringBuffer("");
		shouldType = true;
		tiles = new Tile[XTILES * YTILES];
		for(int i = 1; i < XTILES * YTILES; ++i) tiles[i] = Tile.EMPTY;
		tiles[0] = Tile.SELECTED;
		history = new String[3];
		for(int i = 0; i < 3; ++i) history[i] = "";
		
		wordArray = new WordArray("/list.txt");
		answer = wordArray.words.get(rng.nextInt(wordArray.words.size()));
		finished = false;
		ntries = 0;
		tries = String.format("Tries: %d", ntries);
	}
	
	private final int checkFreq[];
	private int animTimer = 0;
	
	private boolean check() {
		
		String tested = typed.toString();
		
		if(!wordArray.words.contains(tested)) return false;
		
		Arrays.fill(checkFreq, 0);
		for(int i = 0; i < 5; ++i) ++checkFreq[(int)(answer.charAt(i) - 'A')];
		
		for(int i = 0; i < 5; ++i) {
			if(answer.charAt(i) == tested.charAt(i)) {
				--checkFreq[(int)(tested.charAt(i) - 'A')];
				tiles[i + ytile * XTILES] = Tile.RIGHT_SPOT;
			}
			else if(checkFreq[(int)(tested.charAt(i) - 'A')] > 0) {
				tiles[i + ytile * XTILES] = Tile.WRONG_SPOT;
				--checkFreq[(int)(tested.charAt(i) - 'A')];
			}
			else tiles[i + ytile * XTILES] = Tile.WRONG_CHAR;
		}
		
		return true;
	}
	
	private void reset() {
		Arrays.fill(bytes, (byte)0);
		Arrays.fill(response, (byte)'0');
		typed.delete(0, typed.length());
		shouldType = true; finished = false;
		xtile = ytile = 0;
		for(int i = 1; i < XTILES * YTILES; ++i) tiles[i] = Tile.EMPTY;
		tiles[0] = Tile.SELECTED;
		for(int i = 0; i < 3; ++i) history[i] = "";
		answer = wordArray.words.get(rng.nextInt(wordArray.words.size()));
		ntries = 0;
		tries = String.format("Tries: %d", ntries);
	}
	
	private void tickPlaying() {
		if(typed.length() == 5) {
			if(input.keys[KeyEvent.VK_ENTER]) {
				input.keys[KeyEvent.VK_ENTER] = false;
				
				if(!check()) return;
				
				boolean ok = true;
				for(int i = 0; i < 5; ++i) if(tiles[i + ytile * XTILES] != Tile.RIGHT_SPOT) ok = false;
				if(ok) finished = true;
				
				history[ytile] = typed.toString();
				shouldType = true;
				xtile = 0; ytile = (ytile + 1) % 3;
				for(int i = 0; i < XTILES; ++i) tiles[i + ytile * XTILES] = Tile.EMPTY;
				tiles[xtile + ytile * XTILES] = Tile.SELECTED;
				typed.delete(0, typed.length());
				
				++ntries;
				
				return;
			}
		}
		
		if(typed.length() > 0) {
			if(input.keys[KeyEvent.VK_BACK_SPACE]) {
				shouldType = true;
				for(int i = xtile; i < 5; ++i) tiles[i + ytile * XTILES] = Tile.EMPTY;
				if(xtile == 4 && typed.length() == 5) {
					tiles[xtile + ytile * XTILES] = Tile.SELECTED;
				}
				else tiles[--xtile + ytile * XTILES] = Tile.SELECTED;
				typed.deleteCharAt(typed.length() - 1);
				input.keys[KeyEvent.VK_BACK_SPACE] = false;
			}
		}
		
		if(shouldType) {
			for(int code = KeyEvent.VK_A; code <= KeyEvent.VK_Z; ++code) {
				if(input.keys[code]) {
					typed.append((char)('A' + (code - KeyEvent.VK_A)));
					tiles[xtile + ytile * XTILES] = Tile.EMPTY;
					if(xtile <= 3) tiles[++xtile + ytile * XTILES] = Tile.SELECTED;
					if(Math.random() < 0.5) Sound.type.play();
					else Sound.altt.play();
					input.keys[code] = false;
					break;
				}
			}
			
			if(typed.length() >= 5) shouldType = false;
		}
		
		if(animTimer > 10) {
			Tile tile = tiles[xtile + ytile * XTILES];
			
			if(tile == Tile.SELECTED) {
				tile.tx += 14;
				if(tile.tx >= 14 * 4) tile.tx = 0;
			} else {
				if(xtile < 4) {
					tile = tiles[xtile + 1 + ytile * XTILES];
					if(tile == Tile.SELECTED) {
						tile.tx += 14;
						if(tile.tx >= 14 * 4) tile.tx = 0;
					}
				}
			}
			
			animTimer = 0;
		} else ++animTimer;
	}
	
	private byte bytes[], response[];
	private boolean shouldGet;
	
	private void tickGuessing() {
		if(shouldGet) {
			out.get(1, bytes, 0, bytes.length);
			out.put(0, (byte)'0');
			
			typed.append(new String(bytes, StandardCharsets.UTF_8));
			check();
			
			for(int i = 0; i < 5; ++i) {
				Tile tile = tiles[i + ytile * XTILES];
				if(tile == Tile.RIGHT_SPOT) {
					response[i] = (byte)'2';
				} else if(tile == Tile.WRONG_CHAR) {
					response[i] = (byte)'0';
				} else if(tile == Tile.WRONG_SPOT) {
					response[i] = (byte)'1';
				}
			}
			
			out.put(6, response);
			
			boolean ok = true;
			for(int i = 0; i < 5; ++i) if(tiles[i + ytile * XTILES] != Tile.RIGHT_SPOT) ok = false;
			if(ok) finished = true;
			
			history[ytile] = typed.toString();
			xtile = 0; ytile = (ytile + 1) % 3;
			for(int i = 0; i < XTILES; ++i) tiles[i + ytile * XTILES] = Tile.EMPTY;
			tiles[xtile + ytile * XTILES] = Tile.SELECTED;
			typed.delete(0, typed.length());
			++ntries;
			
			shouldGet = false;
		}
	}
	
	public void tick() {
		
		if(finished) {
			if(input.keys[KeyEvent.VK_SPACE]) {
				if(!playing) out.put(0, (byte)'3');
				reset();
				Sound.type.play();
				input.keys[KeyEvent.VK_SPACE] = false;
			}
			return;
		}
		
		if(!playing) {
			shouldGet = (out.get(0) == (byte)'1') ? true : false;
		}
		
		tries = String.format("Tries: %d", ntries);
		
		if(playing) tickPlaying();
		else tickGuessing();
		
	}
	
}
