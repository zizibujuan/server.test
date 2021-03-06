package com.zizibujuan.server.test.servlet;

import java.util.HashMap;
import java.util.Map;

import com.zizibujuan.drip.server.util.dao.DatabaseUtil;
import com.zizibujuan.useradmin.server.model.UserInfo;
import com.zizibujuan.useradmin.server.service.UserService;
import com.zizibujuan.useradmin.server.servlets.UserAdminServiceHolder;

/**
 * 支持用户登录
 * 
 * TODO: 具体实现，不应放在这个项目中
 * 
 * @author jzw
 * @since 0.0.1
 */
public class AuthorizedUserServlet extends DatabaseSupportServletTest{

	
	protected static UserService userService = UserAdminServiceHolder.getDefault().getUserService();
	
	protected static String testUserEmail = "test@zizibujuan.com";
	protected static String testUserPassword = "test123";
	protected static String testUserLoginName = "test";
	protected Long testUserId;
	
	/**
	 * 创建一个测试用户，
	 * 激活测试用户，
	 * 并使用测试用户登录
	 * @return
	 */
	protected void loginTestUser(){
		testUserId = createAndLoginUser(testUserEmail, testUserPassword, testUserLoginName);
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
	
	/**
	 * 创建用户，并使用该用户登录
	 * 
	 * @param email 邮箱地址
	 * @param password 密码
	 * @param loginName 登录用户
	 * @return 用户标识
	 */
	protected Long loginUser(String email, String password, String loginName){
		Long userId = createAndLoginUser(email, password, loginName);
		activeUser(userId);
		return userId;
	}
	
	protected void logoutUser(Long userId){
		removeUser(userId);
		xhr.post("logout");
	}
	
	private Long createAndLoginUser(String email, String password, String loginName){
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("password", password);
		params.put("loginName", loginName);
		// 注册并登录
		xhr.post("users", params);
		return DatabaseUtil.queryForLong(dataSource, "SELECT DBID FROM DRIP_USER_INFO WHERE EMAIL=?", email);
	}
	
	protected Long createUser(String email, String password, String loginName){
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(email);
		userInfo.setPassword(password);
		userInfo.setLoginName(loginName);
		Long userId = userService.register(userInfo);
		activeUser(userId);
		return userId;
	}
	
	protected void removeUser(Long userId){
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
