package com.qxu.tetris.gfx;

import java.util.List;

import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.TetrisAI;

public class Lovetris extends TetrisRunner {
	public static void main(String[] args) {
		TetrisRunner runner = new Lovetris(20, 10, true);
		Thread t = new Thread(runner);
		long start = System.nanoTime();
		runner.nextMove = true;
		t.start();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long stop = System.nanoTime();
		t.interrupt();
		runner.frame.dispose();
		System.out.println(runner.linesCleared / ((stop - start) / 1.0e9) + " lines per second");
		System.out.println(((double) (stop - start)) / runner.linesCleared + " ns per line");
		System.out.println(((double) (stop - start)) / runner.pieceCount + " ns per piece");
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
