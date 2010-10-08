(function( $, undefined ) {

$.widget( "ui.dashboardBarGraphMaster", {

	options: {
		dataSets: []
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-table ui-widget" );

		this._initHeartBeat();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-table ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "dataSets" ) {
			this.options.dataSets = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initHeartBeat: function() {
		var self = this;
		var heartBeatDataSet = function() {
			return self._heartBeatDataSet.apply( self, arguments );
		};
		$(document)
			.data("dashboard-bar-graph-heartbeat-enabled", true)
			.data("dashboard-bar-graph-data-set-index", 0)
			.data("dashboard-bar-graph-data-set", this.options.dataSets[0].key)
			.everyTime("30s", "heartbeat-bar-graph-data-set",
					function() {
						$(document).oneTime("2s", heartBeatDataSet);
					});
		this.element.find("h1").text(this.options.dataSets[0].label);
		this.element.addClass(this.options.dataSets[0].scheme);
	},

	_heartBeatDataSet: function() {
		if (!$(document).data("dashboard-bar-graph-heartbeat-enabled"))
			return;

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
