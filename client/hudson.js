$(function () {
	getBuildsForProject("DUO");
	getBuildsForProject("EduArte%20v2.03%20-%20trunk%20(ontwikkel)");
	getBuildsForProject("Vocus%20-%20trunk%20(ontwikkel)");
	getBuildsForProject("PassePartout");
});

function getBuildsForProject(name) {
	var cleanedName = name.replace(/\(|\)|\.|\-|\%/g, '');
	var id = 'hudson_' + cleanedName;
	var markup = $("<div id='" + id + "' class='row'>&nbsp;</div>");
	markup.appendTo("#builds");
	$.getJSON("http://builds.topicus.local/job/" + name + "/api/json", 
		function ( data ) {
			for(var i = 0; i < 5; i++) {
				var url = data.builds[i].url;
				addBuildData(id, url);
			}
		});
}

function addBuildData(id, url) {
	$.getJSON(url + "/api/json", function(data) {
		if(data.building) 
		{
			$("<span class='building'></span>").appendTo('#' + id);
		}
		else 
		{
			$("<span class='" + data.result.toLowerCase() + "'></span>").appendTo('#' + id);
		}
	});
}
