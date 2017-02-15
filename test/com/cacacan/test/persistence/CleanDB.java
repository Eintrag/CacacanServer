package com.cacacan.test.persistence;

import org.junit.Test;

import com.cacacan.persistence.LocationToCleanDAO;

public class CleanDB {

	@Test
	public void cleanDB() throws Exception {
		LocationToCleanDAO.dropTable();
		LocationToCleanDAO.createTable();
	}

}
