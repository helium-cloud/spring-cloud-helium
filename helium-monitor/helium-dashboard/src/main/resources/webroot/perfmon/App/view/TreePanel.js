Ext.define('MO.view.TreePanel', {
			extend : 'Ext.tree.Panel',
			alias : 'widget.treePanel',
			title : '订阅列表',
			resizable : false,
			rootVisible : false,
			autoScroll : true,
			useArrows : true,
			autoHeight : true,
			collapsible : true,
			region : 'west',
			width : 150,
			initComponent : function() {
				Ext.Ajax.request({
							url : baseUrl+'/categories',
							disableCaching : true,// 是否禁用缓存
							timeout : 10000,// 最大等待时间,超出则会触发超时
							success : function(resp, option) {// ajax请求发送成功时执行
								var body = Ext.decode(resp.responseText);
								appendTreeChild(body);
							},
							failure : function(data) {
								Ext.Msg.alert("failure",
										"Get categories failure");
							}
						});
				function appendTreeChild(categories) {
					if (categories == null
							|| categories.length == 0) {
						return;
					}
					var treeArray = Ext.ComponentQuery.query('treePanel');
					var tree = treeArray[0];
					var root = tree.getRootNode();
					for (var i = 0; i < categories.length; i++) {
						var category = categories[i];
						var newNode = ({
							id : category.instance,
							text : category.name,
							leaf : false,
							url : ''
						});
						newNode.children = new Array();
						if (category.columns != null
								&& category.columns.length > 0) {
							for (var j = 0; j < category.columns.length; j++) {
								newNode.children[j] = {
									text : category.columns[j].name,
									leaf : true
								};
							}
						}
						root.appendChild(newNode);
					}
				}
				
				this.tbar = [{
					text : 'ALL',
					handler : function(button) {
						
					}
				},{
					text : 'SUM',
					handler : function(button) {
						
					}
				}]
				
				this.callParent(arguments);
			}
		});