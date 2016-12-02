package com.qfree.obo.report.rest.client;

import java.io.Serializable;
import java.util.List;

public class OboStepManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private int numberOfProcessedPassages;
	private int numberOfFailedPassages;
	private int totalTimeSpent;
	private int childrenCount;
	//	private List<XXXXXXXXXXX> subStatistics;
	private List<OboStepQueue> queues;
	private List<ResourceLink> links;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfProcessedPassages() {
		return numberOfProcessedPassages;
	}

	public void setNumberOfProcessedPassages(int numberOfProcessedPassages) {
		this.numberOfProcessedPassages = numberOfProcessedPassages;
	}

	public int getNumberOfFailedPassages() {
		return numberOfFailedPassages;
	}

	public void setNumberOfFailedPassages(int numberOfFailedPassages) {
		this.numberOfFailedPassages = numberOfFailedPassages;
	}

	public int getTotalTimeSpent() {
		return totalTimeSpent;
	}

	public void setTotalTimeSpent(int totalTimeSpent) {
		this.totalTimeSpent = totalTimeSpent;
	}

	public int getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}

	public List<OboStepQueue> getQueues() {
		return queues;
	}

	public void setQueues(List<OboStepQueue> queues) {
		this.queues = queues;
	}

	public List<ResourceLink> getLinks() {
		return links;
	}

	public void setLinks(List<ResourceLink> links) {
		this.links = links;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OboStepManager [name=");
		builder.append(name);
		builder.append(", numberOfProcessedPassages=");
		builder.append(numberOfProcessedPassages);
		builder.append(", numberOfFailedPassages=");
		builder.append(numberOfFailedPassages);
		builder.append(", totalTimeSpent=");
		builder.append(totalTimeSpent);
		builder.append(", childrenCount=");
		builder.append(childrenCount);
		builder.append(", queues=");
		builder.append(queues);
		builder.append(", links=");
		builder.append(links);
		builder.append("]");
		return builder.toString();
	}

}
