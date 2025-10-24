package com.example.loginapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.loginapp.mapper")
public class LoginappApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginappApplication.class, args);
	}

}
