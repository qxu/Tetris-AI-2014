package com.qxu.tetris.ai;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.qxu.tetris.BlockData;
import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.TetrisGridSnapshot;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.scores.FinalRater;
import com.qxu.tetris.eval.Debug;
import com.qxu.tetris.gfx.TetrisGridJComponent;

public class AIRunner implements Runnable {
	private static String sss;
	private static boolean useSss = false;
	private static TetrisGridSnapshot snapshot;
	static {
		if (useSss && sss != null) {
			snapshot = TetrisGridSnapshot.fromString(sss);
		}
	}

	private static final double[] c = { -3.728937582015992,
			-20.019104358673093, -6.607740852355959, -3.6078561449050897,
			-1.5987364521026617 };

	private static final int gridHeight = 20;
	private static final int gridWidth = 10;

	private static final Tetromino[] TETROMINOES = Tetromino.values();

	private static final Random rand = new Random();

	private TetrisAI ai;
	private TetrisGrid grid;
	private TetrisGridJComponent comp;

	private int moveColumn;
	private TetrisBlock moveBlock;

	private Object moveLock = new Object();
	private boolean nextMove = false;

	private boolean saveMove = false;

	public AIRunner() {
		this.ai = new RaterAI(new FinalRater(c));
		this.grid = new TetrisGrid(gridHeight, gridWidth);
		if (snapshot != null) {
			this.grid = snapshot.createGrid();
		}

		this.comp = new TetrisGridJComponent(grid);
		comp.setCellSize(20);

		JFrame frame = new JFrame("Tetris");
		JPanel panel = new JPanel();
		panel.add(comp);
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
					System.out.println(grid.getColumnHeight(moveColumn));
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					BlockData data = moveBlock.getData();
					if (moveColumn + data.getWidth() < grid.getWidth()) {
						moveColumn++;
					}
					System.out.println(grid.getColumnHeight(moveColumn));
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
					saveMove = false;
					nextMove = true;
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					saveMove = true;
					nextMove = true;
				} else if (e.getKeyCode() == KeyEvent.VK_P) {
					TetrisGridSnapshot snapshot = new TetrisGridSnapshot(grid,
							moveBlock, moveColumn);
					System.out.println(snapshot.toString());
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
		int score = 0;
		while (true) {
			Tetromino t = TETROMINOES[rand.nextInt(TETROMINOES.length)];
			AIMove move = ai.getMove(new TetrisGrid(grid), t);
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
			comp.repaint();
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
			saveMove = false;

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
		AIRunner runner = new AIRunner();
		runner.run();
	}
}
