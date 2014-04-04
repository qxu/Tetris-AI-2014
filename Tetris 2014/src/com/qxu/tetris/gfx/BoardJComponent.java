package com.qxu.tetris.gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

import tetris.Board;
import tetris.Piece;

@SuppressWarnings("serial")
public class BoardJComponent extends JComponent {

	private static final int cellSize = 20;
	private static final int borderSize = 1;

	private static final Color filledCellColor = Color.LIGHT_GRAY;
	private static final Color emptyCellColor = Color.DARK_GRAY;
	private static final Color moveCellColor = Color.CYAN;
	private static final Color borderColor = Color.BLACK;

	private volatile Board board;

	private volatile Piece movePiece;
	private volatile int moveY;
	private volatile int moveX;
	
	private int boardWidth;
	private int boardHeight;
	
	public BoardJComponent(int boardWidth, int boardHeight) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		initSize();
	}

	private void initSize() {
		Dimension size = calculateSize();
		setPreferredSize(size);
		setSize(size);
	}

	private Dimension calculateSize() {
		int totalLength = cellSize + borderSize;
		int width = boardWidth * totalLength;
		int height = boardHeight * totalLength;
		width += borderSize;
		height += borderSize;
		return new Dimension(width, height);
	}

	public synchronized void setBoard(Board b) {
		this.board = b;
	}

	public synchronized void setMovePiece(Piece p, int x, int y) {
		this.movePiece = p;
		this.moveY = y;
		this.moveX = x;
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		int totalLength = cellSize + borderSize;

		int componentWidth = boardWidth * totalLength + borderSize;
		int componentHeight = boardHeight * totalLength + borderSize;
		g.setColor(borderColor);
		g.fillRect(0, 0, componentWidth, componentHeight);

		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardHeight; y++) {
				if (board != null && board.getGrid(x, y)) {
					g.setColor(filledCellColor);
				} else {
					g.setColor(emptyCellColor);
				}
				int gX = x * totalLength + borderSize;
				int gY = (boardHeight - y - 1) * totalLength + borderSize;
				g.fillRect(gX, gY, cellSize, cellSize);
			}
		}

		if (movePiece != null) {
			g.setColor(moveCellColor);
			for (Point pt : movePiece.getBody()) {
				int gX = (moveX + pt.x) * totalLength + borderSize;
				int gY = (boardHeight - (moveY + pt.y) - 1) * totalLength
						+ borderSize;
				g.fillRect(gX, gY, cellSize, cellSize);
			}
		}
	}
}
