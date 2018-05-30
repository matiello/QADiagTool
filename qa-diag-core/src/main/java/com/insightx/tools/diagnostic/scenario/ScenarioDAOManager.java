package com.insightx.tools.diagnostic.scenario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScenarioDAOManager {

	private static ScenarioDAOManager instance = null;

	private Connection connection = null;
	private final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private final String DB_NAME = "QADiagnosticsStorage";
	private final String STARTUP_URL = "jdbc:derby:../data/" + DB_NAME + ";create=true";
	private final String SHUTDOWN_URL = "jdbc:derby:;shutdown=true";

	private boolean databaseOn;
	
	public static ScenarioDAOManager getInstance() {
		if (instance == null) {
			instance = new ScenarioDAOManager();
		}
		return instance;
	}

	private ScenarioDAOManager() {
		databaseOn = false;
	}

	public boolean startup() {
		try {
			Class.forName(DRIVER);
		} catch (java.lang.ClassNotFoundException e) {
			System.out.println("Unable to find Database driver: " + DRIVER);
			System.out.println(e.getMessage());
			return false;
		}

		try {
			connection = DriverManager.getConnection(STARTUP_URL);
			databaseOn = true;
		} catch (Throwable e) {
			System.out.println("Database did not shut down normally");
			System.out.println(e.getMessage());
			return false;
		}

		return true;
	}

	public boolean shutdown() {
		try {
			DriverManager.getConnection(SHUTDOWN_URL);
		} catch (SQLException se) {
			if (se.getSQLState().equals("XJ015")) {
				databaseOn = false;
			} else {
				System.out.println("Unexpected exception during  database shutdown.");
				return false;
			}
		}
		return true;
	}

	public Boolean checkIntegrity() {
		if (!databaseOn){
			if (!startup()) {
				return false;
			}
		}
		Statement checkStatement = null;
		try {
			checkStatement = connection.createStatement();
			checkStatement.execute("SELECT SCENARIO_ID FROM QA_DIAG_SCENARIOS WHERE SCENARIO_ID='Default_NonExisting_Scenario_123!@#'");
		} catch (SQLException sqle) {
			String theError = (sqle).getSQLState();
			if (theError.equals("42X05")) {
				Statement createStatement = null;
				try {
					createStatement = connection.createStatement();
					final String SQL_CREATE_TABLE_SCENARIOS = 
						"CREATE TABLE QA_DIAG_SCENARIOS ("
			          + "    SCENARIO_ID VARCHAR(200) NOT NULL CONSTRAINT SCENARIO_ID PRIMARY KEY, " 
			          + " 	 ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			          + "    SCENARIO_DATA VARCHAR(400) NOT NULL)" ;
					createStatement.execute(SQL_CREATE_TABLE_SCENARIOS);

					final String SQL_CREATE_TABLE_SCENARIOS_HIST = 
						"CREATE TABLE QA_DIAG_SCENARIOS_HISTORY ("
			          + "    SCENARIO_ID VARCHAR(200) NOT NULL, " 
			          + " 	 EXEC_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			          + "    BEST_AVERAGE_TIME VARCHAR(30) NOT NULL,"
			          + "    WORST_AVERAGE_TIME VARCHAR(30) NOT NULL,"
			          + "    BEST_TIME VARCHAR(30) NOT NULL,"
			          + "    WORST_TIME VARCHAR(30) NOT NULL,"
			          + "    SUCCESS_RATE VARCHAR(30) NOT NULL)";
					createStatement.execute(SQL_CREATE_TABLE_SCENARIOS_HIST);
				} catch (SQLException e) {
					return false;
				} finally {
					if (createStatement != null) {
						try {
							createStatement.close();
						} catch (SQLException e) {
						}
					}
				}
			} else if (theError.equals("42X14") || theError.equals("42821")) {
				// Incorrect table definition. Drop table and recreate.
				return false;
			} else {
				return false;
			}
		} finally {
			if (checkStatement != null) {
				try {
					checkStatement.close();
				} catch (SQLException e) {
				}
			}
		}
		return true;
	}

	public Map<String, String> getScenarios() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		if (checkIntegrity()){
			PreparedStatement queryStatement = null;
			try {
				final String QUERY_SCENARIO = 
					"SELECT SCENARIO_ID, SCENARIO_DATA"
		          + "  FROM QA_DIAG_SCENARIOS"; 
				queryStatement = connection.prepareStatement(QUERY_SCENARIO);
				ResultSet rs = queryStatement.executeQuery();
				while (rs.next()) {
					result.put(rs.getString(1), rs.getString(2).replaceAll("##SEP##", " "));
				}
			} catch (SQLException e) {
			} finally {
				if (queryStatement != null) {
					try {
						queryStatement.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return result;
	}

	public String [] getScenarioParams(String scenarioId) {
		String [] result = new String[]{};
		if (checkIntegrity()){
			PreparedStatement queryStatement = null;
			try {
				final String QUERY_SCENARIO = 
					"SELECT SCENARIO_DATA"
		          + "  FROM QA_DIAG_SCENARIOS " 
		          + " WHERE SCENARIO_ID = ?";
				queryStatement = connection.prepareStatement(QUERY_SCENARIO);
				queryStatement.setString(1, scenarioId);
				ResultSet rs = queryStatement.executeQuery();
				while (rs.next()) {
					result = stringToArray(rs.getString(1), "##SEP##");	
				}
			} catch (SQLException e) {
			} finally {
				if (queryStatement != null) {
					try {
						queryStatement.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return result;
	}

	public boolean saveScenario(String scenarioId, String [] params) {
		Boolean result = false;
		if (checkIntegrity()){
			PreparedStatement queryStatement = null;
			try {
				final String INSERT_SCENARIO = 
					"INSERT INTO QA_DIAG_SCENARIOS (SCENARIO_ID, SCENARIO_DATA)"
		          + "VALUES (?,?)";
				queryStatement = connection.prepareStatement(INSERT_SCENARIO);
				queryStatement.setString(1, scenarioId);
				queryStatement.setString(2, arrayToString(params,"##SEP##"));
				int resultCount = queryStatement.executeUpdate();
				if (resultCount == 1) {
					result = true;
					connection.commit();
				} else {
					connection.rollback();
				}
			} catch (SQLException e) {
			} finally {
				if (queryStatement != null) {
					try {
						queryStatement.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return result;
	}

	public boolean addScenarioHistory (String scenarioId, String BestAvgTime, String WrostAvgTime, String BestTime, String WrostTime, String SuccessRate) {
		Boolean result = false;
		if (checkIntegrity()){
			PreparedStatement queryStatement = null;
			try {
				final String INSERT_SCENARIO = 
					"INSERT INTO QA_DIAG_SCENARIOS_HISTORY (SCENARIO_ID, BEST_AVERAGE_TIME, WORST_AVERAGE_TIME, BEST_TIME, WORST_TIME, SUCCESS_RATE)"
		          + "VALUES (?,?,?,?,?,?)";
				queryStatement = connection.prepareStatement(INSERT_SCENARIO);
				queryStatement.setString(1, scenarioId);
				queryStatement.setString(2, BestAvgTime);
				queryStatement.setString(3, WrostAvgTime);
				queryStatement.setString(4, BestTime);
				queryStatement.setString(5, WrostTime);
				queryStatement.setString(6, SuccessRate);
				int resultCount = queryStatement.executeUpdate();
				if (resultCount == 1) {
					result = true;
					connection.commit();
				} else {
					connection.rollback();
				}
			} catch (SQLException e) {
			} finally {
				if (queryStatement != null) {
					try {
						queryStatement.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return result;
	}

	public List<String []> getScenarioHistory(String scenarioId) {
		List<String []> result = new ArrayList<String[]>();
		if (checkIntegrity()){
			PreparedStatement queryStatement = null;
			try {
				final String QUERY_SCENARIO_HISTORY = 
					"SELECT EXEC_DATE, BEST_AVERAGE_TIME, WORST_AVERAGE_TIME, BEST_TIME, WORST_TIME, SUCCESS_RATE"
		          + "  FROM QA_DIAG_SCENARIOS_HISTORY " 
		          + " WHERE SCENARIO_ID = ?";
				queryStatement = connection.prepareStatement(QUERY_SCENARIO_HISTORY);
				queryStatement.setString(1, scenarioId);
				ResultSet rs = queryStatement.executeQuery();
				while (rs.next()) {
					String [] aux = new String[]{"-1","-1","-1","-1","-1","-1"};
					aux[0] = String.valueOf(rs.getTimestamp(1));
					aux[1] = rs.getString(2);	
					aux[2] = rs.getString(3);	
					aux[3] = rs.getString(4);	
					aux[4] = rs.getString(5);	
					aux[5] = rs.getString(6);
					result.add(aux);
				}
			} catch (SQLException e) {
			} finally {
				if (queryStatement != null) {
					try {
						queryStatement.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return result;
	}

	public boolean removeScenarioHistory(String scenarioId) {
		boolean result = false;
		if (checkIntegrity()){
			PreparedStatement queryStatement = null;
			try {
				final String REMOVE_SCENARIO_HISTORY = 
					"DELETE FROM QA_DIAG_SCENARIOS_HISTORY"
		          + " WHERE SCENARIO_ID = ?";
				queryStatement = connection.prepareStatement(REMOVE_SCENARIO_HISTORY);
				queryStatement.setString(1, scenarioId);
				int resultCount = queryStatement.executeUpdate();
				if (resultCount >= 0) {
					result = true;
				}
			} catch (SQLException e) {
			} finally {
				if (queryStatement != null) {
					try {
						queryStatement.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return result;
	}

	private  static String [] stringToArray( String s, String sep ) {
		return s.split(sep);
	}
	
	private static String arrayToString (String [] s, String sep) {
		String result = "";
		for (int i=0; i<s.length-1; i++) {
			result += s[i] + sep;
		}
		result += s[s.length-1];
		return result;
	}
}
