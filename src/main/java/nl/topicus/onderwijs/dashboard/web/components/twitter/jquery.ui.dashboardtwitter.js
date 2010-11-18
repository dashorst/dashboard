(function( $, undefined ) {

if (!$.conversions) {
	$.conversions = {};
}

$.widget( "ui.dashboardTwitter", {

	options: {
		timelineUrl: "",
		mentionsUrl: ""
	},
	
	_create: function() {
		this.element.addClass( "ui-dashboard-twitter ui-widget" );

		this._initialRead();
	},

	destroy: function() {
		this.element.removeClass( "ui-dashboard-twitter ui-widget" );

		$.Widget.prototype.destroy.apply( this, arguments );
	},

	_setOption: function( key, value ) {
		if ( key === "timelineUrl" ) {
			this.options.timelineUrl = value;
		}
		else if ( key === "mentionsUrl" ) {
			this.options.mentionsUrl = value;
		}

		$.Widget.prototype._setOption.apply( this, arguments );
	},

	_initialRead: function() {
		this.timelineGeneration = 0;
		this.mentionsGeneration = 0;
		this._initTwitter(this.options.timelineUrl, this._addItemsToTimeline, this._animateTimeline);
		this._initTwitter(this.options.mentionsUrl, this._addItemsToMentions, this._animateMentions);
	},
	
	_initTwitter: function(url, addFunction, animateFunction) {
		var self = this;
		var response = function() {
			return addFunction.apply( self, arguments );
		};
		$.getJSON(url, response);
		this.element.oneTime("2000ms", "start-marque", function() {
			animateFunction.apply( self, [] );
		});
	},
	
	_animateTimeline: function() {
		this._animate(
				"div.timeline .list",
				this.timelineGeneration,
				this.options.timelineUrl,
				this._addItemsToTimeline,
				this._animateTimeline,
				20)
	},

	_animateMentions: function() {
		this._animate(
				"div.mentions .list",
				this.mentionsGeneration,
				this.options.mentionsUrl,
				this._addItemsToMentions,
				this._animateMentions,
				15)
	},

	_animate: function(selector, generation, url, addFunction, animateFunction, speed) {
		var self = this;
		var list = this.element.find(selector);
		list.css({
			"margin-left": "0px",
			"-webkit-transition-duration": "0ms"
		});
		list.find(".generation-"+(generation-2)).remove();
		var width = list.width();
		if (width == 0) {
			this.element.oneTime("10000ms", "start-marque", function() {
				self._initTwitter.apply(self, [url, addFunction, animateFunction]);
			});
			return;
		}
		
		list.css({
			"margin-left": -width+"px",
			"-webkit-transition-duration": (width*speed)+"ms"
		});
		var response = function() {
			return addFunction.apply( self, arguments );
		};
		this.element.oneTime(((width-1400)*speed)+"ms", "update-marque", function() {
			$.getJSON(url, response);
		});
		this.element.oneTime((width*speed)+"ms", "rescroll-marque", function() {
			animateFunction.apply( self, [] );
		});
	},
	
	_addItemsToTimeline: function( data ) {
		this._addNewItems( "div.timeline .list", this.timelineGeneration, data );
		this.timelineGeneration++;
	},
	
	_addItemsToMentions: function( data ) {
		this._addNewItems( "div.mentions .list", this.mentionsGeneration, data );
		this.mentionsGeneration++;
	},

	_addNewItems: function( selector, generation, data ) {
		if (data && data.length > 0) {
			var list = this.element.find(selector);
			var listStart = list.width();
			while (list.width() < 1500+listStart) {
				$.each(data, function(index, value) {
					var item = $("<span class='item' />");
					item.addClass("generation-"+generation);
					var time = $("<span class='time'>"+value.timeAsString+"</span>");
					var user = $("<span class='user'>"+value.user+"</span>");
					var text = $("<span class='text'>"+value.text+"</span>");
					item.append(time).append(user).append(text);
					list.append(item);
				});
			}
		}
	}
});

$.extend( $.ui.dashboard, {
	version: "1.8.4"
});

})( jQuery );
