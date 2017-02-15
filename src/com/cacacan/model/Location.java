package com.cacacan.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {
	String latitude;
	String longitude;

	public Location(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(JSONObject jso) throws JSONException {
		this.latitude = jso.getString("latitude");
		this.longitude = jso.getString("longitude");

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

	public Object toJSON() throws JSONException {
		final JSONObject jso = new JSONObject();
		jso.put("latitude", latitude);
		jso.put("longitude", longitude);
		return jso;
	}

}
