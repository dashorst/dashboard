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
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 */
(function( $, undefined ) {

$.widget( "ui.dashboard", {

	options: {
		identifier: ""
	},

	_create: function() {
		var self = this;
		this.element
			.addClass( "ui-dashboard ui-widget" );

		this.options.identifier = this.element.attr("id");
		this.response = function() {
			return self._redraw.apply( self, arguments );
		};
		this._update();
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
		$.getJSON("data-"+this.options.identifier+".json", this.response);
	},

	_redraw: function( data ) {
		var self = this;
		console.log(data);
		this.element
			.empty()
			.append("<h3>"+data.label+"</h3>");
		if (data.data) {
			$.each(data.data, function(index, value) {
				self.element.append("<div class='row'>"+value+"</div>");
			});
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
