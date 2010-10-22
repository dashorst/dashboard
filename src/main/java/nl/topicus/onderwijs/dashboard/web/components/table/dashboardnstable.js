/*
 * jQuery UI Progressbar 1.8.4
 *
 * Copyright 2010, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Progressbar
 *
 * Depends:
 *   jquery.timers.js
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 */
(function( $, undefined ) {

$.conversions.train = function(o) {
	var tr = $("<tr />");
	var type = $("<td class='type'><div class='"+o.type+"'></div></td>");
	var time = $("<td class='departureTime'>"+o.departureTime+"</td>");
	var delay = o.delay > 0 ? $("<td class='delay'>+ "+o.delay+"</td>") : $("<td class='delay' />");
	var platform = $("<td class='platform'>"+o.platform+"</td>");
	var destination = $("<td class='destination'>"+o.destination+"</td>");
	tr.append(type).append(time).append(delay).append(platform).append(destination);
	return tr;
};

})( jQuery );
