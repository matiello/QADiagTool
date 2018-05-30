package com.insightx.tools.diagnostic;

import java.text.DecimalFormat;

public class ExecutionProgressReporter extends Thread {
	
	private long totalToExecute;
	private long totalDurationToExecute;
	private long [] currentProgress = new long[10000];
	private long [] currentTimeProgress = new long[10000];
	private float [] minExecTime = new float[10000];
	private float [] maxExecTime = new float[10000];
	private float [] avgExecTime = new float[10000];
	private boolean [] threadsRunning = new boolean[10000];
	private int maxId;
	private DecimalFormat df;
	private DecimalFormat df3Nbr;
	private DecimalFormat df5Nbr;
	private DecimalFormat dfVar;
	
	public void run () {
		try {
			sleep(500);
		} catch (InterruptedException e) {
		}

		long currentProgressExecuted = 0l;
		long currentTimeProgressExecuted = 0l;
		boolean anyThreadRunning = false;
		
		int threadsRunningCount = 0;
		String threadsRunningStr = "";

		float minExecTimeVal = 0f;
		float maxExecTimeVal = 0f;
		float avgExecTimeVal = 0f;

		for (int i=0; i<=maxId; i++) {
			anyThreadRunning |= threadsRunning[i];
			if (threadsRunning[i]) {
				threadsRunningCount++;
			}
		}
		threadsRunningStr = df3Nbr.format(threadsRunningCount);
		int availableThreads = 0;

		float progress;
		while (anyThreadRunning) {
			currentProgressExecuted = 0l;
			currentTimeProgressExecuted = 0l;
			availableThreads = 0;
			minExecTimeVal = -1f;
			maxExecTimeVal = -1f;
			avgExecTimeVal = 0f;
			for (int i=0; i<=maxId; i++) {
				currentProgressExecuted += currentProgress[i];
				if (currentTimeProgress[i] != 00) {
					availableThreads++;
					currentTimeProgressExecuted += (currentTimeProgress[i]);
				}
				if ((minExecTimeVal == -1f) && (minExecTime[i] != 0)) {
					minExecTimeVal = minExecTime[i];
				} else if ((minExecTimeVal != -1f) && (minExecTime[i] != 0)) {
					minExecTimeVal = Math.min(minExecTimeVal, minExecTime[i]);
				}
				if (maxExecTime[i] != 0) {
					maxExecTimeVal = Math.max(maxExecTimeVal, maxExecTime[i]);
				}
				avgExecTimeVal += avgExecTime[i];
			}
			if (availableThreads > 0) { 
				currentTimeProgressExecuted = currentTimeProgressExecuted/availableThreads;
			} else {
				currentTimeProgressExecuted = 0;
			}
			avgExecTimeVal = avgExecTimeVal/threadsRunningCount;
			progress = 100f;
			if (totalToExecute > 0 && totalDurationToExecute > 0) {
				progress = ((Math.min(100, (float)currentProgressExecuted*100f/(float)totalToExecute)) + (Math.min(100, (float)currentTimeProgressExecuted*100f/(float)totalDurationToExecute)))/2f;
				System.out.print("\r+- Progress: " + df.format(progress) + "% -> [" + dfVar.format(currentProgressExecuted) + "] of [" + totalToExecute + "] executions, [" + currentTimeProgressExecuted + "] of [" + totalDurationToExecute + "] seconds. Threads Running: " + threadsRunningStr + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
			} else if (totalToExecute > 0) {
				progress = (float)currentProgressExecuted*100f/(float)totalToExecute;
				System.out.print("\r+- Progress: " + df.format(progress) + "% -> [" + dfVar.format(currentProgressExecuted) + "] of [" + totalToExecute + "] executions. Threads Running: " + threadsRunningStr  + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
			} else if (totalToExecute == 0 && totalDurationToExecute == 0) {
				System.out.print("\r+- Progress: -> [" + dfVar.format(currentProgressExecuted) + "] executions, [" + currentTimeProgressExecuted + "] seconds. Threads Running: " + threadsRunningStr + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
			} else {
				progress = (float)currentTimeProgressExecuted*100f/(float)totalDurationToExecute;
				System.out.print("\r+- Progress: " + df.format(progress) + "% -> [" + dfVar.format(currentTimeProgressExecuted) + "] of [" + totalDurationToExecute + "] seconds. Threads Running: " + threadsRunningStr  + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
			}
			
			threadsRunningCount = 0;
			anyThreadRunning = false;
			for (int i=0; i<=maxId; i++) {
				anyThreadRunning |= threadsRunning[i];
				if (threadsRunning[i]) {
					threadsRunningCount++;
				}
			}
			threadsRunningStr = df3Nbr.format(threadsRunningCount);
		}
		currentProgressExecuted = 0l;
		currentTimeProgressExecuted = 0l;
		for (int i=0; i<=maxId; i++) {
			currentProgressExecuted += currentProgress[i];
			currentTimeProgressExecuted += (currentTimeProgress[i]);
		}
		if (availableThreads > 0) { 
			currentTimeProgressExecuted = currentTimeProgressExecuted/availableThreads;
		} else {
			currentTimeProgressExecuted = 0;
		}
		if (totalToExecute > 0 && totalDurationToExecute > 0) {
			System.out.println("\r+- Progress: " + df.format(100) + "% -> [" + currentProgressExecuted + "] of [" + totalToExecute + "] executions, [" + currentTimeProgressExecuted + "] of [" + totalDurationToExecute + "] seconds. Threads Running: " + threadsRunningStr + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
		} else if (totalToExecute > 0) {
			System.out.println("\r+- Progress: " + df.format(100) + "% -> [" + currentProgressExecuted + "] of [" + totalToExecute + "] executions. Threads Running: " + threadsRunningStr  + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
		} else if (totalToExecute == 0 && totalDurationToExecute == 0) {
			System.out.println("\r+- Progress: -> [" + dfVar.format(currentProgressExecuted) + "] executions, [" + currentTimeProgressExecuted + "] seconds. Threads Running: " + threadsRunningStr + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
		} else {
			System.out.println("\r+- Progress: " + df.format(100) + "% -> [" + currentTimeProgressExecuted + "] of [" + totalDurationToExecute + "] seconds. Threads Running: " + threadsRunningStr  + ". Min/Max/Avg ExecTime: " + df5Nbr.format(minExecTimeVal) + "/" + df5Nbr.format(maxExecTimeVal) + "/" + df5Nbr.format(avgExecTimeVal) + " ms");
		}
	}

	public ExecutionProgressReporter(long totalToExecute, long totalDurationToExecute) {
		this.totalToExecute = totalToExecute;
		this.totalDurationToExecute = totalDurationToExecute;
		maxId = 0;
		df = new DecimalFormat("000.00");
		df3Nbr = new DecimalFormat("000");
		df5Nbr = new DecimalFormat("########0.0");
		
		String dfVarMask = "";
		for (int i=0; i< (new Long(totalToExecute).toString()).length(); i++) {
			dfVarMask += "0";
		}
		dfVar = new DecimalFormat(dfVarMask);
	}
	
	public void addCurrentProgress (int id, long progressDelta, long progressTimeDelta, float minExecTime, float maxExecTime, float avgExecTime) {
		currentProgress[id] = progressDelta;
		currentTimeProgress[id] = progressTimeDelta;
		this.minExecTime[id] = minExecTime;
		this.maxExecTime[id] = maxExecTime;
		this.avgExecTime[id] = avgExecTime;
		maxId = Math.max(maxId, id);
	}

	public void setThreadRunning (int id, boolean threadRunning) {
		threadsRunning[id] = threadRunning;
		maxId = Math.max(maxId, id);
	}

	public long getTotalToExecute() {
		return totalToExecute;
	}

	public long getCurrentProgress() {
		long progress = 0;
		for (int i=0; i<maxId; i++) {
			progress += currentProgress[i];
		}
		return (long)progress;
	}

	public long getCurrentTimeProgress() {
		long progress = 0;
		for (int i=0; i<maxId; i++) {
			progress += currentTimeProgress[i];
		}
		return (long)progress;
	}
}
