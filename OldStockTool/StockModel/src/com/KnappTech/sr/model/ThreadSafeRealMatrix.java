package com.KnappTech.sr.model;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;

public class ThreadSafeRealMatrix {
	private RealMatrix matrix = null;
	
	public ThreadSafeRealMatrix(int sideSize) {
		matrix = MatrixUtils.createRealMatrix(sideSize, sideSize);
	}
	
	public ThreadSafeRealMatrix(int rows,int columns) {
		matrix = MatrixUtils.createRealMatrix(rows, columns);
	}
	
	public synchronized void setEntry(int row,int col, double value) {
		matrix.setEntry(row, col, value);
	}
	
	public synchronized RealMatrix getMatrix() {
		return matrix;
	}
}
