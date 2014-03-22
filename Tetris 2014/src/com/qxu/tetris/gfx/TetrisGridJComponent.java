package com.qxu.tetris.gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import com.qxu.tetris.TetrisGrid;

@SuppressWarnings("serial")
public class TetrisGridJComponent extends JComponent {
	private static final Color TILE_COLOR = new Color(0xafeeee);
	private static final Color PREV_TILE_COLOR = new Color(0x008080);
	private static final Color EMPTY_TILE_COLOR = Color.DARK_GRAY;
	private static final Color BORDER_COLOR = Color.BLACK;
	
	private TetrisGrid grid;
	private int tileLength = 16;
	private int borderLength = 2;
	private boolean drawOutsideBorders = true;
	
	private TetrisGrid existingGrid;
	
	public TetrisGridJComponent(TetrisGrid grid) {
		setGrid(grid);
	}

	public TetrisGrid getGrid() {
		return grid;
	}
	
	public void setGrid(TetrisGrid grid) {
		if (grid == null)
			throw new NullPointerException("Null grid");
		this.grid = grid;
		this.existingGrid = new TetrisGrid(grid);
		recalculateSize();
	}
	
	public int getTileLength() {
		return tileLength;
	}

	public void setTileLength(int length) {
		if (length < 0)
			throw new IllegalArgumentException("Negative length");
		this.tileLength = length;
		recalculateSize();
	}

	public int getBorderLength() {
		return borderLength;
	}

	public void setBorderLength(int length) {
		if (length < 0)
			throw new IllegalArgumentException("Negative length");
		this.borderLength = length;
		recalculateSize();
	}

	public boolean getDrawOutsideBorders() {
		return drawOutsideBorders;
	}

	public void setDrawOutsideBorders(boolean drawOutsideBorders) {
		this.drawOutsideBorders = drawOutsideBorders;
		recalculateSize();
	}

	private void recalculateSize() {
		Dimension size = calculateSize();
		setPreferredSize(size);
		setSize(size);
	}
	
	private Dimension calculateSize() {
		int totalLength = tileLength + borderLength;
		int width = grid.getWidth() * totalLength;
		int height = grid.getHeight() * totalLength;
		if (drawOutsideBorders) {
			width += borderLength;
			height += borderLength;
		} else {
			width -= borderLength;
			height -= borderLength;
		}
		return new Dimension(width, height);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Dimension size = calculateSize();
		g.setColor(BORDER_COLOR);
		g.fillRect(0, 0, size.width, size.height);
		
		int totalLength = tileLength + borderLength;
		int gridHeight = grid.getHeight();
		int gridWidth = grid.getWidth();
		
		for (int r = 0; r < gridHeight; r++) {
			for (int c = 0; c < gridWidth; c++) {
				boolean tile = grid.get(r, c);
				int x = c * totalLength;
				int y = (gridHeight - r - 1) * totalLength;
				if (drawOutsideBorders) {
					x += borderLength;
					y += borderLength;
				} else {
					x -= borderLength;
					y -= borderLength;
				}
				
				if (tile) {
					if (existingGrid.get(r, c)) {
						g.setColor(TILE_COLOR);
					} else {
						existingGrid.set(r, c);
						g.setColor(PREV_TILE_COLOR);
					}
				} else {
					existingGrid.clear(r, c);
					g.setColor(EMPTY_TILE_COLOR);
				}
				g.fillRect(x, y, tileLength, tileLength);
			}
		}
		
	}
}
