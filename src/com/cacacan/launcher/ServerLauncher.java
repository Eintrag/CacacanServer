package com.cacacan.launcher;

import com.cacacan.receiver.LocationGetterHTTPServerRunnable;

public class ServerLauncher {

	public static void main(String[] args) {
		final Thread receivingThread = new Thread(new LocationGetterHTTPServerRunnable());
		receivingThread.start();
	}
}
