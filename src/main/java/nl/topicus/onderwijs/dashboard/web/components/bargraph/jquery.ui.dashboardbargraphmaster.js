(function( $, undefined ) {

$.widget( "ui.dashboardBarGraphMaster", {

	options: {
		dataSets: [],
		secondsBetweenSwitch: 30
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-bar-graph ui-widget" );

		this._initHeartBeat();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-bar-graph ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "dataSets" ) {
			this.options.dataSets = value;
		}
		else if ( key === "secondsBetweenSwitch" ) {
			this.options.secondsBetweenSwitch = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initHeartBeat: function() {
		var self = this;
		var heartBeatDataSet = function() {
			return self._heartBeatDataSet.apply( self, arguments );
		};
		$(document)
			.data("dashboard-bar-graph-data-set-index", 0)
			.data("dashboard-bar-graph-data-set", this.options.dataSets[0].key)
			.bind("dashboard-heartbeat",
					function(event, count) {
						if (count % self.options.secondsBetweenSwitch == 0)
							heartBeatDataSet();
						if (count % 5 == 0)
							$(document).triggerHandler("dashboard-bar-graph-heartbeat-update");
					});
		this.element.find("h1").text(this.options.dataSets[0].label);
		this.element.addClass(this.options.dataSets[0].scheme);
		this.element.find(".bargraph").addClass("bars-"+this.element.find(".bar-box").length);
	},

	_heartBeatDataSet: function() {
		var self = this;
		$.each(this.options.dataSets, function(key, value) {
			self.element.removeClass(value.scheme);
		});
		var newDataSetIndex = $(document).data("dashboard-bar-graph-data-set-index") + 1;
		if (newDataSetIndex >= this.options.dataSets.length)
			newDataSetIndex = 0;
		$(document).data("dashboard-bar-graph-data-set-index", newDataSetIndex);
		$(document).data("dashboard-bar-graph-data-set", this.options.dataSets[newDataSetIndex].key);
		$(document).triggerHandler("dashboard-bar-graph-change-data-set");
		this.element.find("h1").text(this.options.dataSets[newDataSetIndex].label);
		this.element.addClass(this.options.dataSets[newDataSetIndex].scheme);
	}
});

$.extend( $.ui.dashboardmaster, {
	version: "1.8.4"
});

})( jQuery );
