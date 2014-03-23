package com.qxu.tetris.eval;


public class Debug {
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
	}
	
	public static void notifyAll(Object o) {
		synchronized (o) {
			o.notifyAll();
		}
	}
	
	public static void waitFor(Object o) {
		synchronized (o) {
			try {
				o.wait();
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		}
	}
}
