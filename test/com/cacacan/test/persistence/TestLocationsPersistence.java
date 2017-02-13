package com.cacacan.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;

import com.cacacan.assignationofemployees.LocationEmployeeAssigner;
import com.cacacan.model.Employee;
import com.cacacan.model.Location;
import com.cacacan.persistence.Broker;
import com.cacacan.persistence.LocationToCleanDAO;

public class TestLocationsPersistence {

	@Before
	public void setup() throws Exception {
		LocationToCleanDAO.dropTable();
		LocationToCleanDAO.createTable();
	}

	@Test
	public void testConnectionAccess() throws Exception {
		final Connection bd = Broker.get().getLocationsDB();
		assertNotNull(bd);
	}

	@Test
	public void testInsertLocations() throws Exception {
		assertEquals(0, LocationToCleanDAO.getLocationsToCleanForUser("Manolo").size());
		insertLocations();
		assertEquals(1, LocationToCleanDAO.getLocationsToCleanForUser("Manolo").size());
	}

	private void insertLocations() {
		final Location locationToClean = new Location("38.9342534", "-3.9848568");
		final java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
		final Employee assignedEmployee = LocationEmployeeAssigner.getEmployeeForLocation(locationToClean);
		LocationToCleanDAO.insertLocationToClean(locationToClean, assignedEmployee, currentDate, null);
	}

}
