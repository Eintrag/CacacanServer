package com.cacacan.receiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.cacacan.assignationofemployees.LocationEmployeeAssigner;
import com.cacacan.model.Location;
import com.cacacan.persistence.LocationToCleanDAO;
import com.cacacan.sender.GcmSenderRunnable;

public class LocationGetterHTTPServerRunnable implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(LocationGetterHTTPServerRunnable.class);
	private static final int PORT_TO_LISTEN = 8080;

	@Override
	public void run() {
		LOGGER.info("Started receiver thread");
		listenOnPort(PORT_TO_LISTEN);
	}

	private void listenOnPort(int port) {
		try (ServerSocket server = new ServerSocket(port)) {
			LOGGER.info("Listening for connection on port " + port);
			while (true) {
				try (Socket socket = server.accept()) {
					serverActions(socket);
				}

			}
		} catch (final IOException e) {
			LOGGER.error("Receiver thread failed");
		}
	}

	private void serverActions(Socket socket) throws IOException {
		try {
			final Location receivedLocation = new Location(socket.getInputStream());
			sendResponse(socket);
			LocationToCleanDAO.insertLocationToClean(receivedLocation, LocationEmployeeAssigner.getEmployeeForLocation(
					receivedLocation), new java.sql.Date(System.currentTimeMillis()), null);
			sendNewLocationNoticeViaGCM();
		} catch (final JSONException e) {
			LOGGER.error("Could not parse JSON from http");
		}
	}

	private void sendResponse(Socket socket) throws IOException {
		final String httpResponse = "HTTP/1.1 200 OK\r\n";
		socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
	}

	private void sendNewLocationNoticeViaGCM() {
		final GcmSenderRunnable sendingRunnable = new GcmSenderRunnable();
		sendingRunnable.setMessage("New location assigned to you");
		new Thread(sendingRunnable).start();
	}
}
