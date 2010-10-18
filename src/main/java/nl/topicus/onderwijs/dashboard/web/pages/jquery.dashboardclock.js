(function($) {
	jQuery.fn.dashboardClock = function() {
		$(document)
			.data("dashboard-heartbeat-enabled", true)
			.data("dashboard-heartbeat-count", 0)
			.everyTime(
				"1s",
				"heartbeat-bar-graph",
				function() {
					var count = $(document).data("dashboard-heartbeat-count") + 1;
					$(document).data("dashboard-heartbeat-count", count);
					if ($(document).data("dashboard-heartbeat-enabled"))
						$(document).triggerHandler("dashboard-heartbeat", count);
				});
		$("#stoplink").click(function(){
			var newValue = $(document).data("dashboard-heartbeat-enabled");
			$(document).data("dashboard-heartbeat-enabled", !newValue);
		});
	}
})(jQuery);