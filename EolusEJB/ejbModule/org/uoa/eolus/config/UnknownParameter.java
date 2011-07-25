package org.uoa.eolus.config;

public class UnknownParameter extends Exception {
	
	private static final long serialVersionUID = 5300739460918244496L;

	public UnknownParameter(String msg) {
		super(msg);
	}
	
	public UnknownParameter(String msg, Throwable e) {
		super(msg,e);
	}	

}
