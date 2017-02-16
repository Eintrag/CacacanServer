package com.cacacan.receiver;

import static spark.Spark.get;
import static spark.Spark.port;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.cacacan.assignationofemployees.LocationEmployeeAssigner;
import com.cacacan.model.Location;
import com.cacacan.persistence.LocationToCleanDAO;
import com.cacacan.sender.GcmSenderRunnable;
import com.google.gson.Gson;

public class LocationGetterHTTPServerRunnable implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(LocationGetterHTTPServerRunnable.class);

	@Override
	public void run() {
		LOGGER.info("Started receiver thread");
		port(getHerokuAssignedPort());
		get("/hello", (req, res) -> "Hello World");

		get("/getLocations/:user", (req, res) -> {
			try {
				res.type("application/json");
				return getLocationsForUserJSON(req.params(":user"));
			} catch (final Exception e) {
				LOGGER.error(e.getStackTrace().toString());
				return "{}";
			}
		});

		get("/sendLocation/:location", (req, res) -> {
			try {

				final JSONObject jso = new JSONObject(req.params(":location"));
				final Location receivedLocation = new Location(jso);
				LocationToCleanDAO.insertLocationToClean(receivedLocation, LocationEmployeeAssigner
						.getEmployeeForLocation(receivedLocation), new java.sql.Date(System.currentTimeMillis()), null);
				sendNewLocationNoticeViaGCM();
				return "HTTP/1.1 200 OK\r\n";
			} catch (final Exception e) {
				LOGGER.error(e.getStackTrace().toString());
				return "{}";
			}
		});
	}

	public static int getHerokuAssignedPort() {
		final ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567;
	}

	private String getLocationsForUserJSON(String user) {
		final int maxNumberOfLocations = 50;
		final List<Location> locationsToClean =
				LocationToCleanDAO.getLocationsToCleanForUser(user, maxNumberOfLocations);
		String message = "";
		if (!locationsToClean.isEmpty()) {
			message = new Gson().toJson(locationsToClean);
		}
		return message;

	}

	private void sendNewLocationNoticeViaGCM() {
		final GcmSenderRunnable sendingRunnable = new GcmSenderRunnable();
		sendingRunnable.setMessage("New location assigned to you");
		new Thread(sendingRunnable).start();
	}
}
