package com.qxu.tetris.gfx;

import java.util.List;

import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.TetrisAI;

public class Lovetris extends TetrisRunner {
	public static void main(String[] args) {
		Lovetris love = new Lovetris(20, 10, true);
		love.nextMove = true;

		final Thread runner = Thread.currentThread();
		Thread interrupter = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
				runner.interrupt();
			}
		};
		interrupter.start();

		long start = System.nanoTime();
		love.run();
		long stop = System.nanoTime();

		love.aSyncUpdateThread.interrupt();
		love.frame.dispose();
		
		System.out.println(love.linesCleared / ((stop - start) / 1.0e9) + " lines per second");
		System.out.println(((double) (stop - start)) / love.linesCleared + " ns per line");
		System.out.println(((double) (stop - start)) / love.pieceCount + " ns per piece");
	}

	private int column;
	
	public Lovetris(int gridHeight, int gridWidth, boolean aSync) {
		super(gridHeight, gridWidth, aSync);
		this.column = 0;
		this.ai = new TetrisAI() {
			@Override
			public AIMove getMove(TetrisGrid grid, Tetromino t, List<Tetromino> next) {
				int c = column;
				if (c % grid.getWidth() == 0) {
					c = 0;
				}
				column = c + 1;
				return new AIMove(c, 1);
			}
		};
	}
	
	@Override
	public Tetromino getNewTetromino() {
		return Tetromino.I;
	}
}
