package org.helium.logging;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.SuperPojoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * logging.xml的配置
 * Created by Coral on 8/26/15.
 */
@Entity(name = "logging")
public class LoggingConfiguration extends SuperPojo {
	@Field(id = 1, name = "level", type = NodeType.ATTR)
	private String level;

	@Childs(id = 2, parent = "subLevels", child = "subLevel")
	private List<SubLevelNode> subLevels = new ArrayList<>();

	@Childs(id = 3, parent = "outputs", child = "output")
	private List<OutputNode> outputs = new ArrayList<>();

	@Childs(id = 4, parent = "filters", child = "filter")
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
		@Field(id = 1, name = "name", type = NodeType.ATTR)
		private String name;

		@Field(id = 2, name = "level", type = NodeType.ATTR)
		private String level;

		@Field(id = 3, name = "output", type = NodeType.ATTR)
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
		@Field(id = 1, name = "name", type = NodeType.ATTR)
		private String name;

		@Childs(id = 2, parent = "", child = "appender")
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
		@Field(id = 1, name = "class", type = NodeType.ATTR)
		private String clazz;

		@Childs(id = 11, parent = "setters", child = "setter")
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
		@Field(id = 1, name = "field", type = NodeType.ATTR)
		private String field;

		public String getField() {
			return field;
		}

		public SetterNode setField(String field) {
			this.field = field;
			return this;
		}

		/**
		 * 获取结点中的xml结点
		 * @return
		 */
		public AnyNode getInnerNode() {
			return SuperPojoUtils.getAnyNode(this);
		}

		/**
		 * 获取xml结点中的文本
		 * @return
		 */
		public String getInnerText() {
			return SuperPojoUtils.getStringAnyNode(this);
		}
	}

	public static class FilterNode extends SuperPojo {
		@Field(id = 1, name = "loggerName", type = NodeType.ATTR)
		private String loggerName;

		@Field(id = 2, name = "class", type = NodeType.ATTR)
		private String clazz;

		@Field(id = 3, name = "params", type = NodeType.ATTR)
		private String params;

		@Field(id = 4, name = "output", type = NodeType.ATTR)
		private String output;

		@Childs(id = 11, parent = "setters", child = "setter")
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
