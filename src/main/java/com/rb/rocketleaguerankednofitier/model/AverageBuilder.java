package com.rb.rocketleaguerankednofitier.model;

public class AverageBuilder {

	private int sum;
	private int count;
	
	private AverageBuilder() {
		this.sum = 0;
		this.count = 0;
	}
	
	public static AverageBuilder newBuilder() {
		return new AverageBuilder();
	}
	
	public void addValue(int val) {
		this.sum += val;
		this.count++;
	}
	
	public double buildAverage() {
		return sum/count;
	}
}
