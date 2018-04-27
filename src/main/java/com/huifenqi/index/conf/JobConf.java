package com.huifenqi.index.conf;

public class JobConf {
	
	private String name = null;
	private String cronExpression = null;
	private String configName = null;
	private String id = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "JobConf [name=" + name + ", cronExpression=" + cronExpression
				+ ", configName=" + configName + ", id=" + id + "]";
	}
	
	
}
