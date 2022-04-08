package org.dgonzalo.headlessbroadcaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeadlessBroadcasterApplication {

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		SpringApplication.run(HeadlessBroadcasterApplication.class, args);
	}
}
