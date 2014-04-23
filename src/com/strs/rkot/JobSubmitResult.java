package com.strs.rkot;

public class JobSubmitResult {
	private int resultCode;
	private String id = "";
	private String description = "";

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	@Override
	public String toString() {
		return "JobSubmitResult{" + "resultCode=" + resultCode + " id=" + id + '}';
	}
}
