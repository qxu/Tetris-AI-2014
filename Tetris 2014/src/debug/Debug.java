package debug;

import java.awt.Point;

import tetris.Piece;

public class Debug {
	public static void printPiece(Piece p) {
		Point[] body = p.getBody();
		System.out.println(" --- Piece: ---");
		for (Point pt : body) {
			System.out.println(pt);
		}
	}
}
