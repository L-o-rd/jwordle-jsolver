package com.lord.wordle;

public class Screen extends Bitmap {
	
	private static final int XPAD = 18, YPAD = 18;
	private static final int XPOS = (JCanvas.WIDTH - XPAD * 2 * Game.XTILES) >> 1;
	private static final int YPOS = (JCanvas.HEIGHT - YPAD * 2 * Game.YTILES) >> 1;
	private static final int WXPOS = (JCanvas.WIDTH - XPAD * 2 * Game.XTILES) >> 1;
	private static final int WYPOS = (JCanvas.HEIGHT - YPAD * 2) >> 1;
	
	private Bitmap square;
	private int sx, sy;
	private float angle, fade, fadeInc, r;
	
	public boolean shouldClear;
	
	public Screen(int width, int height) {
		super(width, height);
		
		square = new Bitmap(50, 50);
		for(int i = 0; i < 50; ++i) {
			for(int j = 0; j < 50; ++j) {
				square.pixels[j + i * 50] = (((i + j) & 0xff) << 16) | (0x20 << 8) | ((i * j) & 0xff);
			}
		}
		
		shouldClear = true;
		
		sx = (JCanvas.WIDTH - 50) >> 1;
		sy = (JCanvas.HEIGHT - 50) >> 1;
		angle = 0.f; fade = 1.f; fadeInc = -0.0025f;
	}
	
	public void render(Game game) {
		
		if(game.finished) {
			
			if(fade > 1.f) {
				fadeInc = -fadeInc;
				fade = 1.f;
			} else if(fade < 0.f) {
				fadeInc = -fadeInc;
				fade = 0.f;
			}
			
			r = fade * (sx + 45.f);
			int tsx = (int)(r * Math.cos(angle));
			int tsy = (int)(r * Math.sin(angle));
			
			this.draw(square, sx + tsx, sy + tsy);
			
			angle += 0.1f;
			fade += fadeInc;
			
			for(int x = 0; x < Game.XTILES; ++x) {
				Tile tile = Tile.RIGHT_SPOT;
				int d = (int)(Math.sin(angle * (x + 1) * 0.35f) * 8);
				this.scaledDraw(Art.tiles, 2, WXPOS + x * XPAD * 2, WYPOS + d, tile.tx, tile.ty, 14, 16, 0xffffff);
			}
			
			for(int i = 0; i < game.answer.length(); ++i) {
				final int xp = WXPOS + i * XPAD * 2 + 7, yp = WYPOS + 7;
				int d = (int)(Math.sin(angle * (i + 1) * 0.35f) * 8);
				Font.render(this, game.answer.charAt(i), xp, yp + d, 2, 0xffffff);
			}
			
			String msg = "PRESS SPACE TO PLAY AGAIN!";
			final int msgxx = (JCanvas.WIDTH - msg.length() * 7) >> 1;
			
			Font.render(this, msg, msgxx + 1, 151, 0xd0d0d0);
			Font.render(this, msg, msgxx    , 151, 0xd0d0d0);
			Font.render(this, msg, msgxx    , 150, 0xffffff);
			Font.render(this, msg, msgxx + 1, 150, 0xffffff);
			
			msg = "YOU GUESSED IT IN: " + game.ntries + ((game.ntries == 1) ? " TRY!" : " TRIES!");
			final int msgtr = (JCanvas.WIDTH - msg.length() * 7) >> 1;
			
			Font.render(this, msg, msgtr + 1, 31, 0xd0d0d0);
			Font.render(this, msg, msgtr    , 31, 0xd0d0d0);
			Font.render(this, msg, msgtr    , 30, 0xffffff);
			Font.render(this, msg, msgtr + 1, 30, 0xffffff);
			
		} else renderGame(game);
		
	}
	
	private void renderGame(Game game) {
		for(int y = 0; y < Game.YTILES; ++y) {
			for(int x = 0; x < Game.XTILES; ++x) {
				Tile tile = game.tiles[x + y * Game.XTILES];
				this.scaledDraw(Art.tiles, 2, XPOS + x * XPAD * 2, YPOS + y * YPAD * 2, tile.tx, tile.ty, 14, 16, 0xffffff);
			}
		}
		
		String msg;
		
		for(int j = 0; j < 3; ++j) {
			if(game.ytile == j) continue;
			msg = game.history[j];
			for(int i = 0; i < msg.length(); ++i) {
				final int xp = XPOS + i * XPAD * 2 + 7, yp = YPOS + j * YPAD * 2 + 7;
				Font.render(this, msg.charAt(i), xp, yp, 2, 0xffffff);
			}
		}
		
		msg = game.typed.toString();
		
		for(int i = 0; i < msg.length(); ++i) {
			final int xp = XPOS + i * XPAD * 2 + 7, yp = YPOS + game.ytile * YPAD * 2 + 7;
			Font.render(this, msg.charAt(i), xp, yp, 2, 0xffffff);
		}
		
		Font.render(this, game.tries, 5, 5, 0xededed);
	}

}
