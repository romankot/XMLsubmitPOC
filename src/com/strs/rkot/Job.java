package com.strs.rkot;

public class Job {
	private String submitType;
	private String externalJobId;
	private String filePath;
	private String attributeId;
	private String attributeValue;

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
		
	public String getExternalJobId() {
		return externalJobId;
	}

	public void setExternalJobId(String externalJobId) {
		this.externalJobId = externalJobId;
	}
	

	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}

}
