(function( $, undefined ) {

$.conversions.commit = function(o) {
	var tr = $("<tr /><tr />");
	var rev = $("<td class='revision'>"+o.revision+"</td>");
	var project = $("<td class='project'>"+o.projectName+"</td>");
	var message = $("<td class='message'>"+o.message+"</td>");
	tr.first().append(rev).append(project).append(message);
	
	var details = $("<td class='details' colspan='3' />");
	var time = $("<span class='detail'>at <em>"+o.time+"</em></span>");
	var author = $("<span class='detail'>by <em>"+o.author+"</em></span>");
	var files = $("<span class='detail'><em>"+o.filesChanged+"</em> file"+
			(o.filesChanged == 1 ? "" : "s") + " changed</span>");
	details.append(time).append(author).append(files);
	tr.last().append(details);
	return tr;
};

})( jQuery );
