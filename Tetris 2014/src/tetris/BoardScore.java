package tetris;


/**
 * TODO: implement this
 * 
 * if the best score is too low, do a depth-2 board search from scratch
 * 
 * maybe implement a depth-2 search on a selected subset of the depth-1
 * sub-boards
 * 
 */
public class BoardScore implements Comparable<BoardScore> {
	public final Move move;
	public final double score;

	public BoardScore(Move move, double score) {
		this.move = move;
		this.score = score;
	}

	@Override
	public int compareTo(BoardScore other) {
		return Double.compare(score, other.score);
	}
}