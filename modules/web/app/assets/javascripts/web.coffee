define ["jquery", "bootstrap", "common/utils"], ($, bootstrap, utils) ->

	############################################################################################################
	## DOCUMENT IS READY - INIT APP
	############################################################################################################
	$ ->
		$('#signin-helper tr').click (e) -> utils.fillSignInForm $(this)