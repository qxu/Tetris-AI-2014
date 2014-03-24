package com.qxu.tetris;
import java.util.ArrayList;
import java.util.List;

public class TetrisBlock {
	
	public static List<TetrisBlock> constructBlockChain(BlockData baseData) {
		if (baseData == null)
			throw new NullPointerException();

		List<TetrisBlock> blockChain = new ArrayList<>(4);
		TetrisBlock baseBlock = new TetrisBlock(baseData);
		blockChain.add(baseBlock);
		
		TetrisBlock prevBlock = baseBlock;
		BlockData data = getRotationRight(baseData);
		while (!baseData.equals(data)) {
			TetrisBlock block = new TetrisBlock(data);
			block.prevRotation = prevBlock;
			prevBlock.nextRotation = block;
			blockChain.add(block);
			
			prevBlock = block;
			data = getRotationRight(data);
		}
		
		baseBlock.prevRotation = prevBlock;
		prevBlock.nextRotation = baseBlock;
		
		return blockChain;
	}

	private static BlockData getRotationRight(BlockData data) {
		int height = data.getHeight();
		int width = data.getWidth();
		long newBits = 0L;
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				if (data.get(r, c)) {
					int newRow = width - c - 1;
					int newColumn = r;
					newBits |= 1L << (height * newRow + newColumn);
				}
			}
		}
		return new BlockData(width, height, newBits);
	}

	private BlockData data;
	private TetrisBlock nextRotation;
	private TetrisBlock prevRotation;

	private TetrisBlock(BlockData data) {
		this.data = data;
	}

	public BlockData getData() {
		return data;
	}

	public TetrisBlock getNextRotation() {
		return nextRotation;
	}

	public TetrisBlock getPrevRotation() {
		return prevRotation;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
