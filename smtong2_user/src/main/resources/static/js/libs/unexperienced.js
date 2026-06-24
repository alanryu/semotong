/**
 * 알뜰요금제 사용 미경험자 관련 동작설정
 */

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

/* 선택 데이터 */
var param = new Object();

$(document).ready(function(){
	
	/* 알뜰폰 or 3사 or 둘다 */
	$(document).on('click', 'input[name=hostType]', function(){
		param.hostType = $(this).val();
		callHtml('step2');
		$('#step').val('step2');
	})
	
	/* 사용중인 통신사 선택 */
	$(document).on('click', 'input[name=mno]', function(){
		param.mno = $(this).val();
		callHtml('step3');
		$('#step').val('step3');
	});
	
	/* 통신요금 선택 */
	$(document).on('click', 'input[name=price]', function(){
		param.price = $(this).val();
		callHtml('step4');
		$('#step').val('step4');
	});
	
	/* 데이터 선택 */
	$(document).on('click', 'input[name=supData]', function(){
		param.supData = $(this).val();
		
		/* 통신 3사 선택인 경우 통화량 선택 제외 */
		if ( param.hostType == 'mno' ) {
			param.supCall = '';
			callHtml('step6');
			$('#step').val('step6');
		} else {
			callHtml('step5');
			$('#step').val('step5');
		}
	});
	
	/* 통화량 선택 */
	$(document).on('click', 'input[name=supCall]', function(){
		param.supCall = $(this).val();
		callHtml('step6');
		$('#step').val('step6');
		
		/* 챗봇배너 */
		chatbotBanner();
	});
	
	/* 결합 여부 선택 */
	$(document).on('click', 'input[name=combination]', function(){
		
		/* 서칭 이미지 표시 */
		searchImg();
		/* 선택된 데이터를 바탕으로 plan_list 검색 */
		param.combination = $(this).val();
		searchData(param);
		
		$('#step').val('step7');
	});
	
	/* 카카오톡 공유하기 */
	$(document).on('click', '.kakao-btn', function(){
		var shareModal = new bootstrap.Modal(document.getElementById('shareModal'));
		shareModal.show();
	});
})

/**
 * 레이어팝업 화면변경 html호출
 */
function callHtml(step) {
	if(step =="step0") {
		var data = `
		<!-- 모통봇 소개 섹션 -->
		<section class="cb-intro-sec">
			<div class="sec-inner">
				<div class="intro-img">
					<i class="ui-icn icn-62-motong" aria-hidden="true"></i>
				</div>
				<div class="intro-con">
					<p>안녕하세요. 고객님께 딱 맞는<br> 요금을 추천해드릴 모통봇 이에요.</p>
					<p>모통봇이 알아서 잘 딱 깔끔하고<br>센스있게 알려드릴게요.</p>
				</div>
			</div>
		</section>
		<!-- // 모통봇 소개 섹션 -->

		<!-- 사용자 유형선택 섹션 -->
		<section class=" cb-selection-sec">
			<div class="sec-inner">
				<p class="selection-tit">
					알뜰폰, 잘 알고 계세요?
				</p>
				<div class="selection-frm">
					<!-- 선택된 상태 : is-selected 클래스 -->
					<label class="selection-rdo" id="ui-chatbot-o">
						<img src="/images/common/img-chatbot-o.png" alt="Yes">
						<span class="ui-rdo">
							<input type="radio" name="frmTypeRadio1" onchange="setRadioSelected('frmTypeRadio1', '.selection-rdo')">
							<span class="rdo"><span><em>네,</em> 알뜰폰 사용해 본 적이 <br>있어서 잘 알아요.</span></span>
						</span>
					</label>
					<label class="selection-rdo" id="ui-chatbot-x">
						<img src="/images/common/img-chatbot-x.png" alt="No">
						<span class="ui-rdo">
							<input type="radio" name="frmTypeRadio1" onchange="setRadioSelected('frmTypeRadio1', '.selection-rdo')">
							<span class="rdo"><span><em>아니오.</em> 알뜰폰이 <br>처음이에요.</span></span>
						</span>
					</label>
				</div>
			</div>
		</section>
		<!-- // 사용자 유형선택 섹션 -->
		`;
		$('#modal-content-css').removeClass().addClass('modal-content').addClass('F-CB01-page');
		/* 진행율 숨기기 */
		$('.modal-progress').hide();

		/* modal footer 보여주기 */
		$('#btGoFirst').hide();
		$('#modal-contents').html(data);
		
		step0RdoLisner();
		
	} else {
		$.ajax({
				url 		: '/svc/chatbot/unexperienced/' + step,
				method 		: 'get',
				dataType 	: 'html',
				beforeSend 	: function(xhr){
			       xhr.setRequestHeader(header,token);
			    },
				success 	: function(data) {
					/* modal body css 변경 */
					$('#modal-content-css').removeClass().addClass('modal-content').addClass('F-CB03-01-page');
					
					/* modal body에 컨텐츠 덮어쓰기 */
					$('#modal-contents').html(data);
					
					/* modal footer 보여주기 */
					$('.modal-footer').show();
					
					/* 진행률 표시 (progress) */
					$('.modal-progress').show();
					if ( step == 'step1' ) {
						$('.progress-value').attr('aria-label', 'Step 1/6').css('width', 'calc((1 / 6) * 100%)');
					} else if ( step == 'step2' ) {
						$('.progress-value').attr('aria-label', 'Step 2/6').css('width', 'calc((2 / 6) * 100%)');
					} else if ( step == 'step3' ) {
						$('.progress-value').attr('aria-label', 'Step 3/6').css('width', 'calc((3 / 6) * 100%)');
					} else if ( step == 'step4' ) {
						$('.progress-value').attr('aria-label', 'Step 4/6').css('width', 'calc((4 / 6) * 100%)');
					} else if ( step == 'step5' ) {
						$('.progress-value').attr('aria-label', 'Step 5/6').css('width', 'calc((5 / 6) * 100%)');
					} else if ( step == 'step6' ) {
						$('.progress-value').attr('aria-label', 'Step 6/6').css('width', 'calc((6 / 6) * 100%)');
						initSwiper();
					}
				}
			});
	}
	
	
}

