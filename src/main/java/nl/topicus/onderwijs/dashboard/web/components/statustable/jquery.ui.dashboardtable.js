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
		conversion: [],
		secondsBetweenRotate: 5
	},
	
	standardConversions : {
		"identity" : function(value) {
			return value;
		},
		"dots" : function(value) {
			var ret = "";
			$.each(value, function(index, curDot) {
				ret += '<span class="dot '+curDot.toLowerCase()+'"></span>';
			});
			return ret;
		}
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-table ui-widget" );

		this.options.identifier = this.element.attr("id");
		this._initialRead();
		this._initHandlers();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-table ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "conversion" ) {
			this.options.conversion = value;
		}
		else if ( key === "dataUrl" ) {
			this.options.dataUrl = value;
		}
		else if ( key === "secondsBetweenRotate" ) {
			this.options.secondsBetweenRotate = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initialRead: function() {
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
		$(document).bind("dashboard-heartbeat", function() {
			var count = $(document).data("dashboard-heartbeat-count");
			if (count % self.options.secondsBetweenRotate == 0) {
				var index = self.element.prevAll().length;
				$(document).oneTime(index * 200, onHeartBeat);
			}
		});

		var onInsertProject = function() {
			return self._onInsertProject.apply( self, arguments );
		};
		$(document).bind("dashboard-table-insert-project", onInsertProject);
	},

	_redraw: function( data ) {
		var self = this;
		this.element.empty();
		if (data) {
			console.log(data);
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
				self.element.append(flip);
				self._fillFlip(self, flipIndex, flip);
			});
		}
	},
	
	_fillFlip: function(self, flipIndex, flipDiv) {
		flipDiv.append("<h3>"+this.jsonData[flipIndex].label+"</h3>");
		var dataDiv = $("<div class='data' />").appendTo(flipDiv);
		var projects = $(document).data("dashboard-table-projects").slice();
		projects.reverse();
		$.each(projects, function(index, project) {
			self._insertRow.apply(self, [dataDiv, flipIndex, project]);
		});
	},

	_insertRow: function(dataDiv, flipIndex, project, extraClass) {
		var rowValue = this.jsonData[flipIndex].data[project];
		if (rowValue == undefined)
			rowValue = "n/a";
		else
			rowValue = this._convertRowValue(flipIndex, rowValue);
		var rowDiv = $("<div class='row'><div class='inner-row'>"+rowValue+"</div></div>");
		if (extraClass)
			rowDiv.addClass(extraClass);
		dataDiv.prepend(rowDiv);
	},
	
	_convertRowValue: function(flipIndex, rowValue) {
		if (flipIndex >= this.options.conversion.length)
			return this.standardConversions["identity"](rowValue);
		return this.standardConversions[this.options.conversion[flipIndex]](rowValue);
	},

	_onHeartBeat: function() {
		if (this.jsonData) {
			if (this.nextFlip >= this.jsonData.length) {
				this.nextFlip = 0;
			}
			var doNextFlip = this.nextFlip;
			this.nextFlip++;
			var self = this;
			self.element.addClass("rotate-enabled").removeClass("rotate-disabled");
			if (this.element.hasClass("rotated")) {
				this.element.removeClass("rotated");
				$(this.element).oneTime("1100ms", function() {
					self.element.removeClass("rotate-enabled rotate-invert").addClass("rotate-default rotate-disabled");
					self.element.find(".flip-back").removeClass("flip-back").addClass("flip-hidden");
					var flipDiv = self.element.find(".flip-"+doNextFlip);
					flipDiv.empty().removeClass("flip-hidden").addClass("flip-back");
					self._fillFlip(self, doNextFlip, flipDiv);
				});
			} else {
				this.element.addClass("rotated");
				$(this.element).oneTime("1100ms", function() {
					self.element.removeClass("rotate-enabled").addClass("rotate-disabled");
					self.element.removeClass("rotate-default").addClass("rotate-invert");
					self.element.find(".flip-front").removeClass("flip-front").addClass("flip-hidden");
					var flipDiv = self.element.find(".flip-"+doNextFlip);
					flipDiv.empty().removeClass("flip-hidden").addClass("flip-front");
					self._fillFlip(self, doNextFlip, flipDiv);
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
		var response = function() {
			return self._updateJson.apply( self, arguments );
		};
		$.getJSON(this.options.dataUrl, response);
	},
	
	_updateJson: function(data) {
		this.jsonData = data;
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
