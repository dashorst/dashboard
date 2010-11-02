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
		var scrollMinors = function() {
			return self._scrollMinors.apply( self, arguments );
		};
		$.getJSON(this.options.dataUrl, response);
		this.minorIndex = 0;
		this.scrollDirection = 1;
		$(document).bind("dashboard-heartbeat", function(event, count) {
			if (count % 30 == 0)
				$.getJSON(self.options.dataUrl, response);
			if (count % 5 == 0)
				scrollMinors();
		});
	},

	_redraw: function( data ) {
		if (data) {
			this.minorCount = data.minor.length;
			var major = data.major;
			if (data.major) {
				this.element.find(".majorEvent").text(major.keyName + ": " + major.dateAsString + " - " + major.title);
			} else {
				this.element.find(".majorEvent").text("No upcoming major event");
			}
			var list = this.element.find(".minorEvents ul");
			list.empty();
			$.each(data.minor, function(index, minor) {
				list.append("<li>" + minor.keyName + ": " + minor.dateAsString + " - " + minor.title + "</li>")
			});
		}
	},
	
	_scrollMinors: function() {
		if (this.minorCount < 2) {
			this.minorIndex = 0;
		} else {
			if (this.scrollDirection == 1 && this.minorIndex >= this.minorCount-1) {
				this.minorIndex = this.minorCount-1;
				this.scrollDirection = -1;
			} else if (this.scrollDirection == -1 && this.minorIndex <= 0) {
				this.minorIndex = 0;
				this.scrollDirection = 1;
			}
			this.minorIndex += this.scrollDirection;
		}
		var list = this.element.find(".minorEvents ul");
		list.css("margin-top", (this.minorIndex*-1.5 - 0.2)+"em");
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
