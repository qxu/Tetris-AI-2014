package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class AltitudeDifference {
	public static int getAltitudeDifference(TetrisGrid grid){
		int tallestHeight = getPileHeight(grid);
		int lowestHeight = grid.getColumnHeight(0); //gets height of first column as reference
		for(int i = 0; i < grid.getWidth(); ++i){
			if(grid.getColumnHeight(i) < lowestHeight){
				lowestHeight = grid.getColumnHeight(i);
			}
		}
		return tallestHeight - lowestHeight;
				
	}
	public static int getPileHeight(TetrisGrid grid){
		int pileHeight = 0;//pileHeight is the highest height
		for(int i = 0; i < grid.getWidth(); ++i){
			if(grid.getColumnHeight(i) > pileHeight){
				pileHeight = grid.getColumnHeight(i);
			}
		}
		return pileHeight;
	}
}
