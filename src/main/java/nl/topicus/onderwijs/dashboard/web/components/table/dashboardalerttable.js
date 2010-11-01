(function( $, undefined ) {

$.conversions.alert = function(o) {
	var tr = $("<tr />");
	var color = $("<td class='color'><span class='dot "+o.color.toLowerCase()+"'></span></td>");
	var time = $("<td class='time'>"+o.time+"</td>");
	var project = $("<td class='project'>"+o.projectName+"</td>");
	var message = $("<td class='message'>"+o.message+"</td>");
	tr.append(color).append(time).append(project).append(message);
	return tr;
};

})( jQuery );
