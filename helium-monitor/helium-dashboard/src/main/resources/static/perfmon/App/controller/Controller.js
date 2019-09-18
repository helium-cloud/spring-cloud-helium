Ext.define('MO.controller.Controller', {
			extend : 'Ext.app.Controller',

			views : ['TreePanel', 'TabPanel'],

			init : function() {
				this.control({
							'treePanel [text=ALL]' : {
								click : this.selectAll
							},
							'treePanel [text=SUM]' : {
								click : this.selectAll
							}
						});
			},
			selectAll : function(button) {
				var viewport = button.up('viewport');
				var treePanel = viewport.down('treePanel');
				var checkedNodes = treePanel.getSelectionModel().getSelection()[0];
				var data;
				for (var c = checkedNodes; !c.data.root; c = checkedNodes.parentNode) {
					if (!c.data.leaf) {
						data = c.data;
						break;
					}
				}
				MsgTip.msg('成功', "订阅成功,数据准备中,请稍候", true, 3000);
				Ext.Ajax.request({
							url : baseUrl+'/subscribe',
							method: 'POST',
                            params: {
                            	'category': data.text,
                            	'instance': 'all',
                            	'interval': 1,
                            	'cookie': cookie
                            },
							timeout : 10000,// 最大等待时间,超出则会触发超时
							success : function(resp, option) {// ajax请求发送成功时执行
								var tabpanel = viewport.down('tabpanel');
								var comboBox = viewport.down('comboBox');
								comboBox.setDisabled(false);
								tabpanel.runtask();
							},
							failure : function(data) {
								Ext.Msg.alert("failure", "Subscribe failure");
							}
						});
			},
			selectSUM : function() {
			}
		});