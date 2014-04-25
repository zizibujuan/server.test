package com.zizibujuan.server.test.servlet;

import java.util.Arrays;

import javax.sql.DataSource;

import com.zizibujuan.dbaccess.mysql.service.DataSourceHolder;
import com.zizibujuan.drip.server.util.dao.DatabaseUtil;

/**
 * 支持访问数据库
 * 
 * @author jzw
 * @since 0.0.1
 */
public class DatabaseSupportServletTest extends AbstractServletTest{

	protected static DataSource dataSource = DataSourceHolder.getDefault().getDataSourceService().getDataSource();
	
	protected void clearTables(String... tables) {
		Arrays.stream(tables).forEach(table -> DatabaseUtil.update(dataSource, "DELETE FROM " + table));
	}
	
}
