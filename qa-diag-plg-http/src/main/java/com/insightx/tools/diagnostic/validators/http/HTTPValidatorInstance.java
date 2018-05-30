package com.insightx.tools.diagnostic.validators.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;

public class HTTPValidatorInstance extends LoadValidationInstanceInterface{

	private String processName;
	private String method;
	private String urlStr;
	private String sucessResponse;
	private boolean showResponse;
	private long connTimeout;
	private long soTimeout;
	private String contentType;
	private File content;
	
	private HttpClient httpclient;
	private HttpGet httpget;
	private HttpPost httppost;

	private String httpContentResult = "";
	private HttpResponse response = null;
	private HttpEntity resEntity = null;
	private int resultStatus = 0;
		
	public HTTPValidatorInstance(String processName, String urlStr, String method, String contentType, String requestParams, String fileContent, String sucessResponse, long connTimeout, long soTimeout, boolean showRequest, boolean showResponse) {
 		this.processName = processName;
		this.method = method;
		this.urlStr = urlStr;
		this.sucessResponse = sucessResponse;
		this.showResponse = showResponse;
		this.connTimeout = connTimeout;
		this.soTimeout = soTimeout;
		this.contentType = contentType;
		this.content = new File(fileContent);

		if (showRequest) {
			System.out.println("+- [" + this.getProcessName() + "] URL: " + urlStr);
		}
	}
	
	public String getProcessName() {
		return processName;
	}

	private static final String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	public void setup() throws Exception {
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, (int) connTimeout);
		httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, (int) soTimeout);
		if (method.equals("GET")) {
			httpget = new HttpGet(urlStr);
		} else if (method.equals("POST")) {
			httppost = new HttpPost(urlStr);
		}
	}

	public void tearDown() throws Exception {
		httpclient.getConnectionManager().shutdown();
	}

	public int validate() throws Exception{
		if (method.equals("GET")) {
			response = httpclient.execute(httpget);
		} else if (method.equals("POST")) {
			if (content != null) {
				FileEntity fe = new FileEntity(content, contentType);
				fe.setContentType(contentType);
				httppost.setEntity(fe);
			}
			response = httpclient.execute(httppost);
		}
		
		resEntity = response.getEntity();
		
		resultStatus = response.getStatusLine().getStatusCode();
		
		if (resultStatus != 200) {
			return FAILED;
		} else {
			return SUCCESS;
		}
	}

	public int preValidate() throws Exception {
		httpContentResult = "";
		response = null;
		resEntity = null;
		resultStatus = 0;
		
		return SUCCESS;
	}

	public int postValidate() throws Exception {
		if (resEntity != null) {
			httpContentResult = convertStreamToString(resEntity.getContent());
		} else {
			httpContentResult = "";
		}
		
		EntityUtils.consume(resEntity);

		if (showResponse) {
			System.out.println("+- [" + this.getProcessName()+ "] HTTP Response: : " + httpContentResult);
		}

		if (!httpContentResult.contains(sucessResponse)) {
			return FAILED;
		} else {
			return SUCCESS;
		}
	}
}
