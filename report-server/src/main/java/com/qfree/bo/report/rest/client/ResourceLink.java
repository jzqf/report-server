package com.qfree.bo.report.rest.client;

import java.io.Serializable;

public class ResourceLink implements Serializable {

	private static final long serialVersionUID = 1L;

    private String rel;
	private String href;

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResourceLink [rel=");
		builder.append(rel);
		builder.append(", href=");
		builder.append(href);
		builder.append("]");
		return builder.toString();
	}

}
