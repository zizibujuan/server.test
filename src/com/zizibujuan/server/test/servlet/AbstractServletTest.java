package com.zizibujuan.server.test.servlet;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

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
	
	@Before
	public void setUp(){
		formData = new HashMap<String, Object>();
	}
	
	
}
