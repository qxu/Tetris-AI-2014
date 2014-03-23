package com.qxu.tetris.eval;

import java.util.Random;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.TetrisAI;

public class Eval {
	private static final Tetromino[] TETROMINOES = Tetromino.values();

	private int gridHeight;
	private int gridWidth;

	private Random rand = new Random();

	public Eval(int gridHeight, int gridWidth) {
		this.gridHeight = gridHeight;
		this.gridWidth = gridWidth;
	}

	public double evalN(TetrisAI ai, int n) {
		double total = 0.0;
		for (int i = 0; i < n; i++) {
			int score = evalOnce(ai);
			total += (double) score / n;
		}
		return total;
	}

	public int evalOnce(TetrisAI ai) {
		TetrisGrid grid = new TetrisGrid(gridHeight, gridWidth);

		int score = 0;
		while (true) {
			Tetromino t = TETROMINOES[rand.nextInt(TETROMINOES.length)];
			AIMove move = ai.getMove(new TetrisGrid(grid), t);
			if (move == null)
				break;
			int column = move.getColumn();
			TetrisBlock block = t.getBlockChain().get(move.getOrientation());
			int dropRow = grid.getDropRow(column, block);
			if (dropRow >= grid.getHeight())
				break;

			grid.addBlock(dropRow, column, block);
			score += grid.clearFullRows();
		}

		return score;
	}
}
