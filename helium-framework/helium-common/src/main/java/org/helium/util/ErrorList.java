package org.helium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/25/15.
 */
public class ErrorList {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorList.class);

	private List<Entry> errors = new ArrayList<>();
	public ErrorList() {
	}

	public void addError(String label, Throwable error) {
		LOGGER.warn("Label = " + label + " Error = {}", error);
		Entry e = new Entry();
		e.label = label;
		e.error = error;
		errors.add(e);
	}

	public boolean hasError() {
		return errors.size() > 0;
	}

	public List<Entry> getErrors() {
		return errors;
	}

	public void printToLogger(Logger logger) {
		for (Entry e: errors) {
			logger.error("label: " + e.label + " error:{}", e.error.toString());
		}
	}

	public static class Entry {
		private String label;
		private Throwable error;

		public String getLabel() {
			return label;
		}
		public Throwable getError() {
			return error;
		}
	}
}
