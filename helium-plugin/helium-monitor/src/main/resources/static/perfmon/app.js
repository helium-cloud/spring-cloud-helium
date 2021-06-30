Ext.Loader.setConfig({
			enabled : true
		});
var cookieHelper = new CookieHelper();
var cookie = cookieHelper.getCookie("cookId", true);
Ext.application({
			name : 'MO',
			appFolder : 'App',
			controllers : ['Controller', 'CBController'],
			launch : function() {
				Ext.create('Ext.container.Viewport', {
							layout : 'border',
							items : [Ext.widget('treePanel'),
									Ext.widget('tabpanel'), {
										region : 'north',
										autoHeight : true,
										border : false,
										margins : '0 0 5 0',
										bodyStyle : 'background-color:#BBCCEE;',
										items : [Ext.widget('comboBox', {
													id : 'comboBox',
													value : 2
												})]
									}, {
										region : 'south',
										title : '当前数据服务器端时间',
										height : 50,
										minHeight : 50,
										contentEl : 'time',
										bodyStyle : 'background-color:#BBCCEE;'
									}]
						});
			}
		});
