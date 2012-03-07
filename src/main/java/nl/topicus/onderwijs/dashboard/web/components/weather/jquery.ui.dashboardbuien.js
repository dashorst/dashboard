(function( $, undefined ) {

$.widget( "ui.dashboardbuien", {

	options: {
		dataUrl: ""
	},
	
	_create: function() {
		this._initialRead();
	},

	destroy: function() {
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
			var rfSize = data.rainForecast.length;
			var buiengradient = "-webkit-linear-gradient(left , ";
			for(i=0;i<(rfSize-1);i++){
				buiengradient = buiengradient + "rgba(0, 164, 205, 0."+data.rainForecast[i]+") "+Math.round(((i+1)*(100/rfSize)))+"%, ";
			}
			buiengradient = buiengradient + "rgba(0, 164, 205, 0."+data.rainForecast[rfSize-1]+") 99%)";
			this.element.find(".buien").css("background-image", buiengradient);
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