/* 스와이프 활성화 */ 
function initSwiper() {
    var eventSwiper = new Swiper(".event-swiper", {
		threshold: 3,
		spaceBetween: 10,
		pagination: {
			el: ".swiper-pagination",
		},
		autoplay: {
			delay: 2500,
			disableOnInteraction: false,
		},
	});
}

/* 서칭 이미지 표시 */
function searchImg() {

	var html = '';
	html += '<section class="cb-intro-sec">';
	html += '	<div class="sec-inner">';
	html += '		<div class="intro-img">';
	html += '			<i class="ui-icn icn-62-motong" aria-hidden="true"></i>';
	html += '		</div>';
	html += '		<div class="intro-con">';
	html += '			<p>고객님께 딱 맞는 요금을<br>찾고 있어요!</p>';
	html += '		</div>';
	html += '	</div>';
	html += '</section>';

	html += '<section class="cb-searching-sec">';
	html += '	<div class="sec-inner">';
	html += '		<div class="searching-data">';
	html += '			<div class="searching" role="text" aria-label="검색중">';
	html += '				<div class="img-list">';
	html += '					<img src="/images/common/img-130-srch-list.svg" alt="" />';
	html += '				</div>';
	html += '				<div class="img-zoom">';
	html += '					<img src="/images/common/img-130-srch-zoom.svg" alt="" />';
	html += '				</div>';
	html += '			</div>';
	html += '		</div>';
	html += '	</div>';
	html += '</section>';
	
	/* modal body css 변경 */
	$('#modal-content-css').removeClass().addClass('modal-content').addClass('F-CB03-05-page');
	
	/* 진행율 숨기기 */
	$('.modal-progress').hide();
	
	/* modal footer 보여주기 */
	$('.modal-footer').hide();
	
	/* modal body에 컨텐츠 덮어쓰기 */
	$('#modal-contents').html(html);
}

