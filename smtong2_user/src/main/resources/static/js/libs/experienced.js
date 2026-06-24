/**
 * 알뜰요금제 사용 경험자 관련 동작설정
 */
$(document).ready(function(){
	
	/* 전체 보기 */
	var params = new Object();
	
	/*$(document).on('click', '.card-charge-more', function(){
		
		var confirmModal = new bootstrap.Modal(document.getElementById('confirmModal-3'));
		confirmModal.show();
		
		params.mno = $(this).children('input[name=mno]').val();
		params.supDataVal = $(this).children('input[name=supDataVal]').val(); 
		params.supQos = $(this).children('input[name=supQos]').val()
	});
	
	 전체 보기(예)
	$('#go-list').on('click', function(){
		
		var queryStr = new URLSearchParams(params).toString();
		$(location).attr('href','/pbm/plan/planList?' + queryStr);
	});*/
})

function extandHandler(button, id) {
	// 'cardListExtend01' 요소 가져오기
	var ulElement = document.getElementById(id);
	
	// is-expanded 클래스 토글
	ulElement.classList.toggle('is-expanded');
	
	// 'this'의 자식 span 요소 텍스트 토글
	var spanElement = button.querySelector('span');
	if (ulElement.classList.contains('is-expanded')) {
		spanElement.textContent = '닫기';  // '전체' -> '닫기'
	} else {
		spanElement.textContent = '더보기';  // '닫기' -> '더보기'
	}
}

/* 요금제 상세보기 */
function goPlanDetail(planid, hostNm) {
	
	var path = 'mvno';
	
	if ( hostNm == 'SKT' || hostNm == 'KT' || hostNm == 'U+' ) {
		path = 'mno';
	}
	
	window.open('/pbm/plan/planDetail/' + path + '?planid=' + planid);
}