/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cacacan.sender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

// NOTE:
// This class emulates a server for the purposes of this sample,
// but it's not meant to serve as an example for a production app server.
// This class should also not be included in the client (Android) application
// since it includes the server's API key. For information on GCM server
// implementation see: https://developers.google.com/cloud-messaging/server
public class GcmSenderRunnable implements Runnable {

	public static final String API_KEY =
			"AAAAjz6EIaA:APA91bGwDCngM_god02Rvblo2dlVeo0IV8Hb9mcn25G6xePM9fNjAw2phYnIrACjBt2t4zNAgHmTQBj0w9w-s4DNrPB4d8r2becUMnPiDvKeBLP2AClY8BNlJKgVuGTLVKR50ktOaYzX";
	private static final Logger LOGGER = LogManager.getLogger(GcmSenderRunnable.class);
	private String message = "remember to set a message";

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		LOGGER.info("Started sender thread");
		try {
			// Prepare JSON containing the GCM message content. What to send and
			// where to send.
			final JSONObject jGcmData = new JSONObject();
			final JSONObject jData = new JSONObject();
			jData.put("message", message.trim());
			// Where to send GCM message.
			jGcmData.put("to", "/topics/global");

			// What to send in GCM message.
			jGcmData.put("data", jData);

			// Create connection to send GCM Message request.
			final URL url = new URL("https://android.googleapis.com/gcm/send");
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "key=" + API_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// Send GCM message content.
			final OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jGcmData.toString().getBytes());
			LOGGER.info("Sent message via GCM: " + message);
			// Read GCM response.
			final InputStream inputStream = conn.getInputStream();
			final String resp = IOUtils.toString(inputStream);
			LOGGER.info(resp);
		} catch (IOException | JSONException e) {
			LOGGER.error("Unable to send GCM message.\n"
					+ "Please ensure that API_KEY has been replaced by the server "
					+ "API key, and that the device's registration token is correct (if specified).");
		}
	}

}
