package com.qxu.tetris.gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import javax.swing.JComponent;

import tetris.Piece;

@SuppressWarnings("serial")
public class PieceNextJComponent extends JComponent {

	private static final int cellSize = 20;
	private static final int borderSize = 1;

	private static final Color filledCellColor = Color.LIGHT_GRAY;
	private static final Color borderColor = Color.BLACK;
	
	private int nextSize;
	private List<Piece> next;
	
	public PieceNextJComponent(int nextSize) {
		this.nextSize = nextSize;
		initSize();
	}

	private void initSize() {
		Dimension size = calculateSize();
		setPreferredSize(size);
		setSize(size);
	}

	private Dimension calculateSize() {
		int totalLength = cellSize + borderSize;
		int width = 4 * totalLength;
		int height = (5 * nextSize - 1) * totalLength;
		if (height < 0) {
			height = 0;
		}
		width += borderSize;
		height += borderSize;
		return new Dimension(width, height);
	}
	
	public void setNext(List<Piece> next) {
		this.next = next;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (next == null)
			return;
		
		int totalLength = cellSize + borderSize;
		
		int minSize = Math.min(nextSize, next.size());
		for (int i = 0; i < minSize; i++) {
			int yOff = (5 * totalLength) * i;
			int componentWidth = 4 * totalLength + borderSize;
			int componentHeight = 4 * totalLength + borderSize;
			g.setColor(borderColor);
			g.fillRect(0, yOff, componentWidth, componentHeight);

			Piece p = next.get(i);
			if (p == null)
				break;
			g.setColor(filledCellColor);
			for (Point pt : p.getBody()) {
				int gX = pt.x * totalLength + borderSize;
				int gY = (4 - pt.y - 1) * totalLength + borderSize;
				g.fillRect(gX, yOff + gY, cellSize, cellSize);
			}
		}
	}
}
