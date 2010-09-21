function getBuildsForProjects() {
	getBuildsForProject("DUO");
	getBuildsForProject("EduArte%20v2.03%20-%20trunk%20(ontwikkel)");
	getBuildsForProject("Vocus%20-%20trunk%20(ontwikkel)");
	getBuildsForProject("PassePartout");
}

function getBuildsForProject(name) {
	var cleanedName = name.replace(/\(|\)|\.|\-|\%/g, '');
	var id = 'hudson_' + cleanedName;
	var markup = $("<div id='" + id + "' class='row'>&nbsp;</div>");
	$("#builds").empty().append(markup);
	$.getJSON("http://builds/job/" + name + "/api/json", 
		function ( data ) {
			for(var i = 0; i < 5; i++) {
				var url = data.builds[i].url;
				addBuildData(id, url);
			}
		});
}

function addBuildData(id, url) {
	$.getJSON(url + "/api/json", function(data) {
		var markup;
		if(data.building) 
			markup = $("<span class='building'></span>");
		else 
			markup = $("<span class='" + data.result.toLowerCase() + "'></span>");

		markup.appendTo('#' + id)
	});
}
