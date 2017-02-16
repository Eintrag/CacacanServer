package com.cacacan.launcher;

import org.apache.log4j.BasicConfigurator;

import com.cacacan.receiver.LocationGetterHTTPServerRunnable;

public class ServerLauncher {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		new LocationGetterHTTPServerRunnable().run();
	}
}
