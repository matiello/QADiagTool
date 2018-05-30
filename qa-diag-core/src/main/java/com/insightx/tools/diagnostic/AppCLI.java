package com.insightx.tools.diagnostic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;
import com.insightx.tools.diagnostic.scenario.ScenarioDAOManager;

public class AppCLI {
	private volatile boolean keepOn = true;
	private boolean running = true;

	class RunWhenShuttingDown extends Thread {

		AppCLI app;

		RunWhenShuttingDown(AppCLI app) {
			this.app = app;
		}

		public void run() {
			keepOn = false;
			int MAX_SLEEP_TIMES = 100; // 10s
			int currSleepTime = 0;
			try {
				while (running && currSleepTime < MAX_SLEEP_TIMES) {
					Thread.sleep(100);
					currSleepTime++;
				}
				if (currSleepTime == MAX_SLEEP_TIMES) {
					System.out.println("");
					System.out.println("+- ATTENTION: Gracefull stop failed. Forcing HARD STOP.");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private List<LoadValidationFactoryInterface> plugins;

	public AppCLI() {
		running = true;
		plugins = new ArrayList<LoadValidationFactoryInterface>();
	}

	public void loadPlugins() {
		ServiceLoader<LoadValidationFactoryInterface> pluginList = ServiceLoader.load(LoadValidationFactoryInterface.class);
		for (LoadValidationFactoryInterface plugin : pluginList) {
			plugins.add(plugin);
		}
	}

	public void listInstalledPlugins() {
		System.out.println("List of instaled plugins:");
		for (LoadValidationFactoryInterface plugin : plugins) {
			System.out.println("+- " + plugin.getProcessName() + ": " + plugin.getDescription() + " [" + plugin.getClass().getSimpleName() + "]");
		}
	}

	public String fillGapString(int size, char gap) {
		String result = "";
		for (int i = 0; i < size; i++) {
			result += gap;
		}
		return result;
	}

	public String fillGapString(int size) {
		return fillGapString(size, ' ');
	}

	public String getUsageString() {
		String usage = "";
		usage += "Description: Tool used to measure and diagnose basic software and infrastructure."
				+ "\r\n"
				+ "Usage: QADiag <Selection Option> [Optional Parameters]" + "\r\n"
				+ "  Support Selection Options: " + "\r\n"
				+ "  ------- --------- -------" + "\r\n"
				+ "    -h                      : Displays help" + "\r\n"
				+ "    -listPlugins            : Displays the diagnostic plugins loaded" + "\r\n"
				+ "    -about                  : Displays application information and copyright" + "\r\n"
				+ "  Scenario & History Selection Options: " + "\r\n"
				+ "  -------- - ------- --------- -------" + "\r\n"
				+ "    -listScenarios          : List the stored scenarios" + "\r\n"
				+ "    -load <id>              : Loads a previously saved scenario" + "\r\n"
				+ "    -showHistory <id>       : Shows the scenario execution history" + "\r\n"
				+ "    -clearHistory <id>      : Clears the scenario execution history" + "\r\n"
				+ "    -expHistory <id> <file> : Export the execution history to excel file" + "\r\n"
				+ "  Plugin Diagnostic Selection Options: " + "\r\n"
				+ "  ------ ---------- --------- -------" + "\r\n";
		
		int maxLineSize = 0;
		String aux = "";
		for (LoadValidationFactoryInterface plugin : plugins) {
			for (Entry<String, RequiredParameter> param : plugin.getRequiredParameterList().entrySet()) {
				aux = "      <" + param.getValue().getId() + ">";
				maxLineSize = Math.max(maxLineSize, aux.length());
			}
			for (Entry<String, OptionalParameter> param : plugin.getOptionalParameterList().entrySet()) {
				if (param.getValue().getType() == LoadValidationFactoryInterface.BOOLEAN) {
					aux = "        " + param.getValue().getShellKey();
				} else {
					aux = "        " + param.getValue().getShellKey() + " <" + param.getValue().getId() + ">";
				}
				maxLineSize = Math.max(maxLineSize, aux.length());
			}
		}
		
		for (LoadValidationFactoryInterface plugin : plugins) {
			usage += "    " + plugin.getShellKey();
			
			for (Entry<String, RequiredParameter> param : plugin.getRequiredParameterList().entrySet()) {
				usage += " <" + param.getValue().getId() + ">";
			}
			
			usage += " [Optional Parameters] : " + plugin.getDescription() + "\r\n";
			
			for (Entry<String, RequiredParameter> param : plugin.getRequiredParameterList().entrySet()) {
				aux = "      <" + param.getValue().getId() + ">";
				usage += aux + fillGapString(maxLineSize-aux.length()) + " : " + param.getValue().getDescription() + "\r\n";;
			}
			
			usage += "      Optional Parameters:" + "\r\n";
			
			for (Entry<String, OptionalParameter> param : plugin.getOptionalParameterList().entrySet()) {
				if (param.getValue().getType() == LoadValidationFactoryInterface.BOOLEAN) {
					aux = "        " + param.getValue().getShellKey();
					usage += aux + fillGapString(maxLineSize-aux.length()) + " : " + param.getValue().getDescription() + " (Default " + param.getValue().getDefaultValue() + ")" + "\r\n";
				} else {
					aux = "        " + param.getValue().getShellKey() + " <" + param.getValue().getId() + ">";
					usage += aux + fillGapString(maxLineSize-aux.length()) + " : " + param.getValue().getDescription() + " (Default " + param.getValue().getDefaultValue() + ")" + "\r\n";
				}
			}
		}
		usage += "    Optional Parameters: "
				+ "\r\n"
				+ "      -strategy <type> <nbr>: The strategy to use <endless|count <interations>|duration <sec>) (Default count 1)" + "\r\n"
				+ "      -threads <nbr>        : The execution of threads (Default 1)" + "\r\n"
				+ "      -execInterval <nbr>   : The execution interval in miliseconds (Default 0)" + "\r\n"
				+ "      -hideParams           : Hides the software used paramters (Default false)" + "\r\n"
				+ "      -saveScenario <id>    : Save the scenario parameters for reuse (Default false)" + "\r\n"
				+ "      -showErrors           : Show any errors that may occour (Default false)" + "\r\n"
				+ "      -showThreadDetail     : Shows the thread parcial results (Default false)" + "\r\n"
				+ "      -showSumary           : Shows the result sumary (Default true)" + "\r\n";
		return usage;
	}

	public void exec(String [] args) {		
    	Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown(this));
    	int argIdx = 0;
    	long execCount = -1;
    	long execDur = -1;
    	boolean endless = false;
    	long threadCount = 1;
    	long execInterval = -1;
    	boolean showThreadDetail = false;
    	boolean showSumary = true;
    	boolean showErrors = false;
    	boolean hideParams = false;
    	boolean saveScenario = false;
    	boolean saveHistory = false;
    	String scenarioId = "";
    	DecimalFormat dfTime = new DecimalFormat("############0.00");

    	List<LoadValidationThread> threadList = new ArrayList<LoadValidationThread>();
    	ExecutionProgressReporter reporter = null;

		System.out.println("##############################################"); 
		System.out.println("##### Insight Labs - QA Diagnostics Tool #####");
		System.out.println("##############################################"); 

		loadPlugins();
		
		boolean paramsLoaded = false;
		
		if (args.length < 1) {
        	System.out.println(getUsageString());
        } else {
    		if (args[argIdx].equals("-h")) {
    			System.out.println(getUsageString());
    			shutdown(0);
    		} else if (args[argIdx].equals("-listPlugins")) {
    			listInstalledPlugins();
    			shutdown(0);
    		} else if (args[argIdx].equals("-clearHistory") && (args.length > ++argIdx)) {
    			scenarioId = args[argIdx++];
				System.out.println("Clear all history information for scenario: " + scenarioId);
				ScenarioDAOManager.getInstance().removeScenarioHistory(scenarioId);
    			shutdown(0);
    		} else if (args[argIdx].equals("-expHistory") && (args.length > ++argIdx+1)) {
    			scenarioId = args[argIdx++];
    			String fileId = args[argIdx++];

				System.out.println("Exporting history for: " + scenarioId + " to file:");
    			List<String[]> resultList = ScenarioDAOManager.getInstance().getScenarioHistory(scenarioId);
    			String [] headers = new String[] {"Execution Time","Best Avg Result","Worst Avg Result","Best Result","Worst Result","Success Rate"};

    			HSSFWorkbook wb = new HSSFWorkbook();
    		    FileOutputStream fileOut;
				try {
					fileOut = new FileOutputStream(fileId);
					HSSFSheet sheet = wb.createSheet(scenarioId);
					HSSFRow row = sheet.createRow(0);
					for (int i=0; i<headers.length; i++) {
						row.createCell(i).setCellValue(headers[i]);
					}
					int rowIdx=1;
	    			for (String [] resultItem : resultList ) {
	    				HSSFRow rowInfo = sheet.createRow(rowIdx++);
	    				for (int i=0; i<resultItem.length; i++) {
	    					rowInfo.createCell(i).setCellValue(resultItem[i]);
	    				}
	    			}
	    		    wb.write(fileOut);
	    		    fileOut.close();
				} catch (FileNotFoundException e) {
	    			System.out.println("+- File not found: " + e.getMessage());
	    			shutdown(1);
				} catch (IOException e) {
	    			System.out.println("+- Error writing to file: " + e.getMessage());
	    			shutdown(1);
				}
    			shutdown(0);
    		    
    		} else if (args[argIdx].equals("-listScenarios")) {
				System.out.println("List of stored scenarios:");
    			Map<String,String> scenarioList = ScenarioDAOManager.getInstance().getScenarios();
    			for (Entry<String, String> scenario : scenarioList.entrySet()) {
					System.out.println("+- " + scenario.getKey() + " [" + scenario.getValue() + "]");
    			}
    			shutdown(0);
    		} else if (args[argIdx].equals("-showHistory") && (args.length > ++argIdx)) {
				System.out.println("List of execution history for scenario: " + scenarioId);
    			scenarioId = args[argIdx++];
    			
    			List<String[]> resultList = ScenarioDAOManager.getInstance().getScenarioHistory(scenarioId);
    			String [] headers = new String[] {"Execution Time","Best Avg Result","Worst Avg Result","Best Result","Worst Result","Success Rate"};
    			String [] units = new String[] {""," ms"," ms"," ms"," ms","%"};
    			int [] maxResultSize = new int[] {headers[0].length(),headers[1].length(),headers[2].length(),headers[3].length(),headers[4].length(),headers[4].length()};
    			for (String [] resultItem : resultList ) {
    				for (int i=0; i<resultItem.length; i++) {
    					maxResultSize[i] = Math.max(maxResultSize[i], (resultItem[i]+units[i]).length());
    				}
    			}

				String boarderLinesString = "";
    			for (int i=0; i<maxResultSize.length; i++) {
    				boarderLinesString += fillGapString(maxResultSize[i]+2,'-') + "+";
    			}
    			System.out.println("+- +" + boarderLinesString);

    			String headerString = "";
    			for (int i=0; i<headers.length; i++) {
    				headerString += headers[i] + fillGapString(maxResultSize[i] - headers[i].length()) + " | ";
    			}
    			System.out.println("+- | " + headerString);
    			System.out.println("+- +" + boarderLinesString);
    			
    			for (String [] resultItem : resultList ) {
    				String resultString = "";
    				for (int i=0; i<resultItem.length; i++) {
    					resultString += " " + resultItem[i] + units[i] + fillGapString(maxResultSize[i] - resultItem[i].length() - units[i].length()) + " |";
    				}
    				System.out.println("+- |" + resultString);
    			}
    			System.out.println("+- +" + boarderLinesString);
				
    			shutdown(0);
    		} else if (args[argIdx].equals("-load")&& (args.length > ++argIdx)) {
    			saveHistory = true;
    			paramsLoaded = true;
    			scenarioId = args[argIdx++];
    			args = ScenarioDAOManager.getInstance().getScenarioParams(scenarioId);
    			argIdx = 0;
    		} else if (args[argIdx].equals("-about")) {
    			System.out.println("QA Diagnostic Tool is designed and developed to assist and enable environment");
    			System.out.println("validations and stress tests for bottleneck detection and throughput analysis.");
    			System.out.println("(c) Copyright Insight X Labs 2015.  All rights reserved.");
    			System.out.println("http://www.labs.insightx.com.br");
    			shutdown(0);
    		}
    		
			for (LoadValidationFactoryInterface plugin : plugins ) {
    			boolean pluginFound = false;
				if ( (argIdx < args.length) && (args[argIdx].equals(plugin.getShellKey()))) {
					argIdx++;
					pluginFound = true;

					if ((args.length - argIdx) >= plugin.getRequiredParameterList().entrySet().size()) {
    					for (Entry<String, RequiredParameter> param : plugin.getRequiredParameterList().entrySet() ) {
    						switch (param.getValue().getType()) {
	    						case LoadValidationFactoryInterface.BOOLEAN: {
		    						param.getValue().setValue(Boolean.parseBoolean(args[argIdx++]));
	            	        		break;
	    						}
	    						case LoadValidationFactoryInterface.INTEGER: {
		    						param.getValue().setValue(Integer.parseInt(args[argIdx++]));
	            	        		break;
	    						}
	    						case LoadValidationFactoryInterface.FLOAT: {
		    						param.getValue().setValue(Float.parseFloat(args[argIdx++]));
	            	        		break;
	    						}
	    						case LoadValidationFactoryInterface.STRING: {
		    						param.getValue().setValue(args[argIdx++]);
	            	        		break;
								}
	    						default: {
	    							
	    						}
    						}
	    				}
					}
				}

    			if (pluginFound) {
    				while (argIdx < args.length) {
    					boolean pluginParamFound = false;
	    				for (Entry<String, OptionalParameter> param : plugin.getOptionalParameterList().entrySet() ) {
	    					if ((argIdx < args.length) && (args[argIdx].equals((String)param.getValue().getShellKey()))) {
	    						pluginParamFound = true;
	    						switch (param.getValue().getType()) {
		    						case LoadValidationFactoryInterface.BOOLEAN: {
		    							argIdx++;
		    							param.getValue().setValue(true);
		    							break;
									}
		    						case LoadValidationFactoryInterface.INTEGER: {
		    							argIdx++;
		    							try {
		            	        			param.getValue().setValue(Integer.parseInt(args[argIdx++]));
		            	        		} catch (NumberFormatException nfe) {System.out.println(getUsageString()); shutdown(1);} 
		            	        		break;
		    						}
		    						case LoadValidationFactoryInterface.FLOAT: {
		    							argIdx++;
		    							try {
		            	        			param.getValue().setValue(Float.parseFloat(args[argIdx++]));
		            	        		} catch (NumberFormatException nfe) {System.out.println(getUsageString()); shutdown(1);} 
		            	        		break;
		    						}
		    						case LoadValidationFactoryInterface.STRING: {
		    							argIdx++;
		        	        			param.getValue().setValue(args[argIdx++]);
		            	        		break;
									}
		    						default: {
		    							argIdx++;
		    							System.out.println(getUsageString()); shutdown(1);
		    							break;
		    						}
	    						}
	    					}
	    				}
	    				if ((!pluginParamFound) && (argIdx < args.length)) {
	    					if (args[argIdx].equals("-strategy") && (args.length > ++argIdx)) {
	        	        		try {
	        	        			if (args[argIdx].equals("count") && (args.length > ++argIdx)) {
	        	        				execCount = Long.parseLong(args[argIdx++]);
	        	        			} else if (args[argIdx].equals("duration") && (args.length > ++argIdx)) {
	        	        				execDur = Long.parseLong(args[argIdx++]);
	        	        			} else if (args[argIdx].equals("endless")) {
	        	        				endless = true; argIdx++;
	        	        			} else { 
	        	        				System.out.println(getUsageString());
	        	        				shutdown(1);
	        	        			}
	        	        		} catch (NumberFormatException nfe) {System.out.println(getUsageString()); shutdown(1);} 
	                		} else if (args[argIdx].equals("-threads") && (args.length >= ++argIdx)) {
	        	        		try {
	        	        			threadCount = Long.parseLong(args[argIdx++]);;
	        	        		} catch (NumberFormatException nfe) {System.out.println(getUsageString()); shutdown(1);} 
	                		} else if (args[argIdx].equals("-execInterval") && (args.length >= ++argIdx)) {
	        	        		try {
	        	        			execInterval = Long.parseLong(args[argIdx++]);;
	        	        		} catch (NumberFormatException nfe) {System.out.println(getUsageString()); shutdown(1);} 
	                		} else if (args[argIdx].equals("-showSumary")) { 
	                			showSumary = true; argIdx++;
	                		} else if (args[argIdx].equals("-showThreadDetail")) { 
	                			showThreadDetail = true; argIdx++;
	                		} else if (args[argIdx].equals("-showErrors")) { 
	                			showErrors = true; argIdx++;
	                		} else if (args[argIdx].equals("-hideParams")) { 
	                			hideParams = true; argIdx++;
	                		} else if (args[argIdx].equals("-saveScenario")  && (args.length >= ++argIdx)) {
	                			saveScenario = true;
	                			saveHistory = true;
	                			scenarioId = args[argIdx++];
	                		}
	    				}
    				}
    				
    				if (endless) {
						execCount = 0;
						execDur = 0;
					} else if ( (execCount == -1) && (execDur == -1) ) {
						execCount = 1;
						execDur = 0;
					}

					if (!hideParams) {
						System.out.println("----------");
			    		System.out.println("Parameters");
						System.out.println("----------");
			    		System.out.println("+- Diagnostic Control");
			    		System.out.println("   +- Threads: " + threadCount);
			    		System.out.println("   +- Endless exec: " + endless);
			    		System.out.println("   +- Exec Count: " + execCount);
			    		System.out.println("   +- Duration: " + execDur);
			    		System.out.println("   +- Exec Interval: " + execInterval);
			    		System.out.println("+- " + plugin.getDescription());
    					for (Entry<String, RequiredParameter> param : plugin.getRequiredParameterList().entrySet() ) {
    			    		System.out.println("   +- " + param.getValue().getId() + ": " + param.getValue().getValue());
	    				}
	    				for (Entry<String, OptionalParameter> param : plugin.getOptionalParameterList().entrySet() ) {
    			    		if (param.getValue().getValue() != null) {
    	    					System.out.println("   +- " + param.getValue().getId() + ": " + param.getValue().getValue());
    			    		} else {
    	    					System.out.println("   +- " + param.getValue().getId() + ": " + param.getValue().getDefaultValue() + " [Using Default Value]");
    			    		}
	    				}
			    		System.out.println("+- Results");
		    			System.out.println("   +- showSumary: " + showSumary);
		    			System.out.println("   +- showThreadDetail: " + showThreadDetail);
		    			System.out.println("   +- showErrors: " + showErrors);
					}
			
					reporter = new ExecutionProgressReporter(execCount*threadCount, execDur);
					for (int i=0; i<threadCount; i++) {
						LoadValidationInstanceInterface lvii = plugin.createInstance();
						threadList.add(new LoadValidationThread(endless, execCount, execDur, execInterval, lvii, reporter));
					}
    			}
			}

			if (saveScenario && !paramsLoaded) {
				ScenarioDAOManager.getInstance().saveScenario(scenarioId, args);
			}					

			System.out.println("--------- ------");
			System.out.println("Execution Report");
			System.out.println("--------- ------");
			for (int i=0; i<threadList.size(); i++) {
				threadList.get(i).start();
			}
			reporter.start();
	
			if (showSumary) {
    			boolean execFinished = false;
    			while (!execFinished && keepOn) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				execFinished = true;
        			for (int i=0; i<threadList.size(); i++) {
        				if (threadList.get(i).isAlive() || reporter.isAlive()) {
        					execFinished = false;
        				}
        			}
    			}
    			if (!keepOn) {
        			for (int i=0; i<threadList.size(); i++) {
						threadList.get(i).haltExecution();
        			}
        			while (reporter.isAlive()) {
        				try {
							Thread.sleep(100);
        				} catch (InterruptedException ie) {}
        			}
    				System.out.println("+- Execution finished by user.");
    			}
    			float bestAvgExecTime = 1000000000000f;
    			float worstAvgExecTime = 0f;
    			float bestExecTime = 1000000000000f;
    			float worstExecTime  = 0f;
    			float successfullExecs = 0f;
	    			
    			for (int i=0; i<threadList.size(); i++) {
    				if (threadList.get(i).getAvgGlobalExecTime() != -1f) {
    					bestAvgExecTime = Math.min(bestAvgExecTime, threadList.get(i).getAvgGlobalExecTime());
    				}
    				if (threadList.get(i).getAvgGlobalExecTime() != -1f) {
    					worstAvgExecTime = Math.max(worstAvgExecTime, threadList.get(i).getAvgGlobalExecTime());
    				}
    				if (threadList.get(i).getMinLocalExecTime() != -1f) {
    					bestExecTime = Math.min(bestExecTime, threadList.get(i).getMinLocalExecTime());
    				}
    				if (threadList.get(i).getMaxLocalExecTime() != -1f) {
    					worstExecTime = Math.max(worstExecTime, threadList.get(i).getMaxLocalExecTime());
					}
					successfullExecs += threadList.get(i).getSuccessExecIndicator();
    			}
    			successfullExecs = successfullExecs/threadList.size();
    			if (showThreadDetail){
    				System.out.println("-------- ------");
    				System.out.println("Detailed Result");
    				System.out.println("-------- ------");
	    			for (int i=0; i<threadList.size(); i++) {
		    			System.out.println(threadList.get(i).getResult());
	    			}
    			}
    			if (showErrors) {
    				System.out.println("------");
    				System.out.println("Errors");
    				System.out.println("------");
    				for (int i=0; i<threadList.size(); i++) {
    					List<Exception> e = threadList.get(i).getExceptions();
    					for (int y=0; y<e.size(); y++) {
        	    			System.out.println("+- Error: " + e.get(y).getMessage());
    					}
        			}
    			}
    			if (showSumary){
    				System.out.println("------");
	    			System.out.println("Sumary");
	    			System.out.println("------");
	    			System.out.println("+- Best Avg Time  : " + dfTime.format(bestAvgExecTime) + " mili seconds");
	    			System.out.println("+- Worst Avg Time : " + dfTime.format(worstAvgExecTime) + " mili seconds");
	    			System.out.println("+- Best Time      : " + dfTime.format(bestExecTime) + " mili seconds");
	    			System.out.println("+- Worst Time     : " + dfTime.format(worstExecTime) + " mili seconds");
	    			System.out.println("+- Successfull executions: " + successfullExecs + "%");
    			}

    			if (saveHistory) {
    				ScenarioDAOManager.getInstance().addScenarioHistory(scenarioId, dfTime.format(bestAvgExecTime), dfTime.format(worstAvgExecTime), dfTime.format(bestExecTime), dfTime.format(worstExecTime), String.valueOf(successfullExecs));
	    			System.out.println("+- History saved");
    			}					
			}
			shutdown(0);
		}
		shutdown(0);
	}

	public void shutdown(int status) {
		running = false;
		System.exit(status);
	}

	public static void main(String[] args) throws Exception {
		AppCLI app = new AppCLI();
		app.exec(args);
	}
}
