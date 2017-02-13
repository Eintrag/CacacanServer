package com.cacacan.persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cacacan.model.Employee;
import com.cacacan.model.Location;

public class LocationToCleanDAO {

	/* @formatter:off
	 *
	 * id (auto increment)
	 * latitude
	 * longitude
	 * assigned_employee
	 * date_received
	 * date_cleaned
	 *
	 * @formatter:on
	 */
	private static final Logger LOGGER = LogManager.getLogger(LocationToCleanDAO.class);

	public static void insertLocationToClean(Location locationToClean, Employee assignedEmployee, Date dateReceived,
			Date dateCleaned) {
		try (Connection db = getConnection()) {
			final String sql = "insert into LocationsToClean values (?, ?, ?, ?, ?, ?)";
			final PreparedStatement ps = db.prepareStatement(sql);
			ps.setNull(1, 0);
			ps.setString(2, locationToClean.getLatitude());
			ps.setString(3, locationToClean.getLongitude());
			ps.setString(4, assignedEmployee.getName());
			ps.setDate(5, dateReceived);
			ps.setDate(6, dateCleaned);
			ps.execute();
		} catch (final SQLException sqle) {
			LOGGER.error("error inserting location to database");
		}
	}

	public static List<Location> getLocationsToCleanForUser(String assignedEmployee) {
		final List<Location> locationsToCleanForUser = new ArrayList<Location>();
		try (Connection db = getConnection()) {
			final String sql = "select latitude, longitude FROM LocationsToClean where assigned_employee = ?";
			final PreparedStatement ps = db.prepareStatement(sql);
			ps.setString(1, assignedEmployee);
			final ResultSet r = ps.executeQuery();
			while (r.next()) {
				locationsToCleanForUser.add(new Location(r.getString(1), r.getString(2)));
			}
			ps.close();
		} catch (final SQLException sqle) {
			LOGGER.error("error getting locations for user");
		}
		return locationsToCleanForUser;
	}

	public static void dropTable() throws SQLException {
		// TODO remove
		final Connection db = getConnection();
		final String sql = "DROP TABLE LocationsToClean;";
		final PreparedStatement ps = db.prepareStatement(sql);
		ps.execute();

		// database is in autocommit mode
	}

	public static void createTable() {
		// TODO remove
		try {
			final Connection db = getConnection();
			final String sql = "CREATE TABLE LocationsToClean "
					+ "(id INTEGER NOT NULL PRIMARY KEY, " + "latitude varchar(10) NOT NULL, "
					+ "longitude varchar(10) NOT NULL, " + "assigned_employee varchar(255) NOT NULL, "
					+ "date_received DATE NOT NULL, " + "date_cleaned DATE);";
			final PreparedStatement ps = db.prepareStatement(sql);
			ps.execute();

			// database is in autocommit mode
		} catch (final SQLException e) {
			LOGGER.error("Error creating table. Maybe it already exists?");
		}
	}

	private static Connection getConnection() {
		Connection db = null;
		try {
			db = Broker.get().getLocationsDB();
		} catch (final SQLException e) {
			LOGGER.error("Error getting connection");
		}
		return db;
	}
}
