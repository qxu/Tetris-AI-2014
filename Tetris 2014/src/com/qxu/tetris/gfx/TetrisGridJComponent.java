package com.qxu.tetris.gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import com.qxu.tetris.BlockData;
import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;

@SuppressWarnings("serial")
public class TetrisGridJComponent extends JComponent {

	private static final int cellSize = 20;
	private static final int borderSize = 1;

	private static final Color filledCellColor = Color.LIGHT_GRAY;
	private static final Color emptyCellColor = Color.DARK_GRAY;
	private static final Color moveCellColor = Color.CYAN;
	private static final Color borderColor = Color.BLACK;

	private TetrisGrid grid;

	private TetrisBlock moveBlock;
	private int moveRow;
	private int moveColumn;

	public TetrisGridJComponent(TetrisGrid grid) {
		this.grid = grid;
		initSize();
	}

	private void initSize() {
		Dimension size = calculateSize();
		setPreferredSize(size);
		setSize(size);
	}

	private Dimension calculateSize() {
		int totalLength = cellSize + borderSize;
		int width = grid.getWidth() * totalLength;
		int height = grid.getHeight() * totalLength;
		width += borderSize;
		height += borderSize;
		return new Dimension(width, height);
	}

	public void setMoveBlock(TetrisBlock block, int r, int c) {
		this.moveBlock = block;
		this.moveRow = r;
		this.moveColumn = c;
	}

	@Override
	protected void paintComponent(Graphics g) {
		int totalLength = cellSize + borderSize;
		int gridWidth = grid.getWidth();
		int gridHeight = grid.getHeight();

		int componentWidth = gridWidth * totalLength + borderSize;
		int componentHeight = gridHeight * totalLength + borderSize;
		g.setColor(borderColor);
		g.fillRect(0, 0, componentWidth, componentHeight);

		for (int c = 0; c < gridWidth; c++) {
			for (int r = 0; r < gridHeight; r++) {
				if (grid.get(r, c)) {
					g.setColor(filledCellColor);
				} else {
					g.setColor(emptyCellColor);
				}
				int x = c * totalLength + borderSize;
				int y = (gridHeight - r - 1) * totalLength + borderSize;
				g.fillRect(x, y, cellSize, cellSize);
			}
		}

		if (moveBlock != null) {
			g.setColor(moveCellColor);
			BlockData data = moveBlock.getData();
			int blockWidth = data.getWidth();
			int blockHeight = data.getHeight();
			for (int c = 0; c < blockWidth; c++) {
				for (int r = 0; r < blockHeight; r++) {
					if (data.get(r, c)) {
						int x = (moveColumn + c) * totalLength + borderSize;
						int y = (gridHeight - (moveRow + r) - 1) * totalLength
								+ borderSize;
						g.fillRect(x, y, cellSize, cellSize);
					}
				}
			}
		}
	}
}
