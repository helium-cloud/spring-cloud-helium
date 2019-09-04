Ext.define('MO.view.TabPanel', {
	extend : 'Ext.tab.Panel',
	alias : 'widget.tabpanel',
	region : 'center',
	task : null,
	initComponent : function() {
		this.callParent(arguments);
	},
	runtask : function() {
		// 创建最新的轮训
		if (this.task == null) {
			this.task = this.createTask()
			Ext.TaskManager.start(this.task);
		} else {// 刷新
			Ext.TaskManager.stop(this.task);
			this.task = this.createTask()
			Ext.TaskManager.start(this.task);
		}

	},
	createTask : function() {
		var comboBox = Ext.getCmp('comboBox');
		var times = comboBox.getValue() * 1000;
		return {
			run : function() {
				Ext.Ajax.request({
					url : baseUrl+'/pull?cookie=' + cookie,
					disableCaching : true,// 是否禁用缓存
					timeout : 10000,// 最大等待时间,超出则会触发超时
					success : function(resp, option) {// ajax请求发送成功时执行
						var reports = Ext.decode(resp.responseText);
						var viewport = comboBox.up('viewport');
						var tab = viewport.down('tabpanel');
						if (reports == null || reports == undefined) {
							return;
						}
						for (var i = 0; i < reports.length; i++) {
							if (reports[i].report == null || reports[i].report == undefined) {
								continue;
							}
							var category = reports[i].report.category;
							var time = reports[i].report.time;
							document.getElementById('time').innerHTML = "<center/>"
									+ "服务器端时间：" + time;
							var haveThisGrid = false;
							tab.items.each(function(item) {
								if (item.title == category) {
									var report = reports[i].report;
									haveThisGrid = true;
									var readData = [];
									if (report.rows.length > 0) {
										for (var j = 0; j < report.rows.length; j++) {
											var oneData = [];
											var instance = report.rows[j].row.instance;
											var itemArray = report.rows[j].row.data;
											oneData.push(instance);
											for (var m = 0; m < itemArray.length; m++) {
												oneData.push(itemArray[m]);
											}
											readData.push(oneData);
										}
									}
									item.getStore().loadData(readData);
								}
							}, this);
							if (!haveThisGrid) {
								var report = reports[i].report;
								// 不存在的
								var fields = new Array();
								var columns = new Array();
								fields[0] = "instance";
								columns[0] = {
									header : "instance",
									dataIndex : "instance",
									width : 220
								};
								if (report.columns.length > 0) {
									for (var j = 0; j < report.columns.length; j++) {
										var name = report.columns[j].col.name;
										if (name != null && name != undefined) {
											dataIndex = name.replace(/\//g, "")
													.replace(/\./g, "")
													.replace(/\(/g, "")
													.replace(/\)/g, "");
										}
										fields[j + 1] = dataIndex;
										columns[j + 1] = {
											header : name,
											dataIndex : dataIndex
										};
									}
								}
								var readData = [];
								if (report.rows.length > 0) {
									for (var j = 0; j < report.rows.length; j++) {
										var oneData = [];
										var instance = report.rows[j].row.instance;
										var itemArray = report.rows[j].row.data;
										oneData.push(instance);
										for (var m = 0; m < itemArray.length; m++) {
											oneData.push(itemArray[m]);
										}
										readData.push(oneData);
									}
								}
								var store = new Ext.data.ArrayStore({ // 配置参数必有：fields。
									fields : fields
								});
								store.loadData(readData);
								// 不存在创建新的grid;
								var grid = Ext.create('Ext.grid.Panel', {
									title : category,
									store : store,
									columns : columns,
									closable : true,
									multiSelect : true,
									collapsible : false,
									autoScroll : true,
									listeners : {
										celldblclick : function(grid, rowIndex,
												columnIndex, e) {
											var fields = e.fields.items[columnIndex].name;
											var data = e.get(fields);
											var editWindow = Ext.create(
													'Ext.window.Window', {
														autoDestroy : true,
														title : '查看详细',
														closable : true,
														closeAction : 'hide',
														width : 300,
														height : 300,
														layout : 'fit',
														border : false,
														bodyStyle : 'padding: 5px;',
														items : [{
																	xtype : 'textarea',
																	fieldLabel : '',
																	inputType : 'text',
																	value : data,
																	readOnly : true
																}]
													});
											editWindow.show();
										}
									}
								});
								tab.add(grid);
								tab.setActiveTab(grid);
							}
						}

					},
					failure : function(data) {
						Ext, Msg.alert("failure", "Pull failure");
					}
				});
			},
			interval : times
		};
	}
});