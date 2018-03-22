require.config
	paths:
		common: "../lib/common/javascripts"
		jquery: "../lib/jquery/jquery"
		bootstrap: "../lib/bootstrap/js/bootstrap.bundle"
	
	shim:
		jquery:
			exports: "$"
		bootstrap:
			deps: ["jquery"]


require ["web"]