package com.qxu.tetris.ai;

import java.util.List;

import javax.security.auth.callback.LanguageCallback;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.newscores.ColumnTransitions;
import com.qxu.tetris.ai.newscores.Holes;
import com.qxu.tetris.ai.newscores.RowTransitions;
import com.qxu.tetris.ai.newscores.WellSums;

public class NewAI implements TetrisAI {
	@Override
	public AIMove getMove(TetrisGrid grid, Tetromino t, List<Tetromino> next) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final double[] w = new double[]{0.0,
            -4.500158825082766,
            3.4181268101392694,
            -3.2178882868487753,
            -9.348695305445199,
            -7.899265427351652,
            -3.3855972247263626};
	
	private static double getScore(TetrisGrid grid, TetrisBlock block, int moveHeight, int rowsCleared) {
        double lh = moveHeight + block.getData().getHeight() / 2.0;
        int re = rowsCleared;
        int rt = RowTransitions.getRowTransitions(grid);
        int ct = ColumnTransitions.getColumnTransitions(grid);
        int ho = Holes.getHoleCount(grid);
        int ws = WellSums.getWellSums(grid);
        return w[1]*lh+w[2]*re+w[3]*rt+w[4]*ct+w[5]*ho+w[6]*ws;
		
	}
}
