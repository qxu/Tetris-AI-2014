package com.qxu.tetris;
import java.util.ArrayList;
import java.util.List;

public class TetrisBlock {
	
	public static List<TetrisBlock> constructBlockChain(int baseHeight, int baseWidth, long baseData) {
		List<TetrisBlock> blockChain = new ArrayList<>(4);
		TetrisBlock baseBlock = new TetrisBlock(baseHeight, baseWidth, baseData);
		blockChain.add(baseBlock);

		TetrisBlock prevBlock = baseBlock;
		TetrisBlock block = getRotationRight(baseBlock);
		while (!block.equals(baseBlock)) {
			block.prevRotation = prevBlock;
			prevBlock.nextRotation = block;
			blockChain.add(block);
			
			prevBlock = block;
			block = getRotationRight(block);
		}
		
		baseBlock.prevRotation = prevBlock;
		prevBlock.nextRotation = baseBlock;
		
		return blockChain;
	}

	private static TetrisBlock getRotationRight(TetrisBlock block) {
		int height = block.getHeight();
		int width = block.getWidth();
		long newBits = 0L;
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				if (block.get(r, c)) {
					int newRow = width - c - 1;
					int newColumn = r;
					newBits |= 1L << (height * newRow + newColumn);
				}
			}
		}
		return new TetrisBlock(width, height, newBits);
	}

	private final long data;
	private final int height;
	private final int width;
	private int[] bottomPaddings;

	private TetrisBlock nextRotation;
	private TetrisBlock prevRotation;

	private TetrisBlock(int height, int width, long data) {
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Negative width or height");
		int bitLength = width * height;
		if (bitLength > 64)
			throw new IllegalArgumentException("Bit length too large");

		this.height = height;
		this.width = width;
		this.data = ((1L << bitLength) - 1L) & data;
		this.bottomPaddings = new int[width];
		for (int c = 0; c < width; c++) {
			int padding = 0;
			while (padding < height && !get(padding, c)) {
				padding++;
			}
			bottomPaddings[c] = padding;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean get(int row, int column) {
		checkBounds(row, column);
		return (data & (1L << row * width + column)) != 0;
	}

	public int getBottomPadding(int column) {
		return bottomPaddings[column];
	}

	public TetrisBlock getNextRotation() {
		return nextRotation;
	}

	public TetrisBlock getPrevRotation() {
		return prevRotation;
	}

	private void checkBounds(int row, int column) {
		if (row < 0 || row >= height)
			throw new IndexOutOfBoundsException("Row: " + row);
		if (column < 0 || column >= width)
			throw new IndexOutOfBoundsException("Column: " + column);
	}

	@Override
	public int hashCode() {
		int hash = height;
		hash = hash * 31 + width;
		hash = hash * 31 + (int) (data ^ (data >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TetrisBlock)) {
			return false;
		}

		TetrisBlock other = (TetrisBlock) o;
		return this.data == other.data && this.height == other.height
				&& this.width == other.width;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(height * (width + 1));
		for (int r = height - 1; r >= 0; r--) {
			for (int c = 0; c < width; c++) {
				sb.append(get(r, c) ? "1" : " ");
			}
			if (r == 0)
				break;
			sb.append("\n");
		}
		return sb.toString();
	}
}
