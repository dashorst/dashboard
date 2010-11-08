Topicus Onderwijs Dashboard
===========================

This project is meant to be an implementation of the [Panic
Dashboard](http://www.panic.com/blog/2010/03/the-panic-status-board/) that was
published by the [Panic team](http://panic.com) in last March.

This is an internal project for Topicus Onderwijs and will ultimately run on a
PC + LCD monitor near our coffee corner. The idea is to show the status of our
production systems, build system, issues, release calendar and other
interesting things that make our company tick.

The project consists of two parts: a HTML5/CSS3 client and a small light
weight Java server that will aggregate the data for the client from our
production systems.

Currently the dashboard has the following modules:

 * Project status
 * NS (Dutch railways train schedule)
 * MantisBT
 * Google Calendar
 * Hudson
 * Subversion

A twitter status module is in the works.

The Topicus Onderwijs Dashboard only works in the latest webkit browsers. No
effort has been done to make it work in Firefox, Opera or IE. Safari and
Chromium work as advertised.

## Configuration ##

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

## Available Modules ##

### Project Status Integration ###

To show the status of a project we need data. This is in the form of number of
current users, average request time, deployed version, etc. At Topicus we
created special status pages to retrieve this type of information. Typically a
status page shows the overall health of the application for a server.

Waiting for a proper JSON status resource, we just scrape our status pages
using the `FooRetriever` classes. To show your project's health, you should
create such a status page yourself for your own application and write a
scraper for it. In the `onConfigure` method you can add the `DataSource`
implementations your retriever provides. See the `ParnassysStatusRetriever` as
a typical example.

Configuring a project for the status retrievers is simple:

	"<classname of your Retriever implementation" : {
	  "urls" : [
	    "<(list of) url(s) to status page of this project>"
	  ]
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

### MantisBT Integration ###

MantisBT integration requires MantisConnect 1.2.1.0 which is unfortunately not
available in a Maven repository. You can rebuild the connector yourself
against a l.2+ MantisBT installation.

Configuring the MantisBT integration:

	"nl.topicus.onderwijs.dashboard.modules.mantis.MantisService" : {
	  "url" : "<mantisbt connect URL",
	  "username" : "<dashboard username>",
	  "password" : "<dashboard password",
	  "projects" : [<list of project ids],
	  "filter" : <filter id>
	}

The MantisBT connect URL is something like:

    http://<serverurl>/mantisbt/api/soap/mantisconnect.php

The user needs developer permission for each project to see the issues.


### Subversion Integration ###

The Subversion module shows the latest commits that were applied to the stream
of monitored projects. Each project that wants its commits to be displayed in
the commit log needs to be configured in the following way:

	"nl.topicus.onderwijs.dashboard.modules.svn.SvnService" : {
	  "url" : "<url to svn of your project",
	  "username" : "<dashboard username>",
	  "password" : "<dashboard password>"
	},

The URL is the point at which place the commits are logged from. If you have a
standard layout like:

	/project
	    /trunk
	    /branches
	    /tags

Setting the path to the root of the project will track all commits done to
trunk, tags and branches. If you set the path to the `trunk` folder, only
commits to `trunk` will be logged.
