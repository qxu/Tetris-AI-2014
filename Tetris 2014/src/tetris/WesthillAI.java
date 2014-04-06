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
	private Future<Move> futureMove;

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
		if (futureMove == null) {
			searcher = new BoardSearcher(board, piece, heightLimit);
			futureMove = exec.submit(searcher);
		}
		try {
			Move move = futureMove.get();

			Board subBoard = new Board(board);
			subBoard.place(move);
			subBoard.clearRows();
			searcher = new BoardSearcher(subBoard, nextPiece, heightLimit);
			futureMove = exec.submit(searcher);
			
			return move;
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
