package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Blocks {
	public int getNumOfBlocks(TetrisGrid grid){
		int numOfBlocks = 0;
		for(int i = 0; i < grid.getWidth(); ++i){
			for(int r = 0; r < grid.getColumnHeight(i); ++r){
				if(grid.get(r,i)){
					++numOfBlocks;
				}
			}
		}
		return numOfBlocks;
	}
	public int getWeightedBlocks(TetrisGrid grid){
		int weightedBlocks = 0;
		for(int i = 0; i < grid.getWidth(); ++i){
			for(int r = 0; r < grid.getColumnHeight(i); ++r){
				if(grid.get(r,i)){
					weightedBlocks += (r+1);
				}
			}
		}
		return weightedBlocks;
	}
}
