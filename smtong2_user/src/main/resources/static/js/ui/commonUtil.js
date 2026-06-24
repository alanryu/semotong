//============================================  add =========================================

document.addEventListener('DOMContentLoaded', function () {
	// Bootstrap Popover + Custom
	setPopover('ui-popover popover-style1'); // 공통 컴포넌트 규칙 ui- 반영

	if($(".keyword-swiper").length > 0) {
		// 키워드 스와이프
		var keywordSwiper = new Swiper(".keyword-swiper", {
			threshold: 3,
			slidesPerView: 'auto',
			spaceBetween: 8,
			freeMode: true,
			resistance: true,
			resistanceRatio: 0
		});	
	}
	

	// 리뷰 스와이프
	if($(".review-swiper").length > 0) {
		var reviewSwiper = new Swiper(".review-swiper", {
			threshold: 3,
			spaceBetween: 10,
			slidesPerView: 'auto',
		});
	}

	// 파트너사 스와이프
	/*if($(".partner-swiper").length > 0) {
		var partnerSwiper = new Swiper(".partner-swiper", {
			threshold: 3,
			spaceBetween: 10,
			// slidesPerGroup: 4,
			slidesPerView: 'auto',
			loop: true,
			autoplay: {
				delay: 0,
				disableOnInteraction: false,
			},
			speed: 4000, // 1차 수정 : 최대한 느리게 (IOS 멈춤현상)
			freeMode: true,
			allowTouchMove: false, // 터치 스와이프 비활성화
			keyboard: {
				enabled: false, // 키보드 제어 비활성화
			},
		});
	}*/
	
	
	// 이벤트 스와이프
	if($(".event-swiper").length > 0) {
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
	
	// 2025-03-27 조찬기 : 타임딜 활성화UI 조건개선 (스와이프 상태감지)
	function checkScrollable(thisSwiper, swiperSelector) {
		const swiperContainer = document.querySelector(swiperSelector);
		console.log(thisSwiper);
		if (!thisSwiper || thisSwiper.isLocked === undefined) return;
		if (thisSwiper.isLocked) {
			swiperContainer.classList.add("no-scroll");
		} else {
			swiperContainer.classList.remove("no-scroll");
		}
	}
	
	
	const currentUrl = window.location.href; // Get the current URL
    const metaTag = document.querySelector('meta[property="og:url"]');

    if (metaTag) {
       metaTag.setAttribute('content', currentUrl);
    } 
	  
});


// Set Range Multiple
const setRange = () => {
	// 하드코딩버전
	const range1 = document.getElementById('range1');
	const range2 = document.getElementById('range2');
	const rangeValues = document.getElementById('rangeValues');

	// 천단위 콤마 추가 함수
	const formatNumber = (num) => {
		return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
	};

	function updateRange() {
		let val1 = parseInt(range1.value);
		let val2 = parseInt(range2.value);

		// 1000 단위로 반올림
		val1 = Math.round(val1 / 1000) * 1000;
		val2 = Math.round(val2 / 1000) * 1000;

		// 첫 번째 슬라이더가 두 번째 슬라이더를 넘어가지 않도록 처리
		if (val1 > val2) {
			range1.value = val2;
			val1 = val2;
		}

		// 슬라이더 값에 맞춰 배경 색상 변경 (linear-gradient 사용)
		const min = range1.min;
		const max = range1.max;

		const percent1 = (val1 - min) / (max - min) * 100;
		const percent2 = (val2 - min) / (max - min) * 100;

		range1.style.background = `linear-gradient(to right, #CFD0D1 ${percent1}% , #542FDD ${percent1}%, #542FDD ${percent2}%, #CFD0D1 ${percent2}%)`;

		// 전체 범위 값 (최소값과 최대값이 같을 경우)
		const minValue = range1.min;
		const maxValue = range1.max;

		// 값이 전체 범위일 때 '전체'로 표시, 아니라면 값 표시
		if (val1 === parseInt(minValue) && val2 === parseInt(maxValue)) {
			rangeValues.textContent = '전체';
		} else {
			// 천단위 콤마 적용 후 출력
			const formattedValue1 = formatNumber(val1);
			const formattedValue2 = formatNumber(val2);
			rangeValues.textContent = `${formattedValue1}원 ~ ${formattedValue2}원`;
			$('#range1').val(val1);
			$('#range2').val(val2);
		}
	}

	range1.addEventListener('input', updateRange);
	range2.addEventListener('input', updateRange);

	// 초기 범위 업데이트
	updateRange();
}

// Set Tab Swipe
let tabSwiper = null;
const setTabSwipe = () => {
	tabSwiper = new Swiper(".tab-swiper", {
		threshold: 3,
		spaceBetween: 16,
		slidesPerView: 'auto',
		freeMode: true,
		freeModeMomentum: true,
		resistance: true,
		resistanceRatio: 0
	});
}

// Set SpyScoll 탭스와이프 연동
const setSpyScrollActiveLink = () => {
	// spyScroll ID
	const navbarSpyscroll = "#navbarSpyscroll";

	// ScrollSpy에서 active 탭을 찾고 해당 슬라이드로 이동
	function activateSwipeSlide() {
		const activeTab = document.querySelector(navbarSpyscroll+' .active');
		if (activeTab) {
		const tabList = document.querySelectorAll('.tab-link');
		const tabIndex = Array.from(tabList).indexOf(activeTab); // 해당 탭의 인덱스를 찾기

		// Swiper 슬라이드를 해당 인덱스로 이동
		tabSwiper.slideTo(tabIndex);  // 해당 인덱스로 슬라이드 이동
		}
	}

	// Spyscroll이 끝나면 activateSwipeSlide 실행
	const spyScrollEl = document.querySelector('[data-bs-target="'+navbarSpyscroll+'"]');
	let debounceTimer;
	spyScrollEl.addEventListener('scroll', function() {
		clearTimeout(debounceTimer); // 기존 타이머 클리어
		debounceTimer = setTimeout(activateSwipeSlide, 150);  // 150ms 후에 실행
	});
}


function addCommas(number) {
	
	var ret= "";
		if(number !=null && number != '' && !isNaN(number))  {
			if(number  > 999) {
				ret = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");	
			} else {
				ret = number;
			}
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
function getDataStr(dataval,qosval) {
	var result = "";
	
	if(dataval >= 1024) {
		if (dataval % 1024 === 0) {
	        result = Math.floor(dataval / 1024) + "GB";
	      } else {
	        result = (dataval / 1024).toFixed(1) + "GB";
	      }
	} else {
		result = dataval + "MB";
	}
	
	if(qosval>0) {
		if(qosval>1024) {
			result = result + "다 쓰면 최대" + (qosval/1024) + "Mbps";
		} else {
			result = result + "다 쓰면 최대" + qosval + "Kbps";
		}
	}
	
	return result;
}

// 데이터 표시
function getQosStr(qosval) {
	var result = "";
		
	if(qosval >= 1024) {
		result = (qosval/1024).toFixed() + "Mbps";
	} else {
		result = qosval + "Kbps";
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

// 세일기간 표시
function getDiscountStr(period, price) {
	
	return period + " 이후 " + addCommas(price) + "원";
}		

function getDateStr(_val) {
	return _val;
}

function getDateStrDash(_val) {
	var d = new Date(_val),
	month = '' + (d.getMonth() + 1) , 
	day = '' + d.getDate(), 
	year = d.getFullYear();
	if (month.length < 2) month = '0' + month; 
	if (day.length < 2) day = '0' + day; 
	
	return [year, month, day].join('-');
}
	
function getDateStrDot(_val) {
	var d = new Date(_val),
	month = '' + (d.getMonth() + 1) , 
	day = '' + d.getDate(), 
	year = d.getFullYear();
	if (month.length < 2) month = '0' + month; 
	if (day.length < 2) day = '0' + day; 
	
	//return [year, month, day].join('-');
	return [year, month, day].join('.');
}


function calcSumMonPrice(nomalPrice,salePrice,afterPrice,period,monVal){
	nomalPrice = parseInt(nomalPrice);
	salePrice = parseInt(salePrice);
	afterPrice = parseInt(afterPrice);
	period = parseInt(period);
	monVal = parseInt(monVal);
	
	var calcVal = 0;
	if(period > 0) {
		if(monVal == 12) {
			if(period > 12) {
				calcVal = nomalPrice * 12;
			} else {
				var diffMon = 12 - period;
				calcVal = (salePrice * period) +(nomalPrice * diffMon) ;
			}
		} else {
			if(period > 24) {
				calcVal = nomalPrice * 24;
			} else {
				var diffMon = 24 - period;
				calcVal = (salePrice * period) +(nomalPrice * diffMon) ;
			}
		}
	} else {
		if(monVal == "12") {
			calcVal = nomalPrice * 12;
		} else {
			calcVal = nomalPrice * 24;
		}
	}
	return calcVal;
}


function windowOpen(url) {
	window.open(url);
}


function formatDate(dateString) {
	
	if(dateString ==null) {
		return dateString;
	}
	if(dateString.length < 4) {
		return dateString;
	}
    // 연도, 월, 일을 각각 추출
    const year = dateString.slice(0, 4);   // "2025"
    const month = dateString.slice(4, 6); // "03"
    const day = dateString.slice(6, 8);   // "12"

    // 원하는 형식으로 조합
    return `${year}.${month}.${day}`;
}



var kakaoShrePlanId,kakaoShrePlanNm,kakaoShresalePrice,kakaoShreHostNm

function kakaoInit(planId,planNm,salePrice,hostNm) {
	
	kakaoShrePlanId		= planId;
	kakaoShrePlanNm		= planNm;
	kakaoShresalePrice	= salePrice;
	kakaoShreHostNm		= hostNm;
	
	if (!Kakao.isInitialized()) {
		
		Kakao.init('b5f3d641ba278ee08c517536874d19a9');
	}
}

function createKakaoBtnMain() {
	
	if (!Kakao.isInitialized()) {
		Kakao.init('b5f3d641ba278ee08c517536874d19a9');
	}
	
	var nowUrl = window.location.protocol + '//' + window.location.host;
	
	if (Kakao.isInitialized()) {
		Kakao.Link.sendDefault({
			objectType: 'feed'
			,content: {
				 title			: '세모통 타임딜'
				,imageUrl		:window.location.protocol + '//' + window.location.host + '/images/contents/KakaoTalk_share_img.png'
				,link: {
					 mobileWebUrl	: nowUrl
					,webUrl			: nowUrl
				}
			}
			,buttons: [
				{
					title: '웹으로 보기',
					link: {
						mobileWebUrl	: nowUrl
						,webUrl			: nowUrl
					}
				}
			]
		});
	}
}

function createKakaoBtn() {
	
	var path = 'mvno';
	if ( kakaoShreHostNm == 'SKT' || kakaoShreHostNm == 'KT' || kakaoShreHostNm == 'LGU' ) {
		path = 'mno';
	}
	
	var nowUrl = window.location.protocol + '//' + window.location.host + '/pbm/plan/planDetail/' + path + '?planid=' + kakaoShrePlanId; 
	
	if (Kakao.isInitialized()) {
		
		 Kakao.Link.sendDefault({
			//container: '#kakaoShareBtn'
			objectType: 'feed'
			,content: {
				title			: kakaoShrePlanNm
				,description	: getPriceStr(kakaoShresalePrice) +  ' '+kakaoShrePlanNm + ' '+ kakaoShreHostNm
				,imageUrl		:window.location.protocol + '//' + window.location.host + '/images/contents/KakaoTalk_share_img.png'
				,link: {
					//mobileWebUrl	: 'https://tb.smtong.co.kr/pbm/plan/planDetail?planid='+kakaoShrePlanId,			// [내 애플리케이션] > [플랫폼] 에서 등록한 사이트 도메인과 일치해야 함
					//webUrl			: 'https://tb.smtong.co.kr/pbm/plan/planDetail?planid='+kakaoShrePlanId
					mobileWebUrl	: nowUrl
					,webUrl			: nowUrl
					
				}
			}
			/*,commerce: {
				productName: '세모통',
				regularPrice: p1n,
				discountRate: 10,
				discountPrice: p2n
			}*/
			,buttons: [
				{
					title: '웹으로 보기',
					link: {
						//mobileWebUrl: 'https://tb.smtong.co.kr/pbm/plan/planDetail?planid='+kakaoShrePlanId,
						//webUrl: 'https://tb.smtong.co.kr/pbm/plan/planDetail?planid='+kakaoShrePlanId,
						mobileWebUrl	: nowUrl
						,webUrl			: nowUrl
					}
				}
				/*,{
					title: '앱으로 보기',
					link: {
						//mobileWebUrl: 'https://tb.smtong.co.kr/pbm/plan/planDetail?planid='+kakaoShrePlanId,
						//webUrl: 'https://tb.smtong.co.kr/pbm/plan/planDetail?planid='+kakaoShrePlanId,
						mobileWebUrl	: nowUrl
						,webUrl			: nowUrl
					}
				}*/
			]
		});
	}
}

var kakaoShreTitle, kakaoShreDesc, kakaoShreLink

function kakaoLinkInit(title, desc, nowurl) {
	//alert(nowurl);	//OK
	kakaoShreTitle		= title;
	kakaoShreDesc		= desc;
	kakaoShreLink		= nowurl;
	
	if (!Kakao.isInitialized()) {
		
		Kakao.init('b5f3d641ba278ee08c517536874d19a9');
	}
}

function createKakaoBtnLink() {
	
	//var nowUrl = window.location.protocol + '//' + window.location.host + '/pbm/plan/planDetail/' + path + '?planid=' + kakaoShrePlanId;
	var nowUrl = kakaoShreLink;
	
	if (Kakao.isInitialized()) {
		
		 Kakao.Link.sendDefault({
			//container: '#kakaoShareBtn'
			objectType: 'feed'
			,content: {
				title			: kakaoShreTitle
				,description	: kakaoShreDesc
				,imageUrl		:window.location.protocol + '//' + window.location.host + '/images/contents/smtong_og_image_event.png'
				,imageWidth : 1200
				,imageHeight : 630
				,link: {
					mobileWebUrl	: kakaoShreLink
					,webUrl			: kakaoShreLink
					
				}
			}
			,buttons: [
				{
					title: '웹으로 보기',
					link: {
						mobileWebUrl	: kakaoShreLink
						,webUrl			: kakaoShreLink
					}
				}
			]
		});
	}
}





function urlCopyBtn(type){
	
	var path = 'mvno';
	if ( kakaoShreHostNm == 'SKT' || kakaoShreHostNm == 'KT' || kakaoShreHostNm == 'LGU' ) {
		path = 'mno';
	}
	
	$("#closeShareModal").trigger("click");
	$("#shareModal").blur();	//focus 제거
	$("#urlCopyBtnId").blur();
	
	var nowHost = window.location.protocol + '//' + window.location.host;
	if ( type == null ) {
		nowHost += '/pbm/plan/planDetail/' + path + '?planid=' + kakaoShrePlanId;
	}
	navigator.clipboard.writeText(nowHost).then(res=>{
		alertModalShow("주소가 복사 되었습니다.", "");
	})
}


function event_urlCopyBtn() {
	// 공유 모달 닫기
	$('#shareLinkModal').modal('hide');

    // 현재 페이지의 URL 가져오기
    var currentUrl = window.location.href;

    // 클립보드에 복사
    navigator.clipboard.writeText(currentUrl).then(() => {
        alertModalShow("주소가 복사 되었습니다.", "");
    }).catch(err => {
        console.error('URL 복사 실패:', err);
    });
}


//tit:굵은글씨 제목, 조금얇은글씨 내용
function alertModalShow(txt, tit,confirmCallback,size){ 
	
	const modalEl 	= document.getElementById('alertModal');
	const modalBtn	= document.getElementById('alertModalCloseId');
	$("#alertModal .sec-tit").html(tit);
	$("#alertModal .sec-txt").html(txt);
	
	
	if(tit =="" || tit == null) {
		$("#alertModal .sec-tit").hide();
		$("#alertModal .sec-txt").css('margin-top', '0px'); 
	} else {
		$("#alertModal .sec-tit").show();
		$("#alertModal .sec-txt").css('margin-top', '24rem');	
	}
	
	var alertModal = new bootstrap.Modal(modalEl);
	alertModal.show();
	
	
	if(size !=null) {
		$('#alertModal .modal-content').css("width",size+"rem");
	}
	
	// hidden - focus error 대응
	modalBtn.addEventListener('click', () => {
		modalBtn.blur();
	});
	
	// 모달이 닫힐 때 메시지 초기화 (옵션)
	modalEl.addEventListener('hidden.bs.modal', function () {
		$("#alertModal .sec-tit").text("");
		if(confirmCallback !=null) {
			if (typeof confirmCallback === 'function') {
				confirmCallback();
			} else if (typeof confirmCallback === 'object') {
				confirmCallback.focus();
			}
		}
	});
}


function clickZzim(planId,element) {
	
	var params = {
		prodId : planId
	}
	
	var url = "";
	
	if( !$(element).hasClass('is-selected') ) {
		url = '/pmb/plan_zzim/create';
	} else {
		url = '/pmb/plan_zzim/delete';
	}
		
	
	$.ajax({
	   url : url,
	   type : 'POST',
	   dataType : "json",
	   contentType:"application/json",
	   data : JSON.stringify(params),
	   beforeSend: function(xhr){
		   xhr.setRequestHeader(header,token);
		},
		success: function (data) {
			
			$(element).toggleClass('is-selected');
		},
		error: function(xhr, status, error) {
			if (xhr.status !== 200) {
				let response = xhr.responseJSON;  // JSON 응답을 파싱
				if (response && response.message) {
					if(xhr.status ==422) {
						alertModalShow(response.message, "",goLogin);
					} else {
						alertModalShow(response.message, "");
					}
				} else {
					alertModalShow('Unexpected error occurred.');
				}
			}
		},
		complete:function(){
		   // $('#loading').addClass('display-none');
		}
	});
}

function goMain() {
	window.location.href="/";
}


function goLogin() {
	window.location.href="/users/login";
}

function setCookie(name, value, exp) {
	var date = new Date();
	date.setTime(date.getTime() + exp * 24 * 60 * 60 * 1000);
	document.cookie = name + '=' + escape(value) + ';expires=' + date.toUTCString() + ';path=/';
}
function getCookie(name) {
	var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	return value ? unescape(value[2]) : null;
}
function resetCookie(cName) {
	var expireDate = new Date();
	expireDate.setDate(expireDate.getDate() - 1);
	document.cookie = cName + "= " + "; expires=" + expireDate.toGMTString() + "; path=/";
}



function maskName(name) {
	if (name.length === 1) {
		return name;
	} else if (name.length === 2) {
		return name.slice(0, 1) + '*';
	} else {
		return name.slice(0, 1) + '*'.repeat(name.length - 2) + name.slice(-1)
	}
}


function chkInputMax(el,nextel) {
	// 현재 입력 박스에서 최대 길이에 도달했는지 확인
	    if ($(el).val().length == $(el).attr('maxlength')) {
	      // 다음 입력 박스로 포커스 이동
	       $("#"+nextel).focus();
	    }
}



function inputTelNo(obj) {
	let value = obj.value.replace(/[^0-9]/g, ""); // 숫자만 남기기
	console.log(value);
      if (value.length > 3 && value.length <= 7) {
        value = value.replace(/(\d{3})(\d+)/, "$1-$2");
      } else if (value.length > 7) {
        value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
      }
	  obj.value = value;
}


//주민번호로 생년월일 추출
function getBirthDateFromRRN(rrn) {
    // rrn: 주민등록번호 문자열 (예: "9201231234567")
    if (rrn.length !== 13) {
        console.error("Invalid RRN length");
        return null;
    }

    // Extract parts of RRN
    const year = rrn.slice(0, 2);      // YY
    const month = rrn.slice(2, 4);    // MM
    const day = rrn.slice(4, 6);      // DD
    const genderCode = rrn[6];        // First digit of the second part

    // Determine the full year
    let fullYear;
    if (genderCode === "1" || genderCode === "2") {
        fullYear = `19${year}`; // 1900s
    } else if (genderCode === "3" || genderCode === "4") {
        fullYear = `20${year}`; // 2000s
    } else if (genderCode === "5" || genderCode === "6") {
        fullYear = `19${year}`; // Foreigners born in 1900s
    } else if (genderCode === "7" || genderCode === "8") {
        fullYear = `20${year}`; // Foreigners born in 2000s
    } else {
        console.error("Invalid gender code");
        return null;
    }

    // Return formatted date
    return `${fullYear}${month}${day}`;
}



// 함수: 쿠키에 값을 저장
function setCookie(name, value, days) {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    document.cookie = `${name}=${encodeURIComponent(value)};expires=${date.toUTCString()};path=/`;
}

// 함수: 쿠키에서 값을 가져오기
function getCookie(name) {
    const cookies = document.cookie.split('; ');
    for (let i = 0; i < cookies.length; i++) {
        const [key, value] = cookies[i].split('=');
        if (key === name) return decodeURIComponent(value);
    }
    return null;
}


function deleteCookie(name) {
    // 해당 쿠키 이름을 삭제하도록 설정
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/`;
    console.log(`쿠키 '${name}'가 삭제되었습니다.`);
}



// Confirm 모달 함수
const showConfirm = (message, confirmCallback, cancelCallback, title = '확인') => {
	const modalEl 		= document.getElementById('confirmModal');
	const messageEl 	= document.getElementById('commonConfirmMessage');
	const titleEl 		= document.getElementById('confirmModal_Title'); 
	const confirmBtn 	= document.getElementById('confirmModalConfirmBtn');
	const cancelBtn 	= document.getElementById('confirmModalCancelBtn');

	messageEl.innerHTML = message;
	titleEl.textContent = title;  // title 설정

	const modal = new bootstrap.Modal(modalEl);
	const newConfirmBtn = confirmBtn.cloneNode(true);
	const newCancelBtn = cancelBtn.cloneNode(true);
	confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
	cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);

	newConfirmBtn.addEventListener('click', () => {
		modal.hide();
		if (typeof confirmCallback === 'function') {
			confirmCallback();
		}
		$('#confirmModalConfirmBtn').blur();
		newConfirmBtn.blur();
	});
	newCancelBtn.addEventListener('click', () => {
		modal.hide();
		if (typeof cancelCallback === 'function') {
			cancelCallback();
		}
		$('#confirmModalCancelBtn').blur();
		newCancelBtn.blur();
	});

	modalEl.addEventListener('hidden.bs.modal', function () {
		messageEl.innerHTML = '';
		titleEl.textContent = '확인';  // 모달이 닫힐 때 title 초기화
	});
	modal.show();
};