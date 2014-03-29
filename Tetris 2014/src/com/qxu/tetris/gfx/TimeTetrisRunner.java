package com.qxu.tetris.gfx;

import com.qxu.tetris.ai.Depth2AI;

public class TimeTetrisRunner extends TetrisRunner {
	public static void main(String[] args) {
		TimeTetrisRunner runner = new TimeTetrisRunner(20, 10, true);
		runner.ai = new Depth2AI();
		runner.run();
	}
	
	public TimeTetrisRunner(int gridHeight, int gridWidth, boolean aSync) {
		super(gridHeight, gridWidth, aSync);
	}
	
	
	@Override
	public void run() {
		nextMove = true;
		final Thread runner = Thread.currentThread();
		Thread interrupter = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
				runner.interrupt();
			}
		};
		interrupter.start();
		long start = System.nanoTime();
		super.run();
		long stop = System.nanoTime();
		aSyncUpdateThread.interrupt();
		frame.dispose();

		System.out.println(linesCleared / ((stop - start) / 1.0e9) + " lines per second");
		System.out.println(((double) (stop - start)) / linesCleared + " ns per line");
		System.out.println(((double) (stop - start)) / pieceCount + " ns per piece");
	}
}
