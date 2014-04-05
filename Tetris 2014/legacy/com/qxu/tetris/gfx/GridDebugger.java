package com.qxu.tetris.gfx;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridDebugger {
	private JFrame frame;
	private BoardJComponent comp;
	private PieceNextJComponent nextComp;
	
	public GridDebugger() {
		this.frame = new JFrame();
		this.comp = new BoardJComponent(10, 20);
		this.nextComp = new PieceNextJComponent(1);
		JPanel panel = new JPanel();
		panel.add(comp);
		panel.add(nextComp);
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public BoardJComponent getComp() {
		return comp;
	}
	
	public PieceNextJComponent getNextComp() {
		return nextComp;
	}
}
