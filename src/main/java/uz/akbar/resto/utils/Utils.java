package uz.akbar.resto.utils;

import java.time.format.DateTimeFormatter;

/**
 * Utils
 */
public interface Utils {

	String BASE_URL = "/api/v1";

	String DATE_TIME_PATTERN = "yyyy/MM/dd";

	DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
}
