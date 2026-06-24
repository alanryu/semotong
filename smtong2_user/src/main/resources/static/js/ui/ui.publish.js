/*-------------------------------------------------------------------
	분류순서
	- @ Publish	    : 레이아웃
	- @ Init	    : 초기실행
-------------------------------------------------------------------*/
$(function(){
	$(".header-desktop").each(function(){
		var $nav = $(this).find('.header-nav');
		if ($('.F-G01-01-page').length) {
			$nav.find('.nav-item').eq(0).find('.nav-link').addClass('is-selected');
		}
		else if ($('.F-P01-01-page').length) {
			$nav.find('.nav-item').eq(1).find('.nav-link').addClass('is-selected');
		}
		else if ($('.F-I01-01-page').length) {
			$nav.find('.nav-item').eq(3).find('.nav-link').addClass('is-selected');
		}
		else if ($('.F-HP01-01-page').length) {
			$nav.find('.nav-item').eq(2).find('.nav-link').addClass('is-selected');
		}
		else if ($('.F-E01-01-page').length) {
			$nav.find('.nav-item').eq(4).find('.nav-link').addClass('is-selected');
		}
		else if ($('.F-MY01-01-page').length) {
			$nav.find('.nav-item').eq(5).find('.nav-link').addClass('is-selected');
		}
	});
	$(".aside-modal").each(function(){
		$(this).load("../../html/layout/include_mobileNav.html", function(){
			console.log('[INFO] 퍼블리싱 include_mobileNav.html 인클루드 완료');
		});
	})
	$(".footer").each(function(){
		$(this).load("../../html/layout/include_footer.html", function(){
			console.log('[INFO] 퍼블리싱 include_footer.html 인클루드 완료');
		});
	});
	$(".floating-nav").each(function(){
		var $nav = $(this);
		$(this).load("../../html/layout/include_floatingNav.html", function(){
			if ($('.F-M01-page').length) {
				$nav.find('.nav-item').eq(0).find('.nav-link').addClass('is-selected');
			} else if ($('.F-P01-01-page').length) {
				$nav.find('.nav-item').eq(1).find('.nav-link').addClass('is-selected');
			} else if ($('.F-I01-01-page').length) {
				$nav.find('.nav-item').eq(2).find('.nav-link').addClass('is-selected');
			} else if ($('.F-HP01-01-page').length) {
				$nav.find('.nav-item').eq(3).find('.nav-link').addClass('is-selected');
			} else if ($('.F-MY01-01-page').length) {
				$nav.find('.nav-item').eq(4).find('.nav-link').addClass('is-selected');
			}
		});
	});
});
