package com.zizibujuan.server.test.servlet;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;



/**
 * servlet测试基类
 * 
 * @author jzw
 * @since 0.0.1
 */
public class AbstractServletTest {

	/**
	 * 约定所有的servlet测试都运行在8199端口上
	 */
	protected static final String SERVER_LOCATION = "http://localhost:8199/";
	
	protected XMLHttpRequest xhr = new XMLHttpRequest(SERVER_LOCATION);
	
	protected Map<String, Object> formData;
	
	@BeforeClass
	public static void setUpClass(){
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}
	
	
	@Before
	public void setUp(){
		formData = new HashMap<String, Object>();
	}
	
	@After
	public void tearDown(){
		
	}

	
}
