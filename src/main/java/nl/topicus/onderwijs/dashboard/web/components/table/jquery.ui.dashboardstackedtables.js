(function( $, undefined ) {

$.widget( "ui.dashboardStackedTables", {

	options: {
		secondsBetweenSwitch: 30
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-stacked-tables ui-widget" );

		this._initHeartBeat();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-stacked-tables ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "secondsBetweenSwitch" ) {
			this.options.secondsBetweenSwitch = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initHeartBeat: function() {
		this.tableIndex = 0;
		var tables = this.element.find(".table");
		var count = tables.size();
		this.element.find(".table:gt(0)").addClass("hide");
		$(tables.get(count > 1 ? 1 : 0)).addClass("next");
		var self = this;
		var switchTables = function() {
			return self._switchTables.apply( self, arguments );
		};
		$(document)
			.bind("dashboard-heartbeat",
					function(event, count) {
						if (count % self.options.secondsBetweenSwitch == 0)
							switchTables();
					});
	},

	_switchTables: function() {
		var tables = this.element.find(".table");
		var count = tables.size();
		var oldTable = this.tableIndex;
		var newTable = (oldTable+1) % count;
		var nextTable = (oldTable+2) % count;
		$(tables.get(oldTable)).addClass("hide");
		$(tables.get(newTable)).removeClass("hide next");
		$(tables.get(nextTable)).addClass("next");
		this.tableIndex = newTable;
	}
});

$.extend( $.ui.dashboardmaster, {
	version: "1.8.4"
});

})( jQuery );
