package com.zizibujuan.server.test.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
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

			// FIXME: 下面这种写法，如果formData为null，则不会进入对应的servlet
			if(formData != null && !formData.isEmpty()){
				OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				out.write(preparePostParams(formData).getBytes());
				out.flush();
				out.close();
			}

			responseCode = connection.getResponseCode();
			if(isErrorResponse()){
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

	public void get(String urlString){
		this.get(urlString, null);
	}
	
	public void get(String urlString, Map<String, Object> params){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(prepareQueryParams(serverLocation + urlString, params));
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty(XHR_HEADER_KEY, XHR_HEADER_VALUE);

            responseCode = connection.getResponseCode();
            if(isErrorResponse()){
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                returnContent = IOUtils.toString(inputStream);

            }else{
            	InputStream in = new BufferedInputStream(connection.getInputStream());
			    returnContent = IOUtils.toString(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }
	}
	
	public void put(String urlString){
		put(urlString, null);
	}
	
	public void put(String urlString, Map<String, Object> formData){
		
		HttpURLConnection connection = null;
		
		try {
			URL url = new URL(serverLocation + urlString);
			
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty(XHR_HEADER_KEY, XHR_HEADER_VALUE);

			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			
			

			if(formData != null && !formData.isEmpty()){
				OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				out.write(preparePostParams(formData).getBytes());
				out.flush();
				out.close();
			}

			responseCode = connection.getResponseCode();
			if(isErrorResponse()){
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
	
	private boolean isErrorResponse() {
		return 400/*client error*/ <= responseCode && responseCode <= 600/*500 server error*/;
	}
	
	private String preparePostParams(Map<String, Object> params){
        return JsonUtil.toJson(params);
    }
	
	private String prepareQueryParams(String urlString, Map<String, Object> params){
        StringBuilder sbUrl = new StringBuilder(urlString);
        if(params != null){
            if(params != null && !params.isEmpty()){
                sbUrl.append("?");
                int count = 0;
                for(Map.Entry<String, Object> entry : params.entrySet()){
                    if(count != 0){
                        sbUrl.append("&");
                    }
                    sbUrl.append(entry.getKey()).append("=").append(entry.getValue());
                    count++;
                }
            }
        }
        return sbUrl.toString();
    }

	public int getResponseCode() {
		return responseCode;
	}

	public Map<String, Object> getContentAsJsonObject() {
		return JsonUtil.fromJsonObject(returnContent);
	}
	
	public <T> T getContentAsJsonObject(Class<T> classOfT) {
		return JsonUtil.fromJsonObject(returnContent, classOfT);
	}
	
	public List<Map<String, Object>> getContentAsJsonArray(){
		return JsonUtil.fromJsonArray(returnContent);
	}
	
	public <T> List<T> getContentAsJsonArray(Class<T> classOfT){
		return JsonUtil.fromJsonArray(returnContent, List.class, classOfT);
	}
}
