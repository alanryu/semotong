var gCom = {
	init : function(){
		console.log('gCom.init()');
		this.gHeader.init();
		this.gAside.init();
		this.gBreadCrumb.init();
		this.gResponsive.init();
	},
	gResponsive : {
		asideW : $('#g-aside').width(),
		init : function(){
			this.action();
			this.event();
		},
		event : function(){
			var _this = this;
			var time = null;
			$(window).on('resize', function(){
				clearTimeout(time);
				time = setTimeout(function(){
					_this.action();
				},300)
			})
		},
		action : function(){
			//Set is-responsive-md
			if ($(window).width() > 819 && $(window).width() < 1024 && !$('body').hasClass('is-responsive-md')){
				$('body').addClass('is-responsive-md');
			} else if ($(window).width() > 1023 && $('body').hasClass('is-responsive-md')){
				$('body').removeClass('is-responsive-md');
			}

			//Set is-responsive-sm
			if ($(window).width() < 820 && !$('body').hasClass('is-responsive-sm')){
				$('body').addClass('is-responsive-sm');
				$('body').removeClass('is-responsive-md');
				if ($('.g-lnb > .g-node-title').length == 0){
					var noteTitle = $('.g-lnb > ul > li.is-current > a').text();
					$('.g-lnb > ul').before('<button type="button" class="g-node-title"></button>');
					$('.g-lnb > .g-node-title').text(noteTitle);

					//Event
					$('.g-node-title').on('click', function(){
						$(this).parent().toggleClass('is-visible');
					})
				}
			} else if ($(window).width() > 819 && $('body').hasClass('is-responsive-sm')){
				$('body').removeClass('is-responsive-sm');
				$('body').addClass('is-responsive-md');
				$('.g-lnb > .g-node-title').remove();
			}

			//Set is-aside-closed
			if ($(window).width() < 1024 && !$('body').hasClass('is-aside-closed') && !$('body').hasClass('is-aside-opened')){
				$('body').addClass('is-aside-closed');
			} else if ($(window).width() > 1023 && $('body').hasClass('is-aside-closed')){
				$('body').removeClass('is-aside-closed');
			}
		}
	},
	gHeader : {
		headerEl : '.g-header',
		init : function(){
			this.setInit();
			this.action();
			this.event();
		},
		setInit : function(){
			var path = location.pathname;
			var snbMenu = 'g-snbMenu0';
			var activeEl = null;
			$('.g-lnb a').each(function(i){
				console.log(path.indexOf($(this).attr('data-url')));
				if (path.indexOf($(this).attr('data-url')) > -1){
					activeEl = $(this);
					activeEl.parent().addClass('is-current');
					snbMenu = activeEl.attr('data-aside');
					$('.'+snbMenu).show().siblings().not(':visible').remove();
				}
				else if ($('.g-lnb a').length - 1 == i){
					if (activeEl == null){
						$('.'+snbMenu).show().siblings().not(':visible').remove();
					}
				}
				return;
			});
		},
		event : function(){
			var _this = this;
			$(window).on('scroll', function(){
				_this.action();
			})
			$('.g-lnb > ul > li > a').on('click', function(e){
			})
		},
		action : function(){
			if ($(window).scrollTop() > 0 && $('body').hasClass('is-body-scrolled') == false){
				$('body').addClass('is-body-scrolled');
			} else if ($(window).scrollTop() == 0 && $('body').hasClass('is-body-scrolled') == true){
				$('body').removeClass('is-body-scrolled');
			}
		}
	},
	gAside : {
		asideEl : '#g-aside',
		anbBtnEl : '.g-btn-aside',
		maskEl : '.g-mask',
		asideWid : null,
		init : function(){
			if (location.hash != ''){
				gUI.spyScroll.action(location.hash);
			}

			this.asideWid = $(this.asideEl).width();
			this.setInit();
			this.event();
		},
		setInit : function(){
			var _this = this;
			var path = location.pathname.split('/');
			$(this.asideEl).find('.g-snb a[href*="'+path[path.length-1]+'"]').parent().addClass('is-current');
		},
		event : function(){
			//펼치기
			$(this.anbBtnEl).not('.is-clickEvent').on('click', function(e){
				if (!$('body').hasClass('is-aside-closed')){
					$('body').removeClass('is-aside-opened');
					$('body').addClass('is-aside-closed');
				} else {
					$('body').addClass('is-aside-opened');
					$('body').removeClass('is-aside-closed');
				}
			}).addClass('is-clickEvent');

			//숨기기
			$(this.maskEl).not('.is-clickEvent').on('click', function(e){
				$('body').removeClass('is-aside-opened').addClass('is-aside-closed');
			}).addClass('is-clickEvent');
		},
	},
	gBreadCrumb : {
		init : function(){
			$('.g-content-header h1').each(function(){
				$('.g-breadcrumb p').text($(this).text());
			})
		}
	}
}

