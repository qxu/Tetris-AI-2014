package com.qxu.tetris.gfx;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Locale;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.TetrisGridSnapshot;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.Depth2AI;
import com.qxu.tetris.ai.TetrisAI;

public class TetrisRunner implements Runnable {

	public static void main(String[] args) {
		TetrisRunner runner = new TetrisRunner(10, 10, true);
		runner.ai = new Depth2AI();
		runner.run();
	}

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

	public int seekSize = 1;

	private final boolean aSyncGfxUpdate;
	public Thread aSyncUpdateThread;

	public TetrisAI ai;

	public TetrisGrid grid;
	public TetrisGridJComponent comp;
	public TetrominoNextJComponent nextComp;
	public JLabel scoreLabel;
	public JFrame frame;

	public int moveColumn;
	public TetrisBlock moveBlock;
	public int dropRow;

	public Deque<Tetromino> next;

	public Object moveLock = new Object();
	public boolean nextMove = false;

	public boolean saveMove = true;

	public int pieceCount;
	public int linesCleared;

	public TetrisRunner(int gridHeight, int gridWidth, boolean aSync) {
		this.grid = new TetrisGrid(gridHeight, gridWidth);
		this.aSyncGfxUpdate = aSync;
		if (snapshot != null) {
			this.grid = snapshot.createGrid();
		}

		this.comp = new TetrisGridJComponent(grid);
		this.nextComp = new TetrominoNextJComponent(seekSize);
		this.scoreLabel = new JLabel("score: 0");

		this.frame = new JFrame("Tetris");
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
							synchronized (moveLock) {
								moveLock.notifyAll();
							}
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
					if (moveColumn + moveBlock.getWidth() < grid.getWidth()) {
						moveColumn++;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					moveBlock = moveBlock.getNextRotation();
					if (moveColumn > grid.getWidth() - moveBlock.getWidth()) {
						moveColumn = grid.getWidth() - moveBlock.getWidth();
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

				synchronized (moveLock) {
					moveLock.notifyAll();
				}
			}
		});

		if (aSyncGfxUpdate) {
			aSyncUpdateThread = new Thread("async tetris gfx updater") {
				@Override
				public void run() {
					while (true) {
						try {
							comp.setMoveBlock(moveBlock, dropRow, moveColumn);
							nextComp.setNext(new ArrayList<>(next));
							comp.repaint();
							nextComp.repaint();
							scoreLabel.setText("score: "
									+ NumberFormat.getInstance(Locale.US)
											.format(linesCleared)
											.replace(",", " "));
						} catch (Exception e) {
							// ignore concurrency exception for a-sync run
						}
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			aSyncUpdateThread.start();
		}
	}

	private static final Tetromino[] TETROMINOES = Tetromino.values();
	private static final Random rand = new Random();

	public Tetromino getNewTetromino() {
		return TETROMINOES[rand.nextInt(TETROMINOES.length)];
	}

	protected boolean turnLoop() {
		Tetromino t;
		if (seekSize > 0) {
			t = next.removeFirst();
			next.addLast(getNewTetromino());
		} else {
			t = getNewTetromino();
		}
		AIMove move = ai
				.getMove(new TetrisGrid(grid), t, new ArrayList<>(next));
		if (move == null)
			return false;

		moveColumn = move.getColumn();
		moveBlock = t.getBlockChain().get(move.getOrientation());

		dropRow = grid.getDropRow(moveColumn, moveBlock);
		if (dropRow >= grid.getHeight())
			return false;

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
		}

		while (!nextMove) {
			synchronized (moveLock) {
				try {
					moveLock.wait();
				} catch (InterruptedException e) {
					return false;
				}
			}
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
				TetrisGridSnapshot ss = new TetrisGridSnapshot(grid, moveBlock,
						dropRow, null);
				ss.writeTo(out);
				System.out.println("move serialized");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int rowsCleared = grid.addAndClearRows(dropRow, moveColumn, moveBlock);
		pieceCount++;
		linesCleared += rowsCleared;

		moveBlock = null;

		if (!aSyncGfxUpdate) {
			nextMove = false;
			saveMove = true;

			comp.setMoveBlock(null, 0, 0);
			comp.repaint();

			if (rowsCleared > 0) {
				scoreLabel.setText("score: "
						+ NumberFormat.getInstance(Locale.US)
								.format(linesCleared).replace(",", " "));
			}
		}

		return true;
	}

	@Override
	public void run() {
		frame.setTitle("Tetris: " + grid.getWidth() + "x" + grid.getHeight()
				+ ", " + ai.getClass().getSimpleName());

		next = new ArrayDeque<>(seekSize);
		for (int i = 0; i < seekSize; i++) {
			next.addLast(getNewTetromino());
		}

		pieceCount = 0;
		linesCleared = 0;
		while (true) {
			boolean cont = turnLoop();
			if (!cont)
				break;
			if (Thread.interrupted())
				break;
		}
	}
}
