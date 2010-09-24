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
		this.element.addClass( "ui-dashboard ui-widget" );

		this.options.identifier = this.element.attr("id");
		this._update();
		this._initHandlers();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard ui-widget" );

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
		var response = function() {
			return self._redraw.apply( self, arguments );
		};
		$.getJSON("data-"+this.options.identifier+".json", response);
	},

	_initHandlers: function() {
		var self = this;
		var onHeartBeat = function() {
			return self._onHeartBeat.apply( self, arguments );
		};
		$(document).bind("dashboard-heartbeat-rotate", function() {
			var index = self.element.prevAll().length;
			$(document).oneTime(index * 200, onHeartBeat);
		});

		var onInsertProject = function() {
			return self._onInsertProject.apply( self, arguments );
		};
		$(document).bind("dashboard-insert-project", onInsertProject);
	},

	_redraw: function( data ) {
		console.log(data);
		var self = this;
		this.element.empty();
		var flips = $("<div class='flips' />");
		this.element.append(flips);

		if (data.sets) {
			this.jsonData = data;
			$.each(data.sets, function(flipIndex, value) {
				var flip = $("<div class='flip flip-"+flipIndex+"' />");
				flips.append(flip);
				flip.append("<h3>"+value.label+"</h3>");
				var dataDiv = $("<div class='data' />").appendTo(flip);
				var projects = $(document).data("dashboard-projects").slice();
				projects.reverse();
				$.each(projects, function(index, project) {
					self._insertRow.apply(self, [dataDiv, flipIndex, project]);
				});
			});
		}
	},

	_insertRow: function(dataDiv, flipIndex, project, extraClass) {
		var rowValue = this.jsonData.sets[flipIndex].data[project];
		var rowDiv = $("<div class='row'><div class='inner-row'>"+rowValue+"</div></div>");
		if (extraClass)
			rowDiv.addClass(extraClass);
		dataDiv.prepend(rowDiv);
	},

	_onHeartBeat: function() {
		var self = this;
		self.element.addClass("rotate-enabled");
		if (this.element.hasClass("rotated")) {
			this.element.removeClass("rotated");
			$(this.element).oneTime("1100ms", function() {
				self.element.removeClass("rotate-enabled rotate-invert");
			});
		} else {
			this.element.addClass("rotated");
			$(this.element).oneTime("1100ms", function() {
				self.element.removeClass("rotate-enabled");
				self.element.addClass("rotate-invert");
			});
		}
	},

	_onInsertProject: function(target, project) {
		var self = this;
		$.each(this.jsonData.sets, function(flipIndex, value) {
			var dataDiv = self.element.find(".flip-"+flipIndex+" .data");
			self._insertRow.apply(self, [dataDiv, flipIndex, project]);
		});
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
