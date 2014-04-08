package tetris;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import AIHelper.BoardRater;

/**
 * Westhill's implementation of a Tetris AI. The best move is found by searching
 * through each possible move considering the next piece (if present, a depth 2
 * search) and calculating the score of the sub-board. The best score is then
 * used to return the AI's move.
 * 
 * @author qxu, mcGIT123
 * 
 */
public class WesthillAI implements AI {
	private ExecutorService exec = Executors.newSingleThreadExecutor();
	private Future<BoardScore> futureScore;

	public WesthillAI() {
	}

	/**
	 * Finds the best move. The best move is found by searching through each
	 * possible move considering the next piece (if present, a depth 2 search)
	 * and calculating the score of the sub-board. The search is done by
	 * iterating through all possible orientations of a piece, then all the
	 * possible x-positions of the piece. The best score is then used to return
	 * the AI's move.
	 */
	@Override
	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int heightLimit) {
		if (futureScore == null) {
			futureScore = exec.submit(new BoardSearcherCallable(board, piece,
					heightLimit));
		}
		BoardScore score;
		try {
			score = futureScore.get();
		} catch (InterruptedException e) {
			score = BoardSearcher.bestBoardScore(board, piece, heightLimit);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
//		if (score.score < -1250) {
//			score = BoardSearcher.bestBoardScore2(board, piece, nextPiece,
//					heightLimit);
//		}
		Move move = score.move;
		if (move.piece == null) {
			move = new Move();
			move.x = 0;
			move.y = board.dropHeight(piece, 0);
			move.piece = piece;
		} else {
			Board subBoard = new Board(board);
			subBoard.place(move);
			subBoard.clearRows();
			futureScore = exec.submit(new BoardSearcherCallable(subBoard,
					nextPiece, heightLimit));
		}

		return move;
	}

	@Override
	public void setRater(BoardRater r) {
		throw new UnsupportedOperationException();
	}

	private static class BoardSearcherCallable implements Callable<BoardScore> {
		final Board board;
		final Piece piece;
		final int heightLimit;

		BoardSearcherCallable(Board b, Piece p, int height) {
			this.board = b;
			this.piece = p;
			this.heightLimit = height;
		}

		@Override
		public BoardScore call() throws Exception {
			return BoardSearcher.bestBoardScore(board, piece, heightLimit);
		}

	}
}
