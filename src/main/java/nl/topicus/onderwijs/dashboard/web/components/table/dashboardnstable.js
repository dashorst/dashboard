(function( $, undefined ) {

$.conversions.train = function(o) {
	var tr = $("<tr />");
	var type = $("<td class='type'><div class='"+o.type.toLowerCase()+"'></div></td>");
	var time = $("<td class='departureTime'>"+o.departureTime+"</td>");
	var delay = o.delay > 0 ? $("<td class='delay'>+ "+o.delay+"</td>") : $("<td class='delay' />");
	var platform = $("<td class='platform'>"+o.platform+"</td>");
	var destination = $("<td class='destination'>"+o.destination+"</td>");
	tr.append(type).append(time).append(delay).append(platform).append(destination);
	return tr;
};

})( jQuery );
