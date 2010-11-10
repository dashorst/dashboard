(function( $, undefined ) {

if (!$.conversions) {
	$.conversions = {};
}

$.widget( "ui.dashboardTwitter", {

	options: {
		dataUrl: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-twitter ui-widget" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-twitter ui-widget" );

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
			var visible = false;
			var timeline = this.element.find("ul.timeline");
			timeline.empty();
			$.each(data.timeline, function(index, value) {
				var li = $("<li />");
				var text = $("<span class='text'>"+value.text+"</span>");
				li.append(text);
				timeline.append(li);
			});
			var mentions = this.element.find("ul.mentions");
			mentions.empty();
			$.each(data.mentions, function(index, value) {
				var li = $("<li />");
				var text = $("<span class='text'>"+value.text+"</span>");
				li.append(text);
				mentions.append(li);
			});
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
