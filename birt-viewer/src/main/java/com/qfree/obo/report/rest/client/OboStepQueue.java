package com.qfree.obo.report.rest.client;

import java.io.Serializable;

public class OboStepQueue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String queueName;
	private int capacity;
	private int currentSize;
	private int currentUtilization;

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}

	public int getCurrentUtilization() {
		return currentUtilization;
	}

	public void setCurrentUtilization(int currentUtilization) {
		this.currentUtilization = currentUtilization;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OboStepQueue [queueName=");
		builder.append(queueName);
		builder.append(", capacity=");
		builder.append(capacity);
		builder.append(", currentSize=");
		builder.append(currentSize);
		builder.append(", currentUtilization=");
		builder.append(currentUtilization);
		builder.append("]");
		return builder.toString();
	}

}
