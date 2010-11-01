(function( $, undefined ) {

if (!$.conversions) {
	$.conversions = {};
}

$.widget( "ui.dashboardEvents", {

	options: {
		dataUrl: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-events ui-widget" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-events ui-widget" );

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
			if (count % 30 == 0)
				$.getJSON(self.options.dataUrl, response);
		});
	},

	_redraw: function( data ) {
		var self = this;
		if (data) {
			console.log(data);
			var major = data.major;
			this.element.find(".majorEvent").text(major.key.name + " " + major.dateAsString + " " + major.title);
			var list = this.element.find(".minorEvents ul");
			list.empty();
			$.each(data.minor, function(index, minor) {
				list.append("<li>" + minor.key.name + " " + minor.dateAsString + " " + minor.title + "</li>")
			});
		}
	},
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
