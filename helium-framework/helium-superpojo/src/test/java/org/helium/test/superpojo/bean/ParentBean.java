package org.helium.test.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

public class ParentBean extends SuperPojo {

	@Field(id = 1)
	private int id;

	@Field(id = 2)
	private String parentName;

	@Field(id = 3)
	private int parentLength;

	@Field(id = 4)
	private Table parentTable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public int getParentLength() {
		return parentLength;
	}

	public void setParentLength(int parentLength) {
		this.parentLength = parentLength;
	}

	public Table getParentTable() {
		return parentTable;
	}

	public void setParentTable(Table parentTable) {
		this.parentTable = parentTable;
	}

	public static class ChildBean extends ParentBean {

		@Field(id = 11)
		private int id;

		@Field(id = 12)
		private String childName;

		@Field(id = 13)
		private int childLength;

		@Field(id = 14)
		private Table childTable;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getChildName() {
			return childName;
		}

		public void setChildName(String childName) {
			this.childName = childName;
		}

		public int getChildLength() {
			return childLength;
		}

		public void setChildLength(int childLength) {
			this.childLength = childLength;
		}

		public Table getChildTable() {
			return childTable;
		}

		public void setChildTable(Table childTable) {
			this.childTable = childTable;
		}

	}
}
