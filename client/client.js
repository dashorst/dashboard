
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
//myTweets.refresh();

function eduarteBuilds() {
	var url = 'http://builds/job/EduArte%20v2.03%20-%20trunk%20(ontwikkel)/rssAll?flavor=rss20'
	var script = document.createElement('script');
    script.setAttribute('src', url);
    document.getElementsByTagName('head')[0].appendChild(script);

	// convert string to XML object
	var xmlobject = (new DOMParser()).parseFromString(xmlstring, "text/xml");
	
}