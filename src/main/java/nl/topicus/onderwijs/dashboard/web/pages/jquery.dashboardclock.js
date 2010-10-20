(function($) {
	jQuery.fn.dashboardClock = function(starttimeUrl) {
		var starttime = '';
		var doc = $(document);

		doc.data("dashboard-heartbeat-enabled", true);
		doc.data("dashboard-heartbeat-count", 0);
		doc.everyTime("1s", "heartbeat-bar-graph", function() {
			var count = $(document).data("dashboard-heartbeat-count") + 1;
			if (count % 5 == 0) {
				$.getJSON(starttimeUrl, function(data) {
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
		doc.everyTime("200ms", "clock-update", function() {
			$(".clock .time").text(Date().substring(16, 24));
		});
		$("#stoplink").click(function() {
			var newValue = $(document).data("dashboard-heartbeat-enabled");
			$(document).data("dashboard-heartbeat-enabled", !newValue);
		});
	}
})(jQuery);