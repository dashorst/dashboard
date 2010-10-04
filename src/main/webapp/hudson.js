function getBuildsForProjects() {
    $("#builds").empty().append("<h3>Builds</h3>");
    getBuildsForProject("DUO");
    getBuildsForProject("EduArte%20v2.03%20-%20trunk%20(ontwikkel)");
    getBuildsForProject("Vocus%20-%20trunk%20(ontwikkel)");
    getBuildsForProject("PassePartout");
}

function getBuildsForProject(name) {
    var cleanedName = name.replace(/\(|\)|\.|\-|\%/g, '');
    var id = 'hudson_' + cleanedName;
    var markup = $("<div id='" + id + "' class='row'>&nbsp;</div>");
    $("#builds").append(markup);
    var projectUrl = "http://builds.topicus.local/job/" + name + "/api/json";

    $.ajax({
        url: projectUrl,
        dataType: 'jsonp',
        jsonp: 'jsonp',
        success: function(data) {
            for (var i = 0; i < 5; i++) {
                var url = data.builds[i].url;
                addBuildData(id, url);
            }
        }
    });
}

function addBuildData(id, url) {
    var buildUrl = url + "/api/json";

    $.ajax({
        url: buildUrl,
        dataType: 'jsonp',
        jsonp: 'jsonp',
        success: function(data) {
            var markup;
            if (data.building)
            	markup = $("<span title='Build #" + data.number + "' class='building'></span>");
            else
            	markup = $("<span title='Build #" + data.number + "' class='" + data.result.toLowerCase() + "'></span>");

            markup.appendTo('#' + id)
        }
    });
}
