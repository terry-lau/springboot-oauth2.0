$.noConflict();

jQuery(document).ready(function($) {

	"use strict";

	[].slice.call( document.querySelectorAll( 'select.cs-select' ) ).forEach( function(el) {
		new SelectFx(el);
	});

	jQuery('.selectpicker').selectpicker;
	
	if($.cookie("boUser") == "true") {
		$("#rememberme").attr("checked", true);
		$("#uname").val($.cookie("username"));
		$("#password").val($.cookie("password"));
	}

	$('.search-trigger').on('click', function(event) {
		event.preventDefault();
		event.stopPropagation();
		$('.search-trigger').parent('.header-left').addClass('open');
	});

	$('.search-close').on('click', function(event) {
		event.preventDefault();
		event.stopPropagation();
		$('.search-trigger').parent('.header-left').removeClass('open');
	});

	// var chartsheight = $('.flotRealtime2').height();
	// $('.traffic-chart').css('height', chartsheight-122);


	// Counter Number
	$('.count').each(function () {
		$(this).prop('Counter',0).animate({
			Counter: $(this).text()
		}, {
			duration: 3000,
			easing: 'swing',
			step: function (now) {
				$(this).text(Math.ceil(now));
			}
		});
	});

	$("#uname").blur(function(){
		var val = $('#uname').val();
		$('#username').val(val);
	});

	$("#bu").on('click',function(){
		var name = $('#uname').val();
		var opt = $('#j_captcha').val();
		$('#username').val(name.trim() + "&&&" + opt.trim());
		if($("#rememberme").prop("checked")) {
			var str_username = $("#uname").val();
			var str_password = $("#password").val();
			$.cookie("boUser", "true", {
				expires: 7
			}); //存储一个带7天期限的cookie
			$.cookie("username", str_username, {
				expires: 7
			});
			$.cookie("password", str_password, {
				expires: 7
			});
		} else {
			$.cookie("boUser", "false", {
				expire: -1
			});
			$.cookie("username", "", {
				expires: -1
			});
			$.cookie("password", "", {
				expires: -1
			});
		}
	});

	// Menu Trigger
	$('#menuToggle').on('click', function(event) {
		var windowWidth = $(window).width();   		 
		if (windowWidth<1010) { 
			$('body').removeClass('open'); 
			if (windowWidth<760){ 
				$('#left-panel').slideToggle(); 
			} else {
				$('#left-panel').toggleClass('open-menu');  
			} 
		} else {
			$('body').toggleClass('open');
			$('#left-panel').removeClass('open-menu');  
		} 
			 
	}); 

	$(".menu-item-has-children.dropdown").each(function() {
		$(this).on('click', function() {
			var $temp_text = $(this).children('.dropdown-toggle').html();
			$(this).children('.sub-menu').prepend('<li class="subtitle">' + $temp_text + '</li>'); 
		});
	});


	// Load Resize 
	$(window).on("load resize", function(event) { 
		var windowWidth = $(window).width();  		 
		if (windowWidth<1010) {
			$('body').addClass('small-device'); 
		} else {
			$('body').removeClass('small-device');  
		} 
		
	});
 
});