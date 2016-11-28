package com.qfree.obo.report.rest.client;

import java.io.Serializable;

public class OboFlowStepManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private OboFlowManager oboFlowManager;
	private OboStepManager oboStepManager;

	public OboFlowManager getOboFlowManager() {
		return oboFlowManager;
	}

	public void setOboFlowManager(OboFlowManager oboFlowManager) {
		this.oboFlowManager = oboFlowManager;
	}

	public OboStepManager getOboStepManager() {
		return oboStepManager;
	}

	public void setOboStepManager(OboStepManager oboStepManager) {
		this.oboStepManager = oboStepManager;
	}

}
