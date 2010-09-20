$(function () {
	// var project = getBuildsForProject("DUO");
});

function getBuildsForProject(name) {
	$.getJSON("http://builds.topicus.local/job/" + name + "/api/json", 
		function ( data ) {
			for(var i = 0; i < 5; i++) {
				var url = data.builds[0].url;
				getBuildData(url);
			}
		});
}

function getBuildData(url) {
	$.getJSON(url + "/api/json", function(data) {
		alert(data.result);
	});
}
