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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qxu.tetris.BlockData;
import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.TetrisGridSnapshot;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.NewAI;
import com.qxu.tetris.ai.TetrisAI;
import com.qxu.tetris.eval.Debug;

public class TetrisRunner implements Runnable {
	private static boolean save = false;
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

	private static final int gridHeight = 20;
	private static final int gridWidth = 10;

	private static final int seekSize = 1;

	private static final boolean aSyncGfxUpdate = true;

	private static final Tetromino[] TETROMINOES = Tetromino.values();

	private static final Random rand = new Random();

	private TetrisAI ai;
	private TetrisGrid grid;
	private TetrisGridJComponent comp;
	private TetrominoNextJComponent nextComp;
	private JLabel scoreLabel;

	private int moveColumn;
	private TetrisBlock moveBlock;
	private int dropRow;

	private Deque<Tetromino> next;

	private Object moveLock = new Object();
	private boolean nextMove = false;

	private boolean saveMove = true;

	private int score;

	public TetrisRunner() {
		this.ai = new NewAI();
		this.grid = new TetrisGrid(gridHeight, gridWidth);
		if (snapshot != null) {
			this.grid = snapshot.createGrid();
		}

		this.comp = new TetrisGridJComponent(grid);
		this.nextComp = new TetrominoNextJComponent(seekSize);
		this.scoreLabel = new JLabel("score: ");

		JFrame frame = new JFrame("Tetris");
		JPanel contentPanel = new JPanel();
		contentPanel
				.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		JPanel drawPanel = new JPanel();
		drawPanel.add(comp);
		drawPanel.add(nextComp);
		contentPanel.add(drawPanel);
		contentPanel.add(scoreLabel);
		frame.setContentPane(contentPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		comp.setFocusable(true);
		comp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (aSyncGfxUpdate) {
					if (e.getKeyCode() == KeyEvent.VK_DOWN
							|| e.getKeyCode() == KeyEvent.VK_ENTER
							|| e.getKeyCode() == KeyEvent.VK_SPACE) {
						nextMove = !nextMove;
						if (nextMove) {
							Debug.notifyAll(moveLock);
						}
					}
					return;
				}
				
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

		if (aSyncGfxUpdate) {
			Thread t = new Thread("async tetris gfx updater") {
				@Override
				public void run() {
					while (true) {
						try {
							comp.setMoveBlock(moveBlock, dropRow, moveColumn);
							nextComp.setNext(new ArrayList<>(next));
							comp.repaint();
							nextComp.repaint();
							scoreLabel.setText("score: " + score);
						} catch (Exception e) {
							// ignore concurrency exception for a-sync run
						}
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				}
			};
			t.start();
		}
	}

	@Override
	public void run() {
		next = new ArrayDeque<>(seekSize);
		for (int i = 0; i < seekSize; i++) {
			next.addLast(TETROMINOES[rand.nextInt(TETROMINOES.length)]);
		}

		score = 0;
		while (true) {
			Tetromino t = next.removeFirst();
			next.addLast(TETROMINOES[rand.nextInt(TETROMINOES.length)]);
			AIMove move = ai.getMove(new TetrisGrid(grid), t, new ArrayList<>(
					next));
			if (move == null)
				break;

			moveColumn = move.getColumn();
			moveBlock = t.getBlockChain().get(move.getOrientation());

			dropRow = grid.getDropRow(moveColumn, moveBlock);
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

			if (!aSyncGfxUpdate) {
				comp.setMoveBlock(moveBlock, dropRow, moveColumn);
				nextComp.setNext(new ArrayList<>(next));
				comp.repaint();
				nextComp.repaint();

				// TetrisGrid testGrid = new TetrisGrid(grid);
				// System.out.println("holes: " + Holes.getHoleCount(testGrid));
				// System.out.println("wells: " + Wells.getWellSums(testGrid));
				// System.out.println("ct: " +
				// ColumnTransitions.getColumnTransitionCount(testGrid));
				// System.out.println("rt: " +
				// RowTransitions.getRowTransitionCount(testGrid));
				// System.out.println();
			}
			while (!nextMove) {
				Debug.waitFor(moveLock);
				dropRow = grid.getDropRow(moveColumn, moveBlock);
				if (!aSyncGfxUpdate) {
					comp.setMoveBlock(moveBlock, dropRow, moveColumn);
					comp.repaint();
				}
			}

			if (save && saveMove) {
				File f = new File(savePath);
				try {
					FileOutputStream out = new FileOutputStream(f, true);
					TetrisGridSnapshot ss = new TetrisGridSnapshot(grid,
							moveBlock, dropRow, null);
					ss.writeTo(out);
					System.out.println("move serialized");
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			grid.addBlock(dropRow, moveColumn, moveBlock);
			moveBlock = null;
			if (!aSyncGfxUpdate) {
				nextMove = false;
				saveMove = true;
				
				comp.setMoveBlock(null, 0, 0);
				comp.repaint();
			}

			int rowsCleared = grid.clearFullRows();
			if (rowsCleared > 0) {
				score += rowsCleared;

				if (!aSyncGfxUpdate) {
					scoreLabel.setText("score: " + score);
					comp.repaint();
					Debug.sleep(50);
				}
			}
		}
	}

	public static void main(String[] args) {
		TetrisRunner runner = new TetrisRunner();
		runner.run();
	}
}
