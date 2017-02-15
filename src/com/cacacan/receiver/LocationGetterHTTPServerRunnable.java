package com.cacacan.receiver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.cacacan.assignationofemployees.LocationEmployeeAssigner;
import com.cacacan.model.AvailableActionsEnum;
import com.cacacan.model.Location;
import com.cacacan.persistence.LocationToCleanDAO;
import com.cacacan.sender.GcmSenderRunnable;
import com.google.gson.Gson;

public class LocationGetterHTTPServerRunnable implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(LocationGetterHTTPServerRunnable.class);
	private static int PORT_TO_LISTEN = 4567;

	@Override
	public void run() {
		LOGGER.info("Started receiver thread");

		listenOnPort(getHerokuAssignedPort());
	}

	public static int getHerokuAssignedPort() {
		final ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567;
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
		final String jsoString = ServerUtils.readInputStream(socket.getInputStream());
		try {
			final JSONObject jso = new JSONObject(jsoString);
			final AvailableActionsEnum actionType = AvailableActionsEnum.valueOf(jso.getString("action_type"));
			switch (actionType) {
			case SEND_LOCATION:
				final Location receivedLocation = new Location(jso);
				sendResponse(socket);
				LocationToCleanDAO.insertLocationToClean(receivedLocation, LocationEmployeeAssigner
						.getEmployeeForLocation(receivedLocation), new java.sql.Date(System.currentTimeMillis()), null);
				sendNewLocationNoticeViaGCM();
				break;
			case GET_LOCATIONS_FOR_USER:
				final int maxNumberOfLocations = 5;
				final List<Location> locationsToClean =
						LocationToCleanDAO.getLocationsToCleanForUser(jso.getString("user"), maxNumberOfLocations);
				String message = "";
				if (!locationsToClean.isEmpty()) {
					message = new Gson().toJson(locationsToClean);
				}

				final DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				outToServer.writeBytes(message);
				outToServer.flush();
				outToServer.close();
				break;
			default:
			}

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
