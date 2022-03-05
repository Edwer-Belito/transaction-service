package com.nttdata.common;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

@Data
public class Application {

	private String location;
	
	
	@NestedConfigurationProperty
	private Config config;
	
	@NestedConfigurationProperty
	private Operation operation;

}
