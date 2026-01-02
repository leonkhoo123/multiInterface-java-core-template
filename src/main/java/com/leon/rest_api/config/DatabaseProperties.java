package com.leon.rest_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "db")
public class DatabaseProperties {
    private String url;
    private String username;
    private String password;
    
	public String getUrl() {
		return "jdbc:mysql://"+url+"?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true";
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
}
