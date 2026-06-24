var _root = '../';
var gURL = {
	root : _root,
	parent : _root+'convention/',
	ia : _root+'ia/',
	target : '',
}

var include = {
	meta : function(){
		document.write('<title>웹퍼블리싱가이드 - Standard Guide</title>');
		document.write('<meta http-equiv="X-UA-Compatible" content="IE=edge" />');
		document.write('<meta name="format-detection" content="telephone=no" />');
		document.write('<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />');
	},
	head : function(){
		document.write('<!-- Guide Common -->');
		document.write('<link href="'+gURL.root+'/assets/css/base.css" rel="stylesheet">');
		document.write('<link href="'+gURL.root+'/assets/css/common.css" rel="stylesheet">');
		document.write('<script src="'+gURL.root+'/assets/js/jquery.min.js"></script>');

		document.write('<!-- Guide Conventions-->');
		document.write('<link href="'+gURL.parent+'/assets/css/import.css" rel="stylesheet">');
		document.write('<link href="'+gURL.parent+'/assets/codeview/styles/shCoreDefaultWhite.css" rel="stylesheet" />');
		document.write('<script src="'+gURL.parent+'/assets/js/libs/jquery.mCustomScrollbar.min.js"></script>');
		document.write('<script src="'+gURL.parent+'/assets/js/libs/jquery.clipboard.min.js"></script>');
		document.write('<script src="'+gURL.parent+'/assets/js/guide.common.js"></script>');
		document.write('<script src="'+gURL.parent+'/assets/codeview/scripts/shCore.js?cb=undefined"></script>');
		document.write('<script src="'+gURL.parent+'/assets/codeview/scripts/shAutoLoader.js?cb=undefined"></script>');
		document.write('<script src="'+gURL.parent+'/assets/codeview/scripts/shBrushjScript.js?cb=undefined"></script>');
		document.write('<script src="'+gURL.parent+'/assets/codeview/scripts/shBrushXml.js?cb=undefined"></script>');
		document.write('<script src="'+gURL.parent+'/assets/codeview/scripts/shBrushCss.js?cb=undefined"></script>');
		document.write('<script>SyntaxHighlighter.all();</script>');
	},
	header : function(){
		document.write('	<header class="g-header g-in-sec">');
		document.write('		<h1 class="g-logo"><a href="/">퍼블리싱가이드</a></h1>');
		document.write('		<button type="button" class="g-btn-aside"><span>Menu</span></button>');
		document.write('		<nav class="g-gnb">');
		document.write('			<ul>');
		document.write('				<li><a href="'+gURL.ia+'index.html" data-aside="g-snbMenu4" data-url="index.html">INDEX</a></li>');
		document.write('			</ul>');
		document.write('		</nav>');
		document.write('	</header>');
	},
	aside : function(){
        var baseURL = '';
        document.write('	<aside id="g-aside">');
        document.write('		<div class="g-js-scroll g-aside-scroll">');
        document.write('			<nav class="g-snb">');
        document.write('				<!-- Convention -->');
        document.write('				<ul class="g-depth1 g-snbMenu1">');
        document.write('					<li class="g-node1"><a href="'+baseURL+'rule_intro.html">기본환경</a></li>');
        document.write('					<li class="g-node1"><a href="'+baseURL+'rule_standard.html">표준규칙</a></li>');
        document.write('					<li class="g-node1"><a href="'+baseURL+'rule_code.html">코드규칙</a></li>');
        document.write('					<li class="g-node1"><a href="'+baseURL+'rule_name.html">네임규칙</a></li>');
        document.write('					<li class="g-node1"><a href="'+baseURL+'rule_codeset.html">설계패턴</a></li>');
        document.write('					<li class="g-node1"><a href="'+baseURL+'rule_title.html">서식명칭</a></li>');
        document.write('				</ul>');
        document.write('				<!-- //Convention -->');
        document.write('			</nav>');
        document.write('		</div>');
        document.write('	</aside>');
	},
	footer : function(){
		document.write('	<a href="#g-wrap" class="g-top" data-role="spy-scroll">TOP</a>');
		document.write('	<div class="g-mask"></div>');
	},
}