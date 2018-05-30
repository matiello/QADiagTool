package com.insightx.tools.diagnostic.validators.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;

public class OracleSQLValidatorInstance extends LoadValidationInstanceInterface{

	private String processName;
	private String url;
	private String username;
	private String password;
	private int connectionTimeout;
	private int queryTimeout;
	
	private String SQLGeneratedString;
	private int SQLResultVariables;
	private int bytes;
	private String queryKey;
	private String querySQL;

	private Connection conn;
	private PreparedStatement stmt;
	private String dummy;
	private ResultSet rset;

	
	public OracleSQLValidatorInstance(String processName, String url, String username, String password, int connectionTimeout, int queryTimeout, int bytes, String queryKey, String querySQL) {
 		this.processName = processName;
		this.url = url;
 		this.username = username;
 		this.password = password;
 		this.connectionTimeout = connectionTimeout;
 		this.queryTimeout = queryTimeout;
 		this.bytes = bytes;
 		this.queryKey = queryKey;
 		this.querySQL = querySQL;
	}
	
	@Override
	public void setup() throws Exception {
		SQLGeneratedString = querySQL;
		SQLGeneratedString = SQLGeneratedString.replaceAll("\\$\\{processName\\}", processName);
		SQLGeneratedString = SQLGeneratedString.replaceAll("\\$\\{queryKey\\}", queryKey);
		SQLGeneratedString = SQLGeneratedString.replaceAll("\\$\\{bytes\\}", String.valueOf(bytes));

		int repeatCount = 1;
		String repeatQueryKey = queryKey;
		String repeatString = "'";
		Pattern p = Pattern.compile("\\$Repeat\\((.+)\\,(.+)\\)");
		Matcher m = p.matcher(SQLGeneratedString);
		if (m.find()) {
			repeatQueryKey = m.group(1);
			repeatCount = Integer.parseInt(m.group(2));

			for (int i=1; i<=repeatCount/2; i++){
				if (i % 4000 == 0) {
					repeatString += "','";
				}
				repeatString += repeatQueryKey;
			}
			repeatString += "'";
			SQLGeneratedString = SQLGeneratedString.replaceFirst("\\$Repeat\\((.+)\\,(.+)\\)",repeatString);

			SQLResultVariables = (int) (repeatCount/4001) + 1;
		}

		Class.forName ("oracle.jdbc.OracleDriver");
		DriverManager.setLoginTimeout(connectionTimeout);
		conn = DriverManager.getConnection (url, username, password);
		stmt = conn.prepareStatement(SQLGeneratedString);
		stmt.setQueryTimeout(queryTimeout);
	}

	@Override
	public void tearDown() throws Exception {
		stmt.close();
		conn.close();
	}

	@Override
	public int validate() throws Exception {
		try {
			rset = stmt.executeQuery();
			while (rset.next()) {
				for (int i=0; i<SQLResultVariables; i++) {
					dummy = rset.getString(i+1);
				}
			}
		} finally {
			rset.close();
		}

		if ( (dummy != null) && (dummy.length() > 0)) { 
			return SUCCESS;
		} else {
			return FAILED;
		}
	}
	
	@Override
	public int preValidate() throws Exception {
		dummy = "";
		rset = null;

		return SUCCESS;
	}

	@Override
	public int postValidate() throws Exception {
		return SUCCESS;
	}

}
