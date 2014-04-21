package com.zizibujuan.server.test.servlet;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;

import com.zizibujuan.dbaccess.mysql.service.DataSourceHolder;
import com.zizibujuan.drip.server.util.dao.DatabaseUtil;
import com.zizibujuan.useradmin.server.service.UserService;
import com.zizibujuan.useradmin.server.servlets.UserAdminServiceHolder;

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
	
	
	protected static DataSource dataSource = DataSourceHolder.getDefault().getDataSourceService().getDataSource();
	protected static UserService userService = UserAdminServiceHolder.getDefault().getUserService();
	
	protected static String testUserEmail = "test@zizibujuan.com";
	protected static String testUserPassword = "test123";
	protected static String testUserLoginName = "test";
	protected Long testUserId;
	
	protected void clearTables(String... tables) {
		Arrays.stream(tables).forEach(table -> DatabaseUtil.update(dataSource, "DELETE FROM " + table));
	}
	
	/**
	 * 创建一个测试用户，
	 * 激活测试用户，
	 * 并使用测试用户登录
	 * @return
	 */
	protected void loginTestUser(){
		testUserId = createUser(testUserEmail, testUserPassword, testUserLoginName);
		activeUser(testUserId);
	}
	
	/**
	 * 注销测试用户，
	 * 删除测试用户信息
	 */
	protected void logoutTestUser(){
		removeUser(testUserId);
		xhr.post("logout");
	}
	
	private Long createUser(String email, String password, String loginName){
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("password", password);
		params.put("loginName", loginName);
		// 注册并登录
		xhr.post("users", params);
		return DatabaseUtil.queryForLong(dataSource, "SELECT DBID FROM DRIP_USER_INFO WHERE EMAIL=?", email);
	}
	
	private void removeUser(Long userId){
		// 删除用户
		DatabaseUtil.update(dataSource, "DELETE FROM DRIP_USER_ATTRIBUTES WHERE USER_ID=?", userId);
		DatabaseUtil.update(dataSource, "DELETE FROM DRIP_USER_INFO  WHERE DBID=?", userId);
		// 删除自我关注信息,如果存在的话
		DatabaseUtil.update(dataSource, "DELETE FROM DRIP_USER_RELATION WHERE USER_ID=? AND WATCH_USER_ID=?", userId, userId);
		// 删除用户统计信息
		DatabaseUtil.update(dataSource, "DELETE FROM DRIP_USER_STATISTICS WHERE USER_ID=?", userId);
	}
	
	private void activeUser(Long userId){
		if(userId != null){
			userService.active(userId);
		}
	}
}
