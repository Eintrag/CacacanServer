package com.cacacan.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JSONUtils {
	private static final Logger LOGGER = LogManager.getLogger(JSONUtils.class);

	public static String readInputStream(InputStream inputStream) {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String content = "";
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				content += line;
			}
		} catch (IOException e) {
			LOGGER.error("Error reading received location message");
		}
		LOGGER.info(content);
		content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
		return content;
	}
}
