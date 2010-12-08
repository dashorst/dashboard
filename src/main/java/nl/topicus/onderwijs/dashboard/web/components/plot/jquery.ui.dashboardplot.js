(function( $, undefined ) {

$.widget( "ui.dashboardPlot", {

	options: {
		callback: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-plot ui-widget" );

		this._initPlot();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-plot ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "callback" ) {
			this.options.callback = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initPlot: function() {
		var self = this;
		$(document).bind("dashboard-heartbeat", function(event, count) {
			if (count % 5 == 0)
				self.options.callback();
		});
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
