package tetris;

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
	private BoardSearcher searcher;
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
			searcher = new BoardSearcher(board, piece, heightLimit);
			futureScore = exec.submit(searcher);
		}
		try {
			BoardScore score = futureScore.get();
			System.out.println(score.score);
			if (score.score < -800) {
				BoardSearcher2 d2 = new BoardSearcher2(board, piece, nextPiece,
						heightLimit);
				score = d2.call();
				System.out.println(" -> " + score.score);
			}
			Board subBoard = new Board(board);
			subBoard.place(score.move);
			subBoard.clearRows();
			searcher = new BoardSearcher(subBoard, nextPiece, heightLimit);
			futureScore = exec.submit(searcher);
			return score.move;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setRater(BoardRater r) {
		throw new UnsupportedOperationException();
	}
}
