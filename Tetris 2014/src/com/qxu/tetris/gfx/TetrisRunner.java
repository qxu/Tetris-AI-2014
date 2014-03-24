package com.qxu.tetris.gfx;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.qxu.tetris.BlockData;
import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.TetrisGridSnapshot;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.RaterAI;
import com.qxu.tetris.ai.TetrisAI;
import com.qxu.tetris.ai.scores.FinalRater;
import com.qxu.tetris.eval.Debug;

public class TetrisRunner implements Runnable {
	private static String savePath = "saves.dat";
	
	private static String ssPath = "snapshot.dat";
	private static TetrisGridSnapshot snapshot;
	static {
		File f = new File(ssPath);
		if (f.exists()) {
			try {
				FileInputStream in = new FileInputStream(f);
				snapshot = new TetrisGridSnapshot(in);
				System.out.println("snapshot loaded");
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final double[] c = { -3.728937582015992,
			-20.019104358673093, -6.607740852355959, -3.6078561449050897,
			-1.5987364521026617 };

	private static final int gridHeight = 20;
	private static final int gridWidth = 10;
	
	private static final int seekSize = 1;

	private static final Tetromino[] TETROMINOES = Tetromino.values();

	private static final Random rand = new Random();

	private TetrisAI ai;
	private TetrisGrid grid;
	private TetrisGridJComponent comp;
	private TetrominoNextJComponent nextComp;

	private int moveColumn;
	private TetrisBlock moveBlock;
	
	private Deque<Tetromino> next;

	private Object moveLock = new Object();
	private boolean nextMove = false;

	private boolean saveMove = true;

	public TetrisRunner() {
		this.ai = new RaterAI(new FinalRater(c));
		this.grid = new TetrisGrid(gridHeight, gridWidth);
		if (snapshot != null) {
			this.grid = snapshot.createGrid();
		}

		this.comp = new TetrisGridJComponent(grid);
		this.nextComp = new TetrominoNextJComponent(seekSize);

		JFrame frame = new JFrame("Tetris");
		JPanel panel = new JPanel();
		panel.add(comp);
		panel.add(nextComp);
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		comp.setFocusable(true);
		comp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (moveBlock == null)
					return;

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (moveColumn > 0) {
						moveColumn--;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					BlockData data = moveBlock.getData();
					if (moveColumn + data.getWidth() < grid.getWidth()) {
						moveColumn++;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					moveBlock = moveBlock.getNextRotation();
					BlockData data = moveBlock.getData();
					if (moveColumn > grid.getWidth() - data.getWidth()) {
						moveColumn = grid.getWidth() - data.getWidth();
					}
					if (moveColumn < 0) {
						moveColumn = 0;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveMove = true;
					nextMove = true;
				} else if (e.getKeyCode() == KeyEvent.VK_COMMA) {
					saveMove = false;
					nextMove = true;
				} else if (e.getKeyCode() == KeyEvent.VK_SLASH) {
					TetrisGridSnapshot snapshot = new TetrisGridSnapshot(grid,
							moveBlock, moveColumn, new ArrayList<>(next));
					try {
						File f = new File(ssPath);
						FileOutputStream out = new FileOutputStream(f, false);
						snapshot.writeTo(out);
						out.flush();
						System.out.println("snapshot saved to: "
								+ f.getAbsolutePath());
						out.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					return;
				} else {
					return;
				}

				Debug.notifyAll(moveLock);
			}
		});
	}

	@Override
	public void run() {
		next = new ArrayDeque<>(seekSize);
		for (int i = 0; i < seekSize; i++) {
			next.addLast(TETROMINOES[rand.nextInt(TETROMINOES.length)]);
		}
		nextComp.setNext(new ArrayList<>(next));
		
		int score = 0;
		while (true) {
			Tetromino t = next.removeFirst();
			next.addLast(TETROMINOES[rand.nextInt(TETROMINOES.length)]);
			AIMove move = ai.getMove(new TetrisGrid(grid), t, new ArrayList<>(next));
			if (move == null)
				break;

			moveColumn = move.getColumn();
			moveBlock = t.getBlockChain().get(move.getOrientation());

			int dropRow = grid.getDropRow(moveColumn, moveBlock);
			if (dropRow >= grid.getHeight())
				break;

			if (snapshot != null) {
				if (snapshot.getMoveBlock() != null) {
					moveColumn = snapshot.getMoveColumn();
					moveBlock = snapshot.getMoveBlock();
					dropRow = grid.getDropRow(moveColumn, moveBlock);
				}
				snapshot = null;
			}

			comp.setMoveBlock(moveBlock, dropRow, moveColumn);
			nextComp.setNext(new ArrayList<>(next));
			comp.repaint();
			nextComp.repaint();
			while (!nextMove) {
				Debug.waitFor(moveLock);
				dropRow = grid.getDropRow(moveColumn, moveBlock);
				comp.setMoveBlock(moveBlock, dropRow, moveColumn);
				comp.repaint();
			}

			if (saveMove) {
			}

			grid.addBlock(dropRow, moveColumn, moveBlock);
			moveBlock = null;
			nextMove = false;
			saveMove = true;

			comp.setMoveBlock(null, 0, 0);
			comp.repaint();

			int rowsCleared = grid.clearFullRows();
			if (rowsCleared > 0) {
				score += rowsCleared;

				System.out.println("score: " + score);
				comp.repaint();
				Debug.sleep(50);
			}
		}

		System.out.println("score: " + score);
	}

	public static void main(String[] args) {
		TetrisRunner runner = new TetrisRunner();
		runner.run();
	}
}
