package com.trading.forex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EntityScan(basePackageClasses = {
		ForexServiceWebserviceApplication.class
})
public class ForexServiceWebserviceApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ForexServiceWebserviceApplication.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ForexServiceWebserviceApplication.class, args);
	}

}
