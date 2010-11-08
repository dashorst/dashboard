Topicus Onderwijs Dashboard
===========================

This project is meant to be an implementation of the [Panic
Dashboard](http://www.panic.com/blog/2010/03/the-panic-status-board/) that was
published by the [Panic team](http://panic.com) in last March.

This is an internal project for Topicus Onderwijs and will ultimately run on a
PC + LCD monitor near our coffee corner. The idea is to show the status of our
production systems, build system, issues, release calendar and other
interesting things that make our company tick.

The project will consist of two parts: a HTML5/CSS3 client and a small light
weight Java server that will aggregate the data for the client from our
production systems.

Configuration
-------------

The configuration for the dashboard is stored in 

	~/.dashboard/nl.topicus.onderwijs.dashboard.modules.Settings.json

This JSON file contains the list of projects, locations and organizations (and
possibly other types of keys) and for each key (project, location,...) the
settings for the enabled plugins.

	{
	  "keys" : [ {
	    "nl.topicus.onderwijs.dashboard.keys.Project" : {
	      "code" : "<identifier>",
	      "name" : "<descriptive name>"
	    }
	  }, {
	    "nl.topicus.onderwijs.dashboard.keys.Project" : {
	      "code" : "dashboard",
	      "name" : "Dashboard"
	    }
	  }, {
	    "nl.topicus.onderwijs.dashboard.keys.Location" : {
	      "code" : "amsterdam",
	      "name" : "Amsterdam"
	    }
	  }, {
	    "nl.topicus.onderwijs.dashboard.keys.Organization" : {
	      "code" : "topicus",
	      "name" : "Topicus"
	    }
	  } ],
	  "projectSettings" : {
	    "amsterdam" : {
	      "nl.topicus.onderwijs.dashboard.modules.ns.NSService" : {
	        "station" : "Amsterdam"
	      }
	    },
	    "topicus" : {
	      "nl.topicus.onderwijs.dashboard.modules.google.GoogleEventService" : {
	        "username" : "<google-calendar username>",
	        "calendarId" : "<identifier for the calendar>",
	        "password" : "<password>"
	      }
	    },
	    "dashboard" : {
	      "nl.topicus.onderwijs.dashboard.modules.google.GoogleEventService" : {
	        "username" : "<google-calendar username>",
	        "calendarId" : "<identifier for the calendar>",
	        "password" : "<password>"
	      },
	      "nl.topicus.onderwijs.dashboard.modules.hudson.HudsonService" : {
	        "matchers" : [ "<regex matching project name>" ],
	        "url" : "<hudson url>"
	      }
	    }
	  }
	}

### Google Calendar Integration ###

To access Google calendar you need to provide a username, password and the
calendar ID you want to retrieve events from. If you add `#major` in the body
of the event, the event will be shown in the major events section.

### Hudson Integration ###

The hudson configuration per project consists of two parameters:

 1. Project matchers
 2. Hudson URL

The project matchers is a JSON list of regular expressions that match Hudson
jobs for your project. As it is customary to have a seperate build for
production branch, acceptance test branch and trunk, the Hudson plugin will
aggregate build information accross the matched jobs.

The Hudson url points to the root of your Hudson installation. The Hudson
dashboard plugin will take it from there using the Hudson JSON API.

