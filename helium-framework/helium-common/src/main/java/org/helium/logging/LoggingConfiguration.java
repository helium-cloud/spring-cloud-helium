package org.helium.logging;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * logging.xml的配置
 * Created by Coral on 8/26/15.
 */

public class LoggingConfiguration extends SuperPojo {
	private String level;

	private List<SubLevelNode> subLevels = new ArrayList<>();

	private List<OutputNode> outputs = new ArrayList<>();

	private List<FilterNode> filters = new ArrayList<>();

	public String getLevel() {
		return level;
	}

	public LoggingConfiguration setLevel(String level) {
		this.level = level;
		return this;
	}

	public List<SubLevelNode> getSubLevels() {
		return subLevels;
	}

	public LoggingConfiguration setSubLevels(List<SubLevelNode> subLevels) {
		this.subLevels = subLevels;
		return this;
	}

	public List<OutputNode> getOutputs() {
		return outputs;
	}

	public LoggingConfiguration setOutputs(List<OutputNode> outputs) {
		this.outputs = outputs;
		return this;
	}

	public List<FilterNode> getFilters() {
		return filters;
	}

	public LoggingConfiguration setFilters(List<FilterNode> filters) {
		this.filters = filters;
		return this;
	}

	public static class SubLevelNode extends SuperPojo {

		private String name;


		private String level;

		private String output;

		public String getName() {
			return name;
		}

		public SubLevelNode setName(String name) {
			this.name = name;
			return this;
		}

		public String getLevel() {
			return level;
		}

		public SubLevelNode setLevel(String level) {
			this.level = level;
			return this;
		}

		public String getOutput() {
			return output;
		}

		public SubLevelNode setOutput(String output) {
			this.output = output;
			return this;
		}
	}

	public static class OutputNode extends SuperPojo {

		private String name;


		private List<AppenderNode> appenders = new ArrayList<>();

		public String getName() {
			return name;
		}

		public OutputNode setName(String name) {
			this.name = name;
			return this;
		}

		public List<AppenderNode> getAppenders() {
			return appenders;
		}

		public void setAppenders(List<AppenderNode> appenders) {
			this.appenders = appenders;
		}
	}

	public static class AppenderNode extends SuperPojo {

		private String clazz;


		private List<SetterNode> setters = new ArrayList<>();

		public String getClazz() {
			return clazz;
		}

		public AppenderNode setClazz(String clazz) {
			this.clazz = clazz;
			return this;
		}

		public List<SetterNode> getSetters() {
			return setters;
		}

		public AppenderNode setSetters(List<SetterNode> setters) {
			this.setters = setters;
			return this;
		}
	}

	public static class SetterNode extends SuperPojo {

		private String field;

		public String getField() {
			return field;
		}

		public SetterNode setField(String field) {
			this.field = field;
			return this;
		}

	}

	public static class FilterNode extends SuperPojo {

		private String loggerName;

		private String clazz;

		private String params;

		private String output;

		private List<SetterNode> setters = new ArrayList<>();

		public String getLoggerName() {
			return loggerName;
		}

		public void setLoggerName(String loggerName) {
			this.loggerName = loggerName;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public String getOutput() {
			return output;
		}

		public void setOutput(String output) {
			this.output = output;
		}

		public String getParams() {
			return params;
		}

		public void setParams(String params) {
			this.params = params;
		}

		public List<SetterNode> getSetters() {
			return setters;
		}

		public void setSetters(List<SetterNode> setters) {
			this.setters = setters;
		}

	}
}
