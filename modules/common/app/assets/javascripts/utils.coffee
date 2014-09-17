define ["jquery", "bootstrap"], ($, bootstrap) ->
	
	fillSignInForm: ($tr) ->
		$('#identifier, #email').val($tr.find('.email').text())
		$('#password').val($tr.find('.pwd ').text())