package com.zizibujuan.server.test.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.zizibujuan.drip.server.util.json.JsonUtil;

/**
 * xhr
 * 
 * @author jzw
 * @since 0.0.1
 */
public class XMLHttpRequest {
	
	private static final String XHR_HEADER_KEY = "X-Requested-With";
	private static final String XHR_HEADER_VALUE = "XMLHttpRequest";
	
	private String serverLocation;
	private int responseCode;
	private String returnContent;
	
	public XMLHttpRequest(String serverLocation){
		this.serverLocation = serverLocation;
	}

	public void post(String urlString){
		post(urlString, null);
	}
	
	public void post(String urlString, Map<String, Object> formData){
		
		HttpURLConnection connection = null;
		
		try {
			URL url = new URL(serverLocation + urlString);
			
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty(XHR_HEADER_KEY, XHR_HEADER_VALUE);

			connection.setDoOutput(true);

			if(formData != null && !formData.isEmpty()){
				OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				out.write(prepareParams(formData).getBytes());
				out.flush();
				out.close();
			}

			responseCode = connection.getResponseCode();
			if(400/*client error*/ <= responseCode && responseCode <= 600/*500 server error*/){
				InputStream in = connection.getErrorStream();
			    returnContent = IOUtils.toString(in);
			}else{
				InputStream in = new BufferedInputStream(connection.getInputStream());
			    returnContent = IOUtils.toString(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(connection != null){
				connection.disconnect();
			}
		}
	}
	
	private String prepareParams(Map<String, Object> params){
        return JsonUtil.toJson(params);
    }

	public int getResponseCode() {
		return responseCode;
	}

	public Map<String, Object> getContentAsJsonObject() {
		return JsonUtil.fromJsonObject(returnContent);
	}
}
