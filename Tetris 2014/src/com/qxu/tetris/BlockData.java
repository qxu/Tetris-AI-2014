package com.qxu.tetris;
public class BlockData {
	private final long data;
	private final int height;
	private final int width;

	public BlockData(int height, int width, long data) {
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Negative width or height");
		int bitLength = width * height;
		if (bitLength > 64)
			throw new IllegalArgumentException("Bit length too large");

		this.height = height;
		this.width = width;
		this.data = ((1L << bitLength) - 1L) & data;
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

	public long get(int row) {
		if (row < 0 || row >= height)
			throw new IndexOutOfBoundsException("Row: " + row);
		return (data >>> (row * width)) & ((1L << width) - 1L);
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
		hash = hash * 31 + (int)(data ^ (data >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BlockData)) {
			return false;
		}

		BlockData other = (BlockData) o;
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
