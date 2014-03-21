package com.qxu.tetris.ai;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.qxu.tetris.ai.scores.FinalRater;
import com.qxu.tetris.eval.Eval;

public class AIRunner {
	private static final double[] c = { -3.728937582015992,
			-20.019104358673093, -6.607740852355959, -3.6078561449050897,
			-1.5987364521026617 };

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
