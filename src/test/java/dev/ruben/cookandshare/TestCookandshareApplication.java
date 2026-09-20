package dev.ruben.cookandshare;

import org.springframework.boot.SpringApplication;

public class TestCookandshareApplication {

	public static void main(String[] args) {
		SpringApplication.from(CookandshareApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
