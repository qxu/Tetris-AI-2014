package com.qxu.tetris.gfx;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tetris.Board;
import tetris.Piece;

public class GridDebugger {
	private JFrame frame;
	private BoardJComponent comp;
	
	public GridDebugger() {
		this.frame = new JFrame();
		this.comp = new BoardJComponent(10, 20);
		JPanel panel = new JPanel();
		panel.add(comp);
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void setBoard(Board b) {
		comp.setBoard(b);
	}
	
	public void setMovePiece(Piece p, int x, int y) {
		comp.setMovePiece(p, x, y);
	}
	
	public void repaint() {
		System.out.println("repaint called: " + Thread.currentThread());
		comp.repaint();
	}
}
