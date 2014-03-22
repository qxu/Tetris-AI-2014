package com.qxu.tetris.eval;

import javax.swing.JFrame;

import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.gfx.TetrisGridJComponent;

public class Debug {
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public static TetrisGridJComponent createComp(TetrisGrid grid) {
		TetrisGridJComponent comp = new TetrisGridJComponent(grid);
		JFrame frame = new JFrame("Tetris");
		frame.getContentPane().add(comp);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		return comp;
	}
}
