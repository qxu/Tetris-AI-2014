package com.qxu.tetris.ai;

import java.util.Iterator;
import java.util.List;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;

public class RaterAI implements TetrisAI {
	private FinalRater rater;

	public RaterAI(FinalRater rater) {
		this.rater = rater;
	}

	@Override
	public AIMove getMove(TetrisGrid grid, Tetromino t, List<Tetromino> next) {
		double bestScore = Double.NEGATIVE_INFINITY;

		int bestColumn = -1;
		int bestOrientation = -1;

		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getHeight() > grid.getHeight()) {
					continue;
				}
				TetrisGrid subGrid1 = new TetrisGrid(grid);
				subGrid1.addBlock(row, c, block);
				subGrid1.clearFullRows();

				for (TetrisGrid subGrid2 : iterateMoves(subGrid1, next.get(0))) {
					double score = rater.rate(subGrid2);

					if (score > bestScore) {
						bestScore = score;
						bestOrientation = or;
						bestColumn = c;
					}
				}
			}
		}

		return (bestColumn >= 0) ? new AIMove(bestColumn, bestOrientation)
				: null;
	}

	private static Iterable<TetrisGrid> iterateMoves(TetrisGrid grid,
			Tetromino t) {
		return new MoveIterable(grid, t);
	}

	private static class MoveIterable implements Iterable<TetrisGrid> {
		private final TetrisGrid grid;
		private final TetrisBlock firstBlock;

		MoveIterable(TetrisGrid grid, Tetromino t) {
			this.grid = grid;
			this.firstBlock = t.getFirstRotation();
		}

		@Override
		public Iterator<TetrisGrid> iterator() {
			return new MoveIterator();
		}

		private class MoveIterator implements Iterator<TetrisGrid> {
			private TetrisBlock curBlock;
			private int c;
			private int maxCol;
			private int dropRow;

			MoveIterator() {
				this.curBlock = firstBlock;
				do {
					this.maxCol = grid.getWidth() - curBlock.getWidth();
					for (this.c = 0; c <= maxCol; this.c++) {
						this.dropRow = grid.getDropRow(c, curBlock);
						if (dropRow + curBlock.getHeight() <= grid.getHeight()) {
							return;
						}
					}
					curBlock = curBlock.getNextRotation();
				} while (!curBlock.equals(firstBlock));
				this.curBlock = null;
			}

			private void incrementVars() {
				this.c++;
				while (c <= maxCol) {
					this.dropRow = grid.getDropRow(c, curBlock);
					if (dropRow + curBlock.getHeight() <= grid.getHeight())
						return;
					this.c++;
				}
				this.curBlock = curBlock.getNextRotation();
				while (!curBlock.equals(firstBlock)) {
					this.c = 0;
					this.maxCol = grid.getWidth() - curBlock.getWidth();
					while (c <= maxCol) {
						this.dropRow = grid.getDropRow(c, curBlock);
						if (dropRow + curBlock.getHeight() <= grid.getHeight())
							return;
						this.c++;
					}
					this.curBlock = curBlock.getNextRotation();
				}
				this.curBlock = null;
			}

			@Override
			public boolean hasNext() {
				return curBlock != null;
			}

			@Override
			public TetrisGrid next() {
				TetrisGrid next = new TetrisGrid(grid);
				next.addBlock(dropRow, c, curBlock);
				next.clearFullRows();

				incrementVars();
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}
}
