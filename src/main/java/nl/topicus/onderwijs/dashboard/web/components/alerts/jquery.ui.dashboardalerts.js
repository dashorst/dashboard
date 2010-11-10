(function( $, undefined ) {

if (!$.conversions) {
	$.conversions = {};
}

$.widget( "ui.dashboardAlerts", {

	options: {
		dataUrl: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-alerts ui-widget" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-alerts ui-widget" );

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
			var ul = this.element.find("ul");
			ul.empty();
			$.each(data, function(index, value) {
				visible |= value.overlayVisible;
				var li = $("<li />");
				var color = $("<span class='dot "+value.color.toLowerCase()+"'></span>");
				var time = $("<span class='time'>"+value.time+"</span>");
				var project = $("<span class='project'>"+value.projectName+"</span>");
				var message = $("<span class='message'>"+value.message+"</span>");
				li.append(color).append(time).append(project).append(message);
				ul.append(li);
			});
			if (visible && !this.visible) {
				this.element.find(".alerts-overlay").addClass("showX");
				this.element.oneTime("1s", function() {
					$(this).find(".alerts-overlay").addClass("showY");
				});
			}
			else if (!visible && this.visible) {
				this.element.find(".alerts-overlay").addClass("hide");
				this.element.oneTime("1100ms", function() {
					$(this).find(".alerts-overlay").removeClass("showX showY");
				});
				this.element.oneTime("2200ms", function() {
					$(this).find(".alerts-overlay").removeClass("hide");
				});
			}
			this.visible = visible;
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
