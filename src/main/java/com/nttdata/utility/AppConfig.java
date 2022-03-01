package com.nttdata.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.nttdata.model.Application;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties/*(prefix = "application")*/
public class AppConfig {

	@NestedConfigurationProperty
	public Application application;
}
