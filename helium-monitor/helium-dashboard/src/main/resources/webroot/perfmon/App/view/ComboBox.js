Ext.define('MO.view.ComboBox', {
			extend : 'Ext.form.ComboBox',
			alias : 'widget.comboBox',
			fieldLabel : 'Refresh Time',
			queryMode: 'local',
			store : 'ComboStore',
			displayField : 'name',
			valueField : 'abbr',
			disabled:true
		});