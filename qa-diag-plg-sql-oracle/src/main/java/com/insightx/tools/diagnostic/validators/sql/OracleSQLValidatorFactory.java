package com.insightx.tools.diagnostic.validators.sql;

import com.insightx.tools.diagnostic.LoadValidationFactoryInterface;
import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;
import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;

public class OracleSQLValidatorFactory extends LoadValidationFactoryInterface {

	public OracleSQLValidatorFactory() {
		super("OracleSQLLoadDiagnostic","-db","Execute Oracle SQL load diagnostic.");
	}

	@Override
	protected void loadExecutionParameters() {
		addRequiredParamter ("url",      new RequiredParameter("url","The jdbc url connection.", STRING));
		addRequiredParamter ("username", new RequiredParameter("username","The jdbc url username.", STRING));
		addRequiredParamter ("password", new RequiredParameter("password","The jdbc url password.", STRING));
		addOptionalParameter("connTimeout", new OptionalParameter("connTimeout", "The connection establishment timeout in seconds.", "-connTimeout", INTEGER, 10));
		addOptionalParameter("queryTimeout", new OptionalParameter("queryTimeout", "The query execution timeout in seconds.", "-queryTimeout", INTEGER, 1));
		addOptionalParameter("bytes", new OptionalParameter("bytes", "The number of bytes per sql interaction.", "-bytes", INTEGER, 1));
		addOptionalParameter("queryKey", new OptionalParameter("queryKey", "The query identifier Id.", "-queryKey", STRING, "1"));
		addOptionalParameter("querySQL", new OptionalParameter("querySQL", "The query SQL to be used.", "-querySQL", STRING, "SELECT /* ${processName}.${queryKey} */ $Repeat(${queryKey},${bytes}) FROM DUAL"));
	}

	@Override
	protected LoadValidationInstanceInterface createInstance() {
		return new OracleSQLValidatorInstance(
				this.getProcessName(),
		 		(String) getParameter("url"),
		 		(String) getParameter("username"),
		 		(String) getParameter("password"),
		 		(Integer) getParameter("connTimeout"),
		 		(Integer) getParameter("queryTimeout"),
		 		(Integer) getParameter("bytes"),
		 		(String) getParameter("queryKey"),
		 		(String) getParameter("querySQL")
		);
	}
}
