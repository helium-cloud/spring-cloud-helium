package org.helium.perfmon.observation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.helium.superpojo.type.DateTime;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class ObserverReport {
	private String category;
	private DateTime time;
	private List<ObserverReportColumn> columns;
	private List<ObserverReportRow> rows;
	
	private static final DecimalFormat dformat = new DecimalFormat("0.00");

	public DateTime getTime() {
		return time;
	}

	public String getCategory() {
		return category;
	}

	public ObserverReport(String category, List<ObserverReportColumn> columns, DateTime time) {
		this.time = time;
		this.columns = columns;
		this.category = category;

		this.rows = new ArrayList<ObserverReportRow>();
	}

	private ObserverReport(String category, List<ObserverReportColumn> columns, DateTime time, List<ObserverReportRow> rows) {
		this(category, columns, time);
		this.rows = rows;
	}

	public ObserverReportRow newRow(String instance) {
		ObserverReportRow row = new ObserverReportRow(columns.size(), instance);
		rows.add(row);
		return row;
	}

	public byte[] encodeToProtobuf() throws IOException {

		ObserverReportEntity entity = getProtoEntity();
		return entity.toPbByteArray();
	}

	public ObserverReportEntity getProtoEntity() {
		ArrayList<ObserverReportRowBean> rowBeans = new ArrayList<ObserverReportRowBean>();
		for (ObserverReportRow row : rows) {
			ObserverReportRowBean rowBean = new ObserverReportRowBean();
			rowBean.setData(row.getData());
			rowBean.setInstance(row.getInstanceName());
			rowBeans.add(rowBean);
		}

		ObserverReportEntity entity = new ObserverReportEntity();
		entity.setCategory(category);
		entity.setTime(time.getDate());
		entity.setColumns(columns);
		entity.setRows(rowBeans);
		return entity;
	}

	public List<ObserverReportColumn> getColumns() {
		return columns;
	}

	public List<ObserverReportRow> getRows() {
		return rows;
	}

	public String encodeToJson() {

		JsonObject reportObject = new JsonObject();
		JsonObject reportData = new JsonObject();
		reportData.addProperty("category", category);// add 'category'
		reportData.addProperty("time", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time.getTime())));// add
																														// 'time'
		JsonArray columnsArray = new JsonArray();
		JsonObject colObject;
		JsonObject colData;
		for (ObserverReportColumn col : columns) {
			colObject = new JsonObject();
			colData = new JsonObject();
			colData.addProperty("name", col.getName());
			colData.addProperty("type", col.getType().toString());
			colObject.add("col", colData);
			columnsArray.add(colObject);
		}
		reportData.add("columns", columnsArray);// add 'columns'
		JsonArray rowsArray = new JsonArray();
		JsonObject rowData;
		for (ObserverReportRow row : rows) {
			rowData = new JsonObject();
			rowData.addProperty("instance", row.getInstanceName());// add in
																	// 'instance'
			JsonArray dataArray = new JsonArray();
			for (int j = 0; j < row.getData().length; j++) {
				if (columns.get(j).getType() == ObserverReportColumnType.LONG) {
					long data = Long.parseLong(row.getData()[j]);
					dataArray.add(new JsonPrimitive(dformat.format(data)));
				} else if (columns.get(j).getType() == ObserverReportColumnType.DOUBLE) {
					double data = Double.parseDouble(row.getData()[j]);
					dataArray.add(new JsonPrimitive(dformat.format(data)));
				}else if (columns.get(j).getType() == ObserverReportColumnType.RATIO) {
					double data = Double.parseDouble(row.getData()[j]);
					dataArray.add(new JsonPrimitive(dformat.format(data)));
				} else {
					dataArray.add(new JsonPrimitive(row.getData()[j]));
				}
			}
			rowData.add("data", dataArray);
			JsonObject rowObject = new JsonObject();
			rowObject.add("row", rowData);
			rowsArray.add(rowObject);
		}
		reportData.add("rows", rowsArray);// add 'rows'
		reportObject.add("report", reportData);
		return reportObject.toString();
	}

	public static ObserverReport decodeFromProtobuf(byte[] buffer) throws IOException {
		ObserverReportEntity reportBean = new ObserverReportEntity();
		reportBean.parsePbFrom(buffer);
		ArrayList<ObserverReportRow> standardRows = new ArrayList<ObserverReportRow>();
		for (ObserverReportRowBean rowBean : reportBean.getRows()) {
			ObserverReportRow row = new ObserverReportRow(rowBean.getData(), rowBean.getInstance());
			standardRows.add(row);
		}
		return new ObserverReport(reportBean.getCategory(), reportBean.getColumns(), new DateTime(reportBean.getTime()), standardRows);
	}
}
