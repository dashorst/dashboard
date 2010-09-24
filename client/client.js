
var myTweets = {
  'delayedRefresh' : function() {
    setTimeout(myTweets.refresh, 10 * 60 * 1000); // refresh twitters every 10 minutes
  },
  'refresh' : function() {
	getTwitters('twitter', { 
	  id: 'machielkarels', 
	  count: 10, 
	  enableLinks: true, 
	  ignoreReplies: true, 
	  clearContents: true,
	  template: '%time% twitterde <a href="http://twitter.com/%user_screen_name%">@%user_screen_name%</a>: %text%',
      callback: myTweets.delayedRefresh
	});
  }
};

var myBuilds = {
	'delayedRefresh' : function() {
		setTimeout(myBuilds.refresh, 5*60*1000);
	},
	'refresh' : function() {
		getBuildsForProjects();
		myBuilds.delayedRefresh();
	}
}

$(function () {
	$("#projects").dashboardmaster({
		projects: {
			"duo":"DUO",
		 	"eduarte":"EduArte",
			"atvo":"@VO",
			"passepartout":"PassePartout",
			"test":"Test"
		}});
	$("div.info.col").dashboard();
	$("#stoplink").click(function() {
		$(document).data("dashboard-heartbeat-enabled",
			!$(document).data("dashboard-heartbeat-enabled"));
		});
	// myTweets.refresh();
	// myBuilds.refresh();
});
