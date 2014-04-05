package tetris;

public class DebugBoard extends Board {

	public DebugBoard(int aWidth, int aHeight) {
		super(aWidth, aHeight);
	}
	
	public boolean[][] getGridInternal() {
		return grid;
	}
	
}
