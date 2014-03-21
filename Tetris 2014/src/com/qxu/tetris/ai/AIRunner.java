package com.qxu.tetris.ai;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.qxu.tetris.ai.scores.FinalRater;
import com.qxu.tetris.eval.Eval;

public class AIRunner {
	private static final double[] c = { 3.003598243720936, -2.6008686506403857,
			-23.90707274385931, -16.37067481561546, -11.453920005901825,
			0.35985468486267336 };

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