var gUI = {
	init : function(){
		this.winEvent();
		this.scrolled.init();

		if ($('.g-js-scroll').length){
			this.mScroll.init();
		}
		if ($('[data-role=spy-scroll]').length){
			this.spyScroll.init();
		}
		if ($('.g-tab-codeview').length){
			this.tabCodeview.init();
		}
		if ($('.g-example-wrap').length){
			this.example.init();
		}
		if ($('.js-follow').length){
			this.followActive.init();
		}
		if ($('.g-example-copy').length){
			//ut.setScriptLoader(gRootURL.root+'_wsg/common/js/addon/jquery.clipboard.min.js', 'clipboardScript');
			//this.copyed.init();
		}
	},
	winEvent : function(){
		var setTime = null;
		$(window).on('scroll', function(){
			clearTimeout(setTime);
			setTime = setTimeout(function(){
				gUI.scrolled.init();
			},10)
		})
	},
	mScroll : {
		scrollEl : '.g-js-scroll',
		init : function(){
			$(this.scrollEl).each(function(){
				$(this).mCustomScrollbar({scrollInertia:200});
			})
		}
	},
	spyScroll : {
		init : function(){
			var _this = this;
			var id = null;
			$('[data-role=spy-scroll]').on('click', function(){
				if ($(this).attr('href').indexOf('#') > -1){
					id = '#' + $(this).attr('href').split('#')[1];
				} else {
					id = $(this).attr('href');
				}

				_this.action(id);
			})
		},
		action : function(id){
			var topH = $('.g-header').height();
			var gapH = 30;
			var scrObj = 'html, body';
			if ($(id).length){
				$(scrObj).stop().animate({scrollTop:$(id).offset().top - topH - gapH}, 500);
			}
		},
	},
	scrolled : {
		init : function(){
			if ($('html').scrollTop() > 50){
				if (!$('#g-wrap').is('.is-scrolled')){
					$('#g-wrap').addClass('is-scrolled');
				}
			} else {
				if ($('#g-wrap').is('.is-scrolled')){
					$('#g-wrap.is-scrolled').removeClass('is-scrolled');
				}
			}
		}
	},
	tabCodeview : {
		tabNav : '.g-tab',
		tabLink : '.g-tab-codeview .g-tab-nav a',
		target : null,

		init : function(){
			if ($(this.tabNav).length > 0){
				this.event();
			}
		},
		event : function(){
			//현재페이지의 탭 활성화
			$(this.tabLink).on('click', function(){
				gUI.tabCodeview.action($(this));return false;
			});
		},
		action : function($this){
			this.target = $this.attr('href');
			if ($this.parent().is('.is-active')){
				$this.parent().removeClass('is-active');
				$(this.target).removeClass('is-active');
			} else {
				$this.parent().addClass('is-active').siblings().removeClass('is-active');
				$(this.target).addClass('is-active').siblings().removeClass('is-active');
			}
		}
	},
	example : {
		moduleEl : '.g-example-wrap',
		btnEl : '.g-example-btn',
		contentEl : '.g-example-footer',
		target : null,

		init : function(){
			this.event();
		},
		event : function(){
			var _this = this;
			//현재페이지의 탭 활성화
			$(this.btnEl).on('click', function(){
				var obj = $(this).closest(_this.moduleEl);
				var idx = $(this).parent().find(_this.btnEl).index($(this));
				gUI.example.action(obj, idx); return false;
			});
		},
		action : function($module, $index){
			var $this = $module.find($(this.btnEl)).eq($index);
			this.target = $module.find($(this.contentEl)).eq($index);
			if ($this.is('.is-active')){
				$this.removeClass('is-active');
				$(this.target).removeClass('is-active');
			} else {
				$this.addClass('is-active').siblings().removeClass('is-active');
				$(this.target).addClass('is-active').siblings().removeClass('is-active');
			}
		}
	},
	copyed : {
		elWrap : '.g-example-wrap',
		elCopy : '.g-example-copy',
		elTarget : '.g-example-body',
		init : function(){
			this.reset();
		},
		reset : function(){
			var _this = this;
			var lenCopy = $(_this.elCopy).length;
			$(_this.elCopy).each(function(i){
				var targetHTML = $(this).closest(_this.elWrap).find(_this.elTarget).html();
				$(this).attr('data-clipboard-text', targetHTML);
				if (i == lenCopy-1){
					//_this.action();
				}
			})
		},
		action : function(){
				console.log('a');
			var clipboard = new Clipboard($(this.elCopy));
		}
	}
}
$(document).ready(function(){
	gCom.init();
	gUI.init();
})
