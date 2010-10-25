/*
 * jQuery UI Progressbar 1.8.4
 *
 * Copyright 2010, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Progressbar
 *
 * Depends:
 *   jquery.timers.js
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 */
(function( $, undefined ) {

if (!$.conversions) {
	$.conversions = {};
}

$.widget( "ui.dashboardTable", {

	options: {
		dataUrl: "",
		conversion: "",
		htmlClass: "",
		label: "",
		keyProperty: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-table ui-widget" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-table ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "conversion" ) {
			this.options.conversion = value;
		}
		else if ( key === "htmlClass" ) {
			this.options.htmlClass = value;
		}
		else if ( key === "dataUrl" ) {
			this.options.dataUrl = value;
		}
		else if ( key === "label" ) {
			this.options.label = value;
		}
		else if ( key === "keyProperty" ) {
			this.options.keyProperty = keyProperty;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initialRead: function() {
		this.element.empty();
		this.element.parent().addClass(this.options.htmlClass);
		this.element.append("<thead><tr><th colspan='10'>"+this.options.label+"</th></tr></thead>");
		this.element.append("<tbody />");
		var self = this;
		var response = function() {
			return self._redraw.apply( self, arguments );
		};
		$.getJSON(this.options.dataUrl, response);
		$(document).bind("dashboard-heartbeat", function(event, count) {
			if (count % 5 == 0)
				$.getJSON(self.options.dataUrl, response);
		});
	},

	_redraw: function( data ) {
		var self = this;
		if (data) {
			var tbody = this.element.find("tbody");
			var rows = {};
			var ids = new Array();
			var idCount = 0;
			tbody.find("tr").each(function(index, element) {
				var row = $(element);
				ids[idCount] = row.data("id");
				row.addClass("old");
				rows[row.data("id")] = row;
				idCount++;
			});
			this.jsonData = data;
			$.each(data, function(index, value) {
				var conversionFunction = $.conversions[self.options.conversion];
				var row = conversionFunction(value);
				var rowId = value[self.options.keyProperty];
				row.data("id", rowId);
				if (!rows[rowId]) {
					ids[idCount] = rowId;
					idCount++;
				}
				rows[rowId] = row;
			});
			ids.sort();
			tbody.empty();
			$.each(ids, function(index, id){
				tbody.append(rows[id]);
			});
			tbody.oneTime("4s", function() {
				tbody.find(".old").remove();
			});
		}
	},
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
