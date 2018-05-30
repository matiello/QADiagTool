package com.insightx.tools.diagnostic;

public abstract class LoadValidationInstanceInterface {
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	public static final int TIMEOUT = 3;
	
	public abstract void setup() throws Exception;
	public abstract int preValidate() throws Exception;
	public abstract int validate() throws Exception;
	public abstract int postValidate() throws Exception;
	public abstract void tearDown() throws Exception;

}
