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

$.conversions = {};

$.widget( "ui.dashboardTable", {

	options: {
		dataUrl: "",
		conversion: "",
		htmlClass: "",
		label: ""
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

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initialRead: function() {
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
		this.element.empty();
		if (data) {
			this.jsonData = data;
			this.element.append("<thead><tr><th colspan='5'>"+this.options.label+"</th></tr></thead>");
			var tbody = $("<tbody />")
			$.each(data, function(index, value) {
				var conversionFunction = $.conversions[self.options.conversion];
				tbody.append(conversionFunction(value));
			});
			this.element.append(tbody);
		}
	},
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
