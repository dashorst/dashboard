(function( $, undefined ) {

$.widget( "ui.dashboardTableMaster", {

	options: {
		projects: {},
		projectKeys: [],
		maxProjects: 5,
		secondsBetweenScroll: 15
	},

	_create: function() {
		this.element.addClass( "ui-dashboard-table ui-widget" );

		this._initDraw();
		this._initHeartBeat();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-table ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "projects" ) {
			this.options.projects = value;
		}
		else if ( key === "secondsBetweenScroll" ) {
			this.options.secondsBetweenScroll = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initDraw: function() {
		var self = this;
		this.element.empty();
		$("<h3/>").append("&nbsp;").appendTo(this.element);
		var data = $("<div class='data' />").appendTo(this.element);
		data.append("<div class='prev button row'><div class='inner-row'>&#9650;</div></div>");
		data.append("<div class='next button row'><div class='inner-row'>&#9660;</div></div>");
		var startProjects = new Array();
		this.options.projectKeys = new Array();
		var index = 0;
		$.each(this.options.projects, function(key, value) {
			self.options.projectKeys[index] = key;
			if (index < self.options.maxProjects) {
				data.append("<div class='row'><div class='inner-row'>"+value+"</div></div>");
				startProjects[index] = key;
			}
			index++;
		});
		$(document).data("dashboard-table-projects", startProjects);
	},

	_initHeartBeat: function() {
		var self = this;
		var heartBeatProjects = function() {
			return self._heartBeatProjects.apply( self, arguments );
		};
		$(document)
			.data("dashboard-table-project-index", this.options.projectKeys.length-1)
			.bind("dashboard-heartbeat",
					function() {
						var count = $(document).data("dashboard-heartbeat-count");
						if (count % self.options.secondsBetweenScroll == 0) {
							$(document).oneTime("2s", heartBeatProjects);
						}
					});
	},

	_heartBeatProjects: function() {
		var newProjectIndex = $(document).data("dashboard-table-project-index");
		var newProjects = $(document).data("dashboard-table-projects");
		for (var count=this.options.maxProjects-1; count>0; count--) {
			newProjects[count] = newProjects[count-1];
		}	
		newProjects[0] = this.options.projectKeys[newProjectIndex];
		newProjectIndex--;
		if (newProjectIndex < 0)
			newProjectIndex = this.options.projectKeys.length-1;	
		$(document).data("dashboard-table-projects", newProjects);
		$(document).data("dashboard-table-project-index", newProjectIndex);
		$(document).triggerHandler("dashboard-table-insert-project", newProjects[0]);

		$("body").addClass("project-transition");
		var dataDiv = this.element.find(".data");
		dataDiv.find(".button").prependTo(dataDiv);
		dataDiv.prepend("<div class='row'><div class='inner-row'>"+
				this.options.projects[newProjects[0]]+"</div></div>");
		$(document).triggerHandler("dashboard-table-heartbeat-projects");
		// a small delay is need, because elements need to be rendered before
		// animations can start
		$(document).oneTime("500ms", function() {
			$("body").addClass("animate");
		});
		$(document).oneTime("2000ms", function() {
			$("body").removeClass("project-transition animate");
			$(".ui-dashboard-table .data .row:last-child").remove();
		});
	}
});

$.extend( $.ui.dashboardmaster, {
	version: "1.8.4"
});

})( jQuery );
