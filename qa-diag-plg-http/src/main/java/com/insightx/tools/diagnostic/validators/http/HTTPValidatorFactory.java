package com.insightx.tools.diagnostic.validators.http;

import com.insightx.tools.diagnostic.LoadValidationFactoryInterface;
import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;
import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;

public class HTTPValidatorFactory extends LoadValidationFactoryInterface{

	public HTTPValidatorFactory() {
		super("HTTPDiagnostic", "-http", "Execute HTTP load diagnostic");
	}

	@Override
	protected LoadValidationInstanceInterface createInstance() {
		return new HTTPValidatorInstance (
				this.getProcessName(),
		 		(String)getParameter("url"),
		 		(String)getParameter("method"),
		 		(String)getParameter("contentType"),
		 		(String)getParameter("params"),
		 		(String)getParameter("bodyParams"),
		 		(String)getParameter("testResult"),
		 		(Integer)getParameter("connTimeout"),
		 		(Integer)getParameter("soTimeout"),
		 		(Boolean)getParameter("showRequest"),
		 		(Boolean)getParameter("showResponse")
		 	);
	}

	@Override
	protected void loadExecutionParameters() {
		addRequiredParamter ("url", new RequiredParameter("url","The http url.", STRING));
		addRequiredParamter ("method", new RequiredParameter("method","The http connection method (GET|POST).", STRING));
		addOptionalParameter("testResult", new OptionalParameter("testResult", "Test HTTP Body result.", "-testResult", STRING, ""));		
		addOptionalParameter("connTimeout", new OptionalParameter("connTimeout", "The connection timeout in seconds.", "-connTimeout", INTEGER, 5000));		
		addOptionalParameter("soTimeout", new OptionalParameter("soTimeout", "The SO read timeout in seconds.", "-soTimeout", INTEGER, 5000));		
		addOptionalParameter("params", new OptionalParameter("params", "Add HTTP request parameters (On GET).", "-params", STRING, ""));		
		addOptionalParameter("bodyParams", new OptionalParameter("bodyParams", "The file that contains the HTTP Body Request.", "-bodyParams", STRING, ""));		
		addOptionalParameter("contentType", new OptionalParameter("contentType", "The request content type.", "-contentType", STRING, "text/xml;charset=iso-8859-1"));		
		addOptionalParameter("showRequest", new OptionalParameter("showRequest", "Shows the HTTP Request.", "-showRequest", BOOLEAN, false));		
		addOptionalParameter("showResponse", new OptionalParameter("showResponse", "Shows the HTTP Response.", "-showResponse", BOOLEAN, false));		
	}

}