function searchData(param) {
	
	setTimeout(function(){
		$.ajax({
			url 		: '/svc/chatbot/unexperiencedResult',
			method 		: 'post',
			data 		: JSON.stringify(param),
			dataType 	: 'json',
			contentType	: 'application/json',
			async 		: false,
			beforeSend 	: function(xhr){
		       xhr.setRequestHeader(header,token);
		    },
			success 	: function(data) {
				
				if ( data.totalCnt > 0 ) {
					/* 안내 멘트 */
					var html = '';
					html += '<section class="cb-intro-sec">';
					html += '	<div class="sec-inner">';
					html += '		<div class="intro-img">';
					html += '			<i class="ui-icn icn-62-motong" aria-hidden="true"></i>';
					html += '		</div>';
					html += '		<div class="intro-con">';
					html += '			<p>고객님께 딱 맞는 <br>요금제를 추천 드려요!</p>';
					html += '		</div>';
					html += '	</div>';
					html += '</section>';
					
					$('#modal-contents').html(html);
					
					/* 차트 노출 process */
					showChart(param.price, data.data[0]);
					
					
					$.each(data.data, function(idx, obj){
						obj.displayPrice = 0;
						obj.listSp = "1";
					});
											
											
					/* 추천요금제 html */
					suggestReslut(data.data, param.hostType);
				} else {
					var html = '';
					html += '<section class="cb-intro-sec">';
					html += '	<div class="sec-inner">';
					html += '		<div class="intro-img">';
					html += '			<i class="ui-icn icn-62-motong" aria-hidden="true"></i>';
					html += '		</div>';
					html += '		<div class="intro-con">';
					html += '			<p>조건에 맞는 요금제가 없네요...</p>';
					html += '		</div>';
					html += '	</div>';
					html += '</section>';
					html += '<section class="cb-ratePlanC-sec">';
					html += '	<div class="sec-inner">';
					html += '		<div class="sec-body">';
					html += '			<div class="ratePlanC-list-wrap">';
					html += '				<div class="nodata-wrap">';
					html += '					<div class="nodata">';
					html += '						<i class="ui-icn icn-110-nodata-cry" aria-hidden="true"></i>';
					html += '					</div>';
					html += '				</div>';
					html += '				<button type="button" class="ui-btn btn-more-list" id="more-list-main" data-type="' + param.hostType + '">';
					html += '					<span>다른 요금제를 안내해 드릴까요?</span>';
					html += '					<i class="ui-icn icn-16-more-02-gr" aria-hidden="true"></i>';
					html += '				</button>';
					html += '			</div>';
					html += '		</div>';
					html += '	</div>';
					html += '</section>';
					
					$('#modal-contents').html(html);
				}
				
				/* modal footer 보여주기 */
				$('.modal-footer').show();
				
				/* modal body css 변경 */
				$('#modal-content-css').removeClass().addClass('modal-content').addClass('F-CB03-06-page');
			}
		});
	}, 1000)
}

function addCommas(number) {
	
	var ret= "";
	
	if(number !=null && number != '' && !isNaN(number))  {
		ret = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");	
	} else {
		ret = number;
	}
    return ret;
}

// ================== 요금제 데이터 표시용=====================
		
// 요금제 명(세모통 요금제명이 있는 경우 세모통 요금제 명 아닌 경우 스크래핑 요금제명)
function getPlanNm(orgPlanNm,smtPlanNm) {
	var result = "";
	
	if(smtPlanNm =="" || smtPlanNm == null) {
		result = orgPlanNm;
	} else {
		result = smtPlanNm;
	}
	return result;
}


// sms 표시
function getSmsStr(val) {
	var result = "";
	
	if(val == 9999) {
		result = "무제한";
	} else {
		result = val + "건";
	}
	
	return result;
}

// 음성 표시
function getCallStr(val) {
	var result = "";
	if(val == 9999) {
		result = "무제한";
	} else {
		result = val + "분";
	}
	return result;			
}

// 요금표시
function getPriceStr(val) {
	return addCommas(val) + "원";
}

/* 상세페이지 이동(새창)*/
function goDetail(planid, hostNm) {
	
	var path = 'mvno';
		
	if ( hostNm == 'SKT' || hostNm == 'KT' || hostNm == 'LGU' ) {
		path = 'mno';
	}
	
	window.open('/pbm/plan/planDetail/' + path + '?planid=' + planid);
}

/* 차트설정 */ 
function initChart() {
	const dataStyles = document.querySelectorAll("[data-style]");
	setTimeout(() => {
		dataStyles.forEach(element => {
			const styles = element.getAttribute("data-style");
			if (styles) {
				element.setAttribute("style", styles);
			}
		});
	}, 300); // 최소값 0.3초 후 실행 (추가 delay는 css로 조절하겟습니다.)
}

/* 4G, LTE 통일 */
function getPlanTypeNm(planType) {
	
	var typeNm = planType;
	
	if ( typeNm == '4G' ) {
		typeNm = 'LTE';
	}
	
	return typeNm;
}

