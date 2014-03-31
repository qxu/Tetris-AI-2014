package com.qxu.tetris.gfx;

import com.qxu.tetris.ai.Depth1AI;
import com.qxu.tetris.ai.NewAI2;

public class TimeTetrisRunner extends TetrisRunner {
	public static void main(String[] args) {
		TimeTetrisRunner runner1 = new TimeTetrisRunner(20, 10, true);
		runner1.ai = new Depth1AI();
		Thread t1 = new Thread(runner1);
		
		TimeTetrisRunner runner2 = new TimeTetrisRunner(20, 10, true);
		runner2.ai = new NewAI2();
		Thread t2 = new Thread(runner2);
		
		t1.start();
		t2.start();
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

		System.out.println("[" + ai.getClass().getSimpleName() + "]");
		System.out.println(linesCleared / ((stop - start) / 1.0e9) + " lines per second");
		System.out.println(((double) (stop - start)) / linesCleared + " ns per line");
		System.out.println(((double) (stop - start)) / pieceCount + " ns per piece");
	}
}
