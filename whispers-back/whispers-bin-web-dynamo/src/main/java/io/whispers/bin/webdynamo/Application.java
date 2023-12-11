package io.whispers.bin.webdynamo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.whispers")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
