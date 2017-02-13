package com.cacacan.launcher;

import com.cacacan.receiver.LocationGetterHTTPServerRunnable;
import com.cacacan.sender.GcmSenderRunnable;

public class ServerLauncher {

	public static void main(String[] args) {
		final Thread receivingThread = new Thread(new LocationGetterHTTPServerRunnable());

		final GcmSenderRunnable sendingRunnable = new GcmSenderRunnable();
		sendingRunnable.setMessage("HOLA HOLA HOLA");
		final Thread sendingThread = new Thread(sendingRunnable);

		receivingThread.start();
		if (false) {
			sendingThread.start();
		}
	}
}
