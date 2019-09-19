Ext.define('MO.controller.CBController', {
			extend : 'Ext.app.Controller',

			stores : ['ComboStore'],
			models : ['ComboModel'],

			views : ['ComboBox'],

			init : function() {
				this.control({
							'comboBox' : {
								select : this.selectRoot
							}
						});
			},
			selectRoot : function(combo, records, eOpts) {
				if (combo.value != "") {
					MsgTip.msg('刷新时间更新成功', "刷新时间:" + combo.value, true, 3000);
					var tabpanelAarry = Ext.ComponentQuery.query('tabpanel');
					var tabpanel = tabpanelAarry[0];
					tabpanel.runtask();
				}
			}
		});