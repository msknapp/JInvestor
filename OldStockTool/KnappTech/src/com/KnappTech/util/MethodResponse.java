package com.KnappTech.util;

public class MethodResponse {
	private final boolean pass;
	private final Object responseArgument;
	private final Object[] responseArguments;
	private final String comment;
	
	public MethodResponse(boolean pass,Object responseArgument,Object[] responseArguments,String comment) {
		this.pass = pass;
		this.responseArgument = responseArgument;
		this.responseArguments = responseArguments;
		this.comment = comment;
	}

	public boolean isPass() {
		return pass;
	}

	public Object getResponseArgument() {
		return responseArgument;
	}

	public Object[] getResponseArguments() {
		return responseArguments;
	}

	public String getComment() {
		return comment;
	}
	
	public String toString() {
		return "this "+(pass ? "passed. " : "did not pass. ")+comment;
	}
}