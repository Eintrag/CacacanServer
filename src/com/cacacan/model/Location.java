package com.cacacan.model;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.cacacan.receiver.JSONUtils;

public class Location {
	String latitude;
	String longitude;

	public Location(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(InputStream inputStream) throws JSONException {
		final JSONObject parsedJSONObject = new JSONObject(JSONUtils.readInputStream(inputStream));
		this.latitude = parsedJSONObject.getString("latitude");
		this.longitude = parsedJSONObject.getString("longitude");

	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "Location = latitude: " + latitude + " longitude = " + longitude;
	}

}
