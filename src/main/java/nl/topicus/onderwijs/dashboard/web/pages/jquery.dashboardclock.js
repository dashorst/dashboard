(function($) {
	jQuery.fn.dashboardClock = function(starttimeUrl) {
		var starttime = '';
		$(document)
			.data("dashboard-heartbeat-enabled", true)
			.data("dashboard-heartbeat-count", 0)
			.everyTime(
				"1s",
				"heartbeat-bar-graph",
				function() {
					var count = $(document).data("dashboard-heartbeat-count") + 1;
					if (count % 5 == 0) {
						$.getJSON(starttimeUrl, function(data){
							console.log(starttime + " -> "+data);
							if (starttime == '')
								starttime = data;
							if (data && starttime != data) {
								location.reload(true);
							}
						});
					}
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