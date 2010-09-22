/*
 * jQuery UI Progressbar 1.8.4
 *
 * Copyright 2010, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Progressbar
 *
 * Depends:
 *   jquery.timers.js
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 */
(function( $, undefined ) {

$.widget( "ui.dashboard", {

	options: {
		identifier: ""
	},

	_create: function() {
		this.element
			.addClass( "ui-dashboard ui-widget" );

		this.options.identifier = this.element.attr("id");
		this._update();
		this._initHeartBeat();
	},

	destroy: function() {
		this.element
			.removeClass( "ui-dashboard ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "identifier" ) {
			this.options.identifier = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_update: function() {
		var self = this;
		this.response = function() {
			return self._redraw.apply( self, arguments );
		};
		$.getJSON("data-"+this.options.identifier+".json", this.response);
	},

	_redraw: function( data ) {
		console.log(data);
		this.element.empty();
		var flips = $("<div class='flips' />");
		this.element.append(flips);

		if (data.sets) {
			$.each(data.sets, function(index, value) {
				var flip = $("<div class='flip flip-"+index+"' />");
				flips.append(flip);
				flip.append("<h3>"+value.label+"</h3>");
				$.each(value.data, function(index2, value2) {
					flip.append("<div class='row'>"+value2+"</div>");
				});
			});
		}
	},

	_initHeartBeat: function() {
		var self = this;
		var onHeartBeat = function() {
			return self._onHeartBeat.apply( self, arguments );
		};
		if (!$(document).data("dashboard-heartbeat")) {
			$(document)
				.data("dashboard-heartbeat", true)
				.everyTime("5s", "heartbeat", this._heartBeat);
		}
		$(document).bind("dashboard-heartbeat", function() {
				var index = self.element.prevAll().length;
				$(document).oneTime(index * 200, onHeartBeat);
		});
	},

	_heartBeat: function() {
		$(document).triggerHandler("dashboard-heartbeat");
	},

	_onHeartBeat: function() {
		this.element.toggleClass("rotated");
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
