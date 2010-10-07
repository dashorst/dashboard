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
		this.element.everyTime("5s", "update-data", function(){
			$.getJSON(self.options.dataUrl, response);
		});
	},

	_redraw: function( data ) {
		var self = this;
		this.element.find("h3").text(this.options.projectName);
		var initial = !data;
		if (data) {
			var oldData = this.jsonData;
			this.jsonData = data;
			this.element.find(".bar").css("height", data.height+"em");
			var valueElement = this.element.find(".value");
			valueElement.css("bottom", data.height+"em");
			if (!oldData || isNaN(parseInt(data.value)) || isNaN(parseInt(oldData.value))) {
				valueElement.text(data.value);
			} else {
				var count = 0;
				this.element.everyTime("60ms", "bar-redraw-value", function() {
					count++;
					var oldValue = parseInt(oldData.value);
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
