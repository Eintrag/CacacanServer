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
	public void testInsertAndFetchLocations() throws Exception {
		assertEquals(0, LocationToCleanDAO.getLocationsToCleanForUser("Manolo", 1).size());
		insertLocations();
		assertEquals(1, LocationToCleanDAO.getLocationsToCleanForUser("Manolo", 1).size());
		assertEquals(2, LocationToCleanDAO.getLocationsToCleanForUser("Manolo", 2).size());

	}

	private void insertLocations() {
		final Location locationToClean = new Location("41.4445574", "-3.3678568");
		final Location locationToClean2 = new Location("40.4514524", "-5.9843318");

		final java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
		final Employee assignedEmployee = LocationEmployeeAssigner.getEmployeeForLocation(locationToClean);
		// insert 2 locations to clean
		LocationToCleanDAO.insertLocationToClean(locationToClean, assignedEmployee, currentDate, null);
		LocationToCleanDAO.insertLocationToClean(locationToClean2, assignedEmployee, currentDate, null);
	}

}
