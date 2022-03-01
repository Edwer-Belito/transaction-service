package com.nttdata.model;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

@Data
public class Application {

	public String location;
	
	
	@NestedConfigurationProperty
	public Config config;
	

}
