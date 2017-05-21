package com.spider.business.repostory.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(locations = "application.properties", prefix = "jdbc.datasource", ignoreUnknownFields = false)
public class DataSourceProperties {

	/**
	 * 数据库驱动类
	 */
	private String driverClassName;
	/**
	 * 数据库路径
	 */
	private String url;
	/**
	 * 数据库名称
	 */
	private String username;
	/**
	 * 数据库密码
	 */
	private String password;
	/**
	 * 默认提交
	 */
	private boolean defaultAutoCommit = false;
	/**
	 * 连接池的最大数据库连接数。设为0表示无限制。
	 */
	private int maxActive = 100;
	/**
	 * 最大空闲数，数据库连接的最大空闲时间。超过空闲时间，数据库连接将被标记为不可用，然后被释放。设为0表示无限制。
	 */
	private int maxIdle = 8;
	/**
	 * 最小空闲数，
	 */
	private int minIdle = 8;
	/**
	 * 最大建立连接等待时间。如果超过此时间将接到异常。设为-1表示无限制。
	 */
	private int maxWait = 60000;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

}
