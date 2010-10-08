/*
 * Depends:
 *   jquery.timers.js
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 */
(function( $, undefined ) {

$.widget( "ui.dashboardBarGraph", {

	options: {
		dataUrl: "",
		projectName: ""
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-bar-graph ui-widget" );

		this._initBarGraph();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-bar-graph ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "dataUrl" ) {
			this.options.dataUrl = value;
		}
		else if ( key === "projectName" ) {
			this.options.projectName = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initBarGraph: function() {
		var self = this;
		var response = function() {
			return self._redraw.apply( self, arguments );
		};
		$.getJSON(this.options.dataUrl, response);
		$(document).bind("dashboard-bar-graph-heartbeat-update", function() {
			$.getJSON(self.options.dataUrl, response);
		});
	},

	_redraw: function( allData ) {
		var self = this;
		this.element.find("h3").text(this.options.projectName);
		if (allData) {
			var dataSetKey = $(document).data("dashboard-bar-graph-data-set");
			var data = allData[dataSetKey];
			this.element.find(".bar").css("height", data.height+"em");
			var valueElement = this.element.find(".value");
			var oldTextValue = valueElement.text();
			valueElement.css("bottom", Math.max(1.0, data.height)+"em");
			if (isNaN(parseInt(data.value)) || isNaN(parseInt(oldTextValue))) {
				valueElement.text(data.value);
			} else {
				var count = 0;
				this.element.everyTime("60ms", "bar-redraw-value", function() {
					count++;
					var oldValue = parseInt(oldTextValue);
					var newValue = parseInt(data.value);
					var curValue = Math.round(oldValue+(newValue - oldValue)/30*count);
					valueElement.text(curValue);
				}, 30);
			}
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
