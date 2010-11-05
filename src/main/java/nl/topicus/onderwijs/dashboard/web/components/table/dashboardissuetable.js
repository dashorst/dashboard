(function( $, undefined ) {

$.conversions.issue = function(o) {
	var tr = $("<tr />");
	var priority = $("<td class='priority'><div class='"+o.priority.toLowerCase()+"'></div></td>");
	var id = $("<td class='id'>"+o.id+"</td>");
	var project = $("<td class='project'>"+o.projectName+"</td>");
	var summary = $("<td class='summary'>"+o.summary+"</td>");
	tr.append(priority).append(id).append(project).append(summary);
	return tr;
};

})( jQuery );
