Ext.define('MO.view.ComboBox', {
			extend : 'Ext.form.ComboBox',
			alias : 'widget.comboBox',
			fieldLabel : '刷新时间(s)',
			queryMode: 'local',
			store : 'ComboStore',
			displayField : 'name',
			valueField : 'abbr',
			disabled:true
		});