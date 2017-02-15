package com.cacacan.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerUtils {
	private static final Logger LOGGER = LogManager.getLogger(ServerUtils.class);

	public static String readInputStream(InputStream inputStream) {
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String content = "";
		try {
			content = br.readLine();
		} catch (final IOException e) {
			LOGGER.error("Error reading received message");
		}
		LOGGER.info(content);
		content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
		return content;
	}
}
