package com.cacacan.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Broker {
	private String url = "jdbc:sqlite:locations.db";
	private static Broker broker;

	private Broker() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		}
	}

	public static Broker get() {
		if (broker == null)
			broker = new Broker();
		return broker;
	}

	public Connection getLocationsDB() throws SQLException {
		return DriverManager.getConnection(url);
	}

}
