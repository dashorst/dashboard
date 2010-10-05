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

$.widget( "ui.dashboardTable", {

	options: {
		dataUrl: "",
		identifier: ""
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-table ui-widget" );

		console.log("create");
		this.options.identifier = this.element.attr("id");
		this._update();
		this._initHandlers();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-table ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		console.log(key +"="+value);
		if ( key === "identifier" ) {
			this.options.identifier = value;
		}
		else if ( key === "dataUrl" ) {
			this.options.dataUrl = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_update: function() {
		var self = this;
		var response = function() {
			return self._redraw.apply( self, arguments );
		};
		$.getJSON(this.options.dataUrl, response);
	},

	_initHandlers: function() {
		var self = this;
		var onHeartBeat = function() {
			return self._onHeartBeat.apply( self, arguments );
		};
		$(document).bind("dashboard-table-heartbeat-rotate", function() {
			var index = self.element.prevAll().length;
			$(document).oneTime(index * 200, onHeartBeat);
		});

		var onInsertProject = function() {
			return self._onInsertProject.apply( self, arguments );
		};
		$(document).bind("dashboard-table-insert-project", onInsertProject);
	},

	_redraw: function( data ) {
		console.log(data);
		var self = this;
		this.element.empty();
		var flips = $("<div class='flips' />");
		this.element.append(flips);

		if (data) {
			this.jsonData = data;
			this.nextFlip = 2;
			$.each(data, function(flipIndex, value) {
				var flip = $("<div class='flip flip-"+flipIndex+"' />");
				if (flipIndex == 0)
					flip.addClass("flip-front");
				else if (flipIndex == 1)
					flip.addClass("flip-back");
				else if (flipIndex > 1)
					flip.addClass("flip-hidden");
				flips.append(flip);
				flip.append("<h3>"+value.label+"</h3>");
				var dataDiv = $("<div class='data' />").appendTo(flip);
				var projects = $(document).data("dashboard-table-projects").slice();
				projects.reverse();
				$.each(projects, function(index, project) {
					self._insertRow.apply(self, [dataDiv, flipIndex, project]);
				});
			});
		}
	},

	_insertRow: function(dataDiv, flipIndex, project, extraClass) {
		var rowValue = this.jsonData[flipIndex].data[project];
		var rowDiv = $("<div class='row'><div class='inner-row'>"+rowValue+"</div></div>");
		if (extraClass)
			rowDiv.addClass(extraClass);
		dataDiv.prepend(rowDiv);
	},

	_onHeartBeat: function() {
		if (this.jsonData) {
			if (this.nextFlip >= this.jsonData.length) {
				this.nextFlip = 0;
			}
			var doNextFlip = this.nextFlip;
			this.nextFlip++;
			var self = this;
			self.element.addClass("rotate-enabled");
			if (this.element.hasClass("rotated")) {
				this.element.removeClass("rotated");
				$(this.element).oneTime("1100ms", function() {
					self.element.removeClass("rotate-enabled rotate-invert");
					self.element.find(".flip-back").removeClass("flip-back").addClass("flip-hidden");
					self.element.find(".flip-"+doNextFlip).removeClass("flip-hidden").addClass("flip-back");
				});
			} else {
				this.element.addClass("rotated");
				$(this.element).oneTime("1100ms", function() {
					self.element.removeClass("rotate-enabled");
					self.element.addClass("rotate-invert");
					self.element.find(".flip-front").removeClass("flip-front").addClass("flip-hidden");
					self.element.find(".flip-"+doNextFlip).removeClass("flip-hidden").addClass("flip-front");
				});
			}
		}
	},

	_onInsertProject: function(target, project) {
		var self = this;
		if (this.jsonData) {
			$.each(this.jsonData, function(flipIndex, value) {
				var dataDiv = self.element.find(".flip-"+flipIndex+" .data");
				self._insertRow.apply(self, [dataDiv, flipIndex, project]);
			});
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