/* 차트 노출 process */
function showChart(selPrice, data) {
	
	/* 기존사용중인 요금제 1년 금액 */
	var yearPrice = (selPrice * 10000) * 12;
	
	/* 최저가 금액 계산 */
	var salePrice = data.salePrice;						//할인 금액
	var afterPrice = data.afterPrice;					//할인기간 종료후 금액
	var promotionPeriodVal = data.promotionPeriodVal;	//할인기간

	var salePriceRes = 0;
	var afterPriceRes = 0;
	var resultPrice = 0;
	
	/* 할인기간이 평생이거나 12개월이상인 경우 */
	if ( promotionPeriodVal == 9999 || promotionPeriodVal >= 12 ) {
		resultPrice = salePrice * 12;
	/* 할인기간이 12개월 미만인 경우 */	
	} else {
		salePriceRes = salePrice * promotionPeriodVal;
		afterPriceRes = afterPrice * (12 - promotionPeriodVal);
		resultPrice = salePriceRes + afterPriceRes;
	}
	
	/* 차감액 5만원 이상일시 차트 노출 */
	if ( (yearPrice - resultPrice) >= 50000 ) {
		
		/* 퍼센트 계산 */
		var per = (resultPrice / yearPrice) * 100;
		
		var html = '';
		html += '<section class="cb-graph-sec">';
		html += '	<div class="sec-inner">';
		html += '		<div class="graph-wrap">';
		html += '			<div class="graph-grid"></div>';
		html += '			<div class="graph-list">';
		html += '				<div class="graph-item type-normal">';
		html += '					<div class="graph-track">';
		html += '						<div class="graph-bar"></div>';
		html += '					</div>';
		html += '					<div class="graph-data">';
		html += '						<p class="period">1년 통신비</p>';
		html += '						<p class="charge"><strong>약 ' + Math.floor(yearPrice / 10000) + '만원</strong></p>';
		html += '					</div>';
		html += '				</div>';
		html += '				<div class="graph-item type-savings">';
		html += '					<div class="graph-track">';
		html += '						<div class="graph-bar" data-style="height: calc((' + Math.floor(per) + ' / 100) * 100%)">';
		html += '							<p class="value">' + (100 - Math.floor(per)) + '% 절감!</p>';
		html += '						</div>';
		html += '					</div>';
		html += '					<div class="graph-data">';
		html += '						<p class="period">1년 통신비</p>'; //현재 위약금 제외
		html += '						<p class="charge"><strong>약 ' + Math.floor(resultPrice / 10000) + '만원</strong></p>';
		html += '					</div>';
		html += '				</div>';
		html += '				<div class="graph-savings" aria-hidden="true">';
		html += '					<div class="graph-gage" data-style="height: calc((' + (100 - Math.floor(per)) + ' / 100) * 100%)"></div>';
		html += '				</div>';
		html += '			</div>';
		html += '		</div>';
		html += '	</div>';
		html += ' </section>';
		
		$('#modal-contents').append(html);
		
		initChart();
	}
}

/* 요금제 추천 리스트 */
function suggestReslut(data, hostType) {
	
	var html = '';
	
	html += '<section class="cb-ratePlanC-sec">';
	html += '	<div class="sec-inner">';
	html += '		<div class="sec-body">';
	html += '			<div class="ratePlanC-list-wrap">';
	html += '				<ul class="ratePlanC-list" id="cbRateList02">';
	html += '				</ul>';
	html += '				<button type="button" class="ui-btn btn-more-list" id="more-list-main" data-type="' + hostType + '">';
	html += '					<span>다른 요금제를 안내해 드릴까요?</span>';
	html += '					<i class="ui-icn icn-16-more-02-gr" aria-hidden="true"></i>';
	html += '				</button>';
	html += '			</div>';
	html += '		</div>';
	html += '	</div>';
	html += '</section>';
	
	$('#modal-contents').append(html);
	
	
	$("#planListTmpl").tmpl(data).appendTo("#cbRateList02");
	
	setPopover('ui-popover popover-style1');
}

/* 챗봇배너 들고오기 */
function chatbotBanner() {
	
	$.ajax({
		url 		: '/svc/chatbot/banner',
		method 		: 'get',
		dataType 	: 'json',
		beforeSend 	: function(xhr){
	       xhr.setRequestHeader(header,token);
	    },
		success 	: function(data) {
			
			var html = '';
			$.each(data.data, function(idx, obj){
				html += '<div class="swiper-slide">';
				html += '	<button type="button" class="event-bnr" onclick="window.open(\'' + obj.url + '\')">';
				html += '		<img src="' + obj.imageMo + '" alt="' + obj.bannerAlt + '" class="only-mo" />';
				html += '		<img src="' + obj.imagePc + '" alt="' + obj.bannerAlt + '" class="only-pc" />';
				html += '	</button>';
				html += '</div>';
			});
			
			$('#banner-area').html(html);
		}
	});
}