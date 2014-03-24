package com.qxu.tetris.ai;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.qxu.tetris.ai.scores.FinalRater;
import com.qxu.tetris.eval.Eval;

public class AIRunner {
	private static final double[] c = { -4.7374263131618495,
			-27.32919796347618, 0.8786045827865605, -0.25860236442089146,
			-2.2918267447948453, 5.144797187566756, 1.1574416939020156 };

	public static void main(String[] args) {
		final Object buttonLock = new Object();
		JFrame frame = new JFrame();
		final JButton button = new JButton();
		button.setText("Step");
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (buttonLock) {
					buttonLock.notifyAll();
				}
			}
		});
		frame.getContentPane().add(button);
		frame.pack();
		frame.setVisible(true);

		RaterAI ai = new RaterAI(new FinalRater(c));
		Eval eval = new Eval(20, 10);
		int score = eval.evalAndDisplayOnce(ai, new Runnable() {
			@Override
			public void run() {
				synchronized (buttonLock) {
					try {
						buttonLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		System.out.println("score: " + score);
	}
}
