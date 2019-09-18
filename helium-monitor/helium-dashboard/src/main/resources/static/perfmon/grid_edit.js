Ext.view.TableChunker.metaRowTpl = [  
            '<tr class="' + Ext.baseCSSPrefix + 'grid-row {addlSelector} {[this.embedRowCls()]}" {[this.embedRowAttr()]}>',  
            '<tpl for="columns">',  
            '<td class="{cls} ' + Ext.baseCSSPrefix + 'grid-cell ' + Ext.baseCSSPrefix + 'grid-cell-{columnId} {{id}-modified} {{id}-tdCls} {[this.firstOrLastCls(xindex, xcount)]}" {{id}-tdAttr}><div class="' + Ext.baseCSSPrefix + 'grid-cell-inner" style="{{id}-style}; text-align: {align};">{{id}}</div></td>',  
            '</tpl>',  
            '</tr>'  
        ];  
  
Ext.core.Element.prototype.unselectable = function() {  
            var me = this;  
            if (me.dom.className.match(/(x-grid-table|x-grid-view)/)) {  
                return me;  
            }  
            me.dom.unselectable = "on";  
            me.swallowEvent("selectstart", true);  
            me.applyStyles("-moz-user-select:none;-khtml-user-select:none;");  
            me.addCls(Ext.baseCSSPrefix + 'unselectable');  
            return me;  
        };  