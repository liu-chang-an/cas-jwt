package com.gccloud.jwtdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hy
 */
@SpringBootApplication(scanBasePackages = {"com.gccloud.jwtdemo"})
public class JwtDemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(JwtDemoApplication.class, args);
	}

}
