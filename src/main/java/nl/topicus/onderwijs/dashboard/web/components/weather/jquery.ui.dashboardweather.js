(function( $, undefined ) {

$.widget( "ui.dashboardWeather", {

	options: {
		dataUrl: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-weather ui-widget" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-weather ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "dataUrl" ) {
			this.options.dataUrl = value;
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
		if (data) {
			var icon = this.element.find(".weatherIcon");
			icon.attr("class", "weatherIcon "+data.type.toLowerCase()+" "+(data.day ? "day" : "night"));
			
			this.element.find(".wind .icon").css("-webkit-transform", "rotate("+data.windDirection+"deg)");
			this.element.find(".wind .speed").text(data.windSpeed+" km/h");
			this.element.find(".temperature .min").text(data.minTemperature+"°C");
			this.element.find(".temperature .max").text(data.maxTemperature+"°C");
			this.element.find(".sun .rise").text(data.sunriseTime);
			this.element.find(".sun .set").text(data.sunsetTime);
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
