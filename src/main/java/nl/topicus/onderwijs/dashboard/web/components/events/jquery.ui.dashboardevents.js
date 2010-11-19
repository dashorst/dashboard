(function( $, undefined ) {

if (!$.conversions) {
	$.conversions = {};
}

$.widget( "ui.dashboardEvents", {

	options: {
		dataUrl: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-events ui-widget events" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-events ui-widget events" );

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
			var major1Div = this.element.find(".major .event1").empty();
			var major2Div = this.element.find(".major .event2").empty();
			if (data.major1) {
				var major1 = data.major1;
				var project1 = $("<span />");
				project1.text(major1.keyName);
				project1.addClass("project");
				project1.css("color", major1.color);
				major1Div.append(project1);
				major1Div.append("<span class='days'>"+major1.daysUntilAsString+"</span>");
				major1Div.append("<span class='title'>"+major1.title+"</span>");
				if (data.major2) {
					var major2 = data.major2;
					var project2 = $("<span />");
					project2.text(major2.keyName);
					project2.addClass("project");
					project2.css("color", major2.color);
					major2Div.append(project2);
					major2Div.append("<span class='days'>"+major2.daysUntilAsString+"</span>");
					major2Div.append("<span class='title'>"+major2.title+"</span>");
					major2Div.show();
				} else {
					major2Div.hide();
				}
			} else {
				this.element.find(".major .event1").text("No upcoming major event");
			}
			var list = this.element.find(".minorEvents ul");
			list.empty();
			$.each(data.minor, function(index, minor) {
				var li = $("<li />");
				li.append("<span class='project' style='color:"+minor.color+";'>"+minor.keyName+"</span>");
				li.append("<span class='days'>"+minor.daysUntilAsString+"</span>");
				li.append("<span class='title'>"+minor.title+"</span>");
				li.append("<span class='date'>"+minor.dateAsString+"</span>");
				list.append(li);
			});
		}
	},
	
	_scrollMinors: function() {
		if (this.minorCount < 4) {
			this.minorIndex = 0;
		} else {
			if (this.minorIndex >= this.minorCount-3) {
				this.minorIndex = 0;
				this.skipScroll = 1;
			} else {
				if (this.skipScroll == 0)
					this.minorIndex ++;
				else
					this.skipScroll = 0;
			}
		}
		var list = this.element.find(".minorEvents ul");
		list.css("margin-top", (this.minorIndex*-35)+"px");
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
