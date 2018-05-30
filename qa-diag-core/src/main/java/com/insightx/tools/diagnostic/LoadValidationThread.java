package com.insightx.tools.diagnostic;

import java.util.ArrayList;
import java.util.List;


public class LoadValidationThread extends Thread {
	private boolean endlessExec;
	private long execCount;
	private long execDur;
	private long execInterval;
	private ExecutionProgressReporter reporter;
	
	private long threadId;
	private String result;
	private String processName;
	
	private float avgGlobalExecTime;
	private float avgLocalExecTime;
	private float maxLocalExecTime;
	private float minLocalExecTime;
	
	private float successExecIndicator;
	
	private LoadValidationInstanceInterface app;

	private List<Exception> exceptions;
	
	private boolean continueRunning;
	private boolean executionFinished;

	public LoadValidationThread(boolean endlessExec, long execCount, long execDur, long execInterval, LoadValidationInstanceInterface app, ExecutionProgressReporter reporter) {
		this.endlessExec = endlessExec;
		this.execCount = execCount;
		this.execDur = execDur;
		this.execInterval = execInterval;
		this.app = app;
		this.reporter = reporter;

		this.threadId = this.getId();

		avgGlobalExecTime = -1f;
		avgLocalExecTime = -1f;
		maxLocalExecTime = -1f;
		minLocalExecTime = -1f;
		successExecIndicator = 0f;
		
		setExceptions(new ArrayList<Exception>());
		continueRunning = true;
		executionFinished = false;
	}
	
	public final void run() {
		try {
			this.execute();
		} catch (Exception e) {
			getExceptions().add(e);
		}
	}

	private final void execute () throws Exception {
		long lMaxLocalExecTime = 0;
		long lMinLocalExecTime = 10000000000l;

		processName = this.getName();

		reporter.setThreadRunning((int)threadId, true);

		/* Global Execution Stats */
		long globalStartTime = 0l;
		long globalEndTime = 0l;
		float globalExecutionTime = 0f;

		long globalIntervalExecutionTime = 0l;

		long localStartTime = 0l;
		long localEndTime = 0l;

		long currentExecTime = 0l;
		
		long localExecutionTime = 0l;
		
		int execResult = LoadValidationInstanceInterface.FAILED;

		try {
			app.setup();
		} catch (Exception e) {
			getExceptions().add(e);
			reporter.setThreadRunning((int)threadId, false);

			result = "+- Process [" + getProcessName() + "]: Finished with error";
			reporter.setThreadRunning((int)threadId, false);
			return;
		}

		globalStartTime = System.nanoTime();

		int x=0;
		while ((endlessExec || x < execCount || currentExecTime < execDur) && continueRunning) {
			try {
				execResult = app.preValidate();
			} catch (Exception e){
				getExceptions().add(e);
			}
			localStartTime = System.nanoTime();
			try {
				execResult = app.validate();
			} catch (Exception e){
				getExceptions().add(e);
				execResult = LoadValidationInstanceInterface.FAILED;
			}
			localEndTime = System.nanoTime();
			if (execResult == LoadValidationInstanceInterface.SUCCESS) {
				try {
					execResult = app.postValidate();
				} catch (Exception e){
					getExceptions().add(e);
				}
			}
			long localElapsedTime = localEndTime - localStartTime;
			localExecutionTime += localElapsedTime;
			switch (execResult) {
				case LoadValidationInstanceInterface.SUCCESS:
					successExecIndicator++;
					break;
				case LoadValidationInstanceInterface.FAILED:
					break;
				case LoadValidationInstanceInterface.TIMEOUT:
					break;
				default:
					break;
			}
			if (localElapsedTime > lMaxLocalExecTime) {
				lMaxLocalExecTime = localElapsedTime;
			}
			if (localElapsedTime < lMinLocalExecTime) {
				lMinLocalExecTime = localElapsedTime;
			}
			currentExecTime = (System.nanoTime()-globalStartTime)/1000000000l;
			x++;
			reporter.addCurrentProgress((int)threadId, x, currentExecTime, ((float)(lMinLocalExecTime/1000l))/1000f, ((float)(lMaxLocalExecTime/1000l))/1000f, (float)localExecutionTime / 1000000f / (float)x);
			
			if (execInterval > 0) {
				long intervalStartTime = System.nanoTime();
				try {
					LoadValidationThread.sleep(execInterval);
				} catch (InterruptedException e) { System.out.println(e); getExceptions().add(e); }
				globalIntervalExecutionTime += System.nanoTime() - intervalStartTime;
			}
		}

		globalEndTime = System.nanoTime();
		try {
			app.tearDown();
		} catch (Exception e){
			getExceptions().add(e);
		}
		
		globalExecutionTime = (globalEndTime - globalStartTime - globalIntervalExecutionTime)/1000000f;
			
		avgGlobalExecTime = globalExecutionTime/x;
		avgLocalExecTime = (float)localExecutionTime / 1000000f / (float)x;
		maxLocalExecTime = ((float)(lMaxLocalExecTime/1000l))/1000f;
		minLocalExecTime = ((float)(lMinLocalExecTime/1000l))/1000f;
		successExecIndicator = (successExecIndicator*100f)/((float)x);
		
		result = "+- Process [" + getProcessName() + "]: Each execution took approximately: " + avgLocalExecTime + " mili seconds for " + x + " executions" + "\r\n" + 
			     "+- Process [" + getProcessName() + "]: The best and worst execuon took approximately: " + minLocalExecTime + " and " + maxLocalExecTime + " mili seconds";

		reporter.setThreadRunning((int)threadId, false);
		executionFinished = true;
	}

	public final String getResult() {
		return result;
	}

	public final float getAvgGlobalExecTime() {
		return avgGlobalExecTime;
	}

	public final float getAvgLocalExecTime() {
		return avgLocalExecTime;
	}

	public final float getMaxLocalExecTime() {
		return maxLocalExecTime;
	}

	public final float getMinLocalExecTime() {
		return minLocalExecTime;
	}

	public final String getProcessName() {
		return processName;
	}
	
	public final float getSuccessExecIndicator(){
		return successExecIndicator;
	}

	private void setExceptions(List<Exception> exceptions) {
		this.exceptions = exceptions;
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}
	
	public void haltExecution() {
		continueRunning = false;
	}
	
	public boolean executionFinished() {
		return executionFinished;
	}
}
