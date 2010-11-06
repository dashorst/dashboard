(function( $, undefined ) {

$.conversions.commit = function(o) {
	var tr = $("<tr />");
	var rev = $("<td class='revision'>"+o.revision+"</td>");
	var project = $("<td class='project'>"+o.projectName+"</td>");
	var message = $("<td class='message'>"+o.message+"</td>");
	tr.append(rev).append(project).append(message);
	return tr;
};

})( jQuery );
