document.addEventListener("DOMContentLoaded", () => {
  const downloadIcon = document.querySelector(".fas.fa-download");
  if (downloadIcon) {
    const parentButton = downloadIcon.closest(".btn.btn-light.btn-sm");
    if (parentButton) {
      // 클릭 리스너 추가
      parentButton.addEventListener("click", () => {
        downloadExcel();
      });
    }
  }

  const downloadIconS = document.querySelector(".fas.fa-file-excel");
  if (downloadIconS) {
    const parentButtonS = downloadIconS.closest(".btn.btn-success.btn-sm");
    if (parentButtonS) {
      // 클릭 리스너 추가
      parentButtonS.addEventListener("click", () => {
        downloadExcel();
      });
    }
  }

  setTimeout(() => {
    if ($(".numberonly").length) {
      $(".numberonly").on("keyup", function () {
        $(this).val(
          $(this)
            .val()
            .replace(/[^0-9]/g, "")
        ); // 숫자 외 문자 제거
      });
    }

    if ($(".phonnumClass").length) {
      $(".phonnumClass").on("keyup", function () {
        // 현재 입력값에서 숫자만 남김
        let value = $(this)
          .val()
          .replace(/[^0-9]/g, "");

        // 전화번호 포맷 적용
        if (value.length < 4) {
          // 4자리 미만: 그대로 유지
          $(this).val(value);
        } else if (value.length < 7) {
          // 4~6자리: 000-000 형식
          $(this).val(value.replace(/(\d{3})(\d+)/, "$1-$2"));
        } else if (value.length < 11) {
          // 7~10자리: 000-0000-0000 형식
          $(this).val(value.replace(/(\d{3})(\d{3})(\d+)/, "$1-$2-$3"));
        } else {
          // 11자리 이상: 000-0000-0000 형식 유지
          $(this).val(value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3"));
        }
      });
    }
  }, 1000); // 최소값 0.3초 후 실행 (추가 delay는 css로 조절하겟습니다.)
});

function downloadExcelProc(params, _url, dowloadFileNM) {
  fetch(_url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(params),
  })
    .then((response) => {
      if (response.ok) {
        return response.blob();
      } else {
        throw new Error("Failed to download file");
      }
    })
    .then((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = dowloadFileNM + ".xlsx";
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    })
    .catch((error) => console.error("Error:", error));
}

function downloadFileProc(orgFileNm, sysFileNm) {
  const encodedFileName = encodeURIComponent(sysFileNm);
  const url = "/cmm/filemng/getDocument/" + encodedFileName;
  fetch(url)
    .then((response) => {
      if (!response.ok) {
        // 👇 서버 응답이 실패하면 예외 발생
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.blob();
    })
    .then((blob) => {
      if (blob.size < 1024) {
        // 👇 파일 크기가 너무 작으면 오류 처리 (예: 1KB 이하)
        throw new Error("Downloaded file is too small. Possible error response.");
      }
      const downloadUrl = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = downloadUrl;
      a.download = orgFileNm; // 다운로드될 파일 이름
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(downloadUrl);
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("파일 다운로드 실패: ${error.message}");
    });
}

function addCommas(number) {
  var ret = "";

  if (number != null && number != "" && !isNaN(number)) {
    ret = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  } else {
    ret = number;
  }
  return ret;
}

// ================== 요금제 데이터 표시용=====================

// 요금제 명(세모통 요금제명이 있는 경우 세모통 요금제 명 아닌 경우 스크래핑 요금제명)
function getPlanNm(orgPlanNm, smtPlanNm) {
  var result = "";

  if (smtPlanNm == "" || smtPlanNm == null) {
    result = orgPlanNm;
  } else {
    result = smtPlanNm;
  }
  return result;
}

// 데이터 표시
function getDataStr(dataval) {
  var result = "";
  if (dataval >= 1024) {
    if (dataval == 10238976) {
      result = "무제한";
    } else {
      if (dataval % 1024 === 0) {
        result = Math.floor(dataval / 1024) + "GB";
      } else {
        result = (dataval / 1024).toFixed(1) + "GB";
      }
    }
  } else {
    result = dataval + "MB";
  }
  return result;
}

// 데이터 표시
function getQosStr(qosval) {
  var result = "";

  if (qosval >= 1024) {
    result = qosval / 1024 + "Mbps";
  } else {
    result = qosval + "Kbps";
  }

  return result;
}

// sms 표시
function getSmsStr(val) {
  var result = "";
  if (val == 9999) {
    result = "무제한";
  } else {
    result = val + "건";
  }
  return result;
}

// 음성 표시
function getCallStr(val) {
  var result = "";
  if (val == 9999) {
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
  return period + "개월 이후" + addCommas(val);
}

function getDateStr(_val) {
	if (_val == null || _val == "") return ""; 
  _val = _val.replace("T", " ");
  return _val;
}

function getDateStrYYYYmmdd(_val) {
  if (_val) {
    if (_val.includes("T")) {
      return _val.split("T")[0];
    } else if (_val.includes(" ")) {
      return _val.split(" ")[0];
    }

    return _val;
  }
  return "";
}

// 링크 url 가져오기
function getLinkUrl(linkurl, smtlink) {
  var res = linkurl;
  if (smtlink != "" && smtlink != null && smtlink != "null") {
    res = smtlink;
  }
  return res;
}

function calcSumMonPrice(nomalPrice, salePrice, afterPrice, period, monVal) {
  nomalPrice = parseInt(nomalPrice);
  salePrice = parseInt(salePrice);
  afterPrice = parseInt(afterPrice);
  period = parseInt(period);
  monVal = parseInt(monVal);

  var calcVal = 0;
  if (period > 0) {
    if (monVal == 12) {
      if (period > 12) {
        calcVal = nomalPrice * 12;
      } else {
        var diffMon = 12 - period;
        calcVal = salePrice * period + nomalPrice * diffMon;
      }
    } else {
      if (period > 24) {
        calcVal = nomalPrice * 24;
      } else {
        var diffMon = 24 - period;
        calcVal = salePrice * period + nomalPrice * diffMon;
      }
    }
  } else {
    if (monVal == "12") {
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

function addDate(addVal) {
  let date = new Date();
  date.setDate(date.getDate() + addVal);
  return formatDate(date);
}

function getToday() {
  let date = new Date();
  return formatDate(date);
}

function formatDate(date) {
  (month = "" + (date.getMonth() + 1)), (day = "" + date.getDate()), (year = date.getFullYear());

  if (month.length < 2) month = "0" + month;
  if (day.length < 2) day = "0" + day;

  return [year, month, day].join("-");
}

function formatDateTime(dateTimeString) {
  // 연도, 월, 일, 시간, 분, 초를 추출
  const year = dateTimeString.slice(0, 4);
  const month = dateTimeString.slice(4, 6);
  const day = dateTimeString.slice(6, 8);
  const hour = dateTimeString.slice(8, 10);
  const minute = dateTimeString.slice(10, 12);

  // 원하는 형식으로 조합
  return `${year}.${month}.${day} ${hour}:${minute}`;
}

function formatPhoneNumber(phoneNumber) {
  // 정규식을 이용해 핸드폰 번호를 그룹화하여 형식 변경
  return phoneNumber.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
}

//============ 페이징 처리
var currentPage = 1; // 초기 페이지 번호
function renderPagination(page, _totalCount, itemsPerPage) {
  var paginationHtml = "";
  var totalPage = Math.ceil(_totalCount / itemsPerPage);
  var pageGroup = Math.ceil(currentPage / 10);

  var last = pageGroup * 10;
  if (last > totalPage) last = totalPage;
  var first = last - (10 - 1) <= 0 ? 1 : last - (10 - 1);
  var next = last + 1;
  var prev = first - 1;

  // 첫 번째 페이지와 이전 페이지 버튼

  if (prev > 0) {
    paginationHtml +=
      '<li class="page-item"><a class="page-link" href="javascript:;" data-page="1"><i class="fas fa-angle-double-left"></i></a></li>';
    paginationHtml +=
      '<li class="page-item"><a class="page-link" href="javascript:;" data-page="' +
      (page - 1) +
      '"><i class="fas fa-angle-left"></i></a></li>';
  }

  // 페이지 번호 버튼
  for (var i = first; i <= last; i++) {
    if (currentPage == i) {
      paginationHtml +=
        '<li class="page-item active"><a class="page-link" href="javascript:;"  data-page="' +
        i +
        '">' +
        i +
        "</a></li>";
    } else {
      paginationHtml +=
        '<li class="page-item"><a class="page-link" href="javascript:;"  data-page="' + i + '">' + i + "</a></li>";
    }
  }

  // 마지막 페이지와 다음 페이지 버튼
  if (last < totalPage) {
    paginationHtml +=
      '<li class="page-item"><a class="page-link" href="javascript:;" data-page="' +
      (page + 1) +
      '"><i class="fas fa-angle-right"></i></a></li>';
    paginationHtml +=
      '		<li class="page-item"><a class="page-link" href="javascript:;" data-page="' +
      totalPage +
      '"><i class="fas fa-angle-double-right"></i></a></li>';
  }

  // 페이징 버튼 출력
  $(".pagination").html(paginationHtml);
  setPagenationClick();

  if (parseInt(_totalCount) < parseInt(itemsPerPage)) {
    $(".pagination").hide();
  } else {
    $(".pagination").show();
  }
}

function setPagenationClick() {
  $(".pagination").off("click");
  $(".pagination").on("click", ".page-link", function () {
    var page = $(this).data("page");
    currentPage = page;
    renderPage(page);
  });
}

function getCheckedCompanies(chkboxName) {
  var checkedValues = $("input[name='" + chkboxName + "']:checked")
    .map(function () {
      return $(this).val(); // 체크된 값 반환
    })
    .get() // 배열로 변환
    .join(","); // ,로 구분된 문자열로 결합

  return checkedValues;
}

let toastEditor;
function initToastEditor(targetId, contentFieldId) {
  toastEditor = new toastui.Editor({
    el: document.querySelector(`#${targetId}`),
    height: "400px",
    initialEditType: "wysiwyg", // 또는 'markdown'
    previewStyle: "vertical", // 또는 'tab'
    placeholder: "",
    initialValue: document.getElementById(contentFieldId)?.value || "",
    plugins: [[toastui.Editor.plugin.colorSyntax]],

    hooks: {
      addImageBlobHook: async (blob, callback) => {
        try {
          const imageUrl = await uploadImageToServer(blob);

          callback(imageUrl, blob.name || "image");
        } catch (error) {
          console.error("Image upload failed:", error);
          callback("", ""); // 실패 시 빈 값 전달
        }
      },
    },

    // 툴바 설정 (TinyMCE와 유사하게)
    toolbarItems: [
      ["heading", "bold", "italic", "strike"],
      ["hr", "quote"],
      ["ul", "ol", "task", "indent", "outdent"],
      ["table", "image", "link"],
      ["code", "codeblock"],
      ["scrollSync"],
    ],
  });
}

// 기존 TinyMCE 이미지 업로드 핸들러를 Toast UI Editor용으로 변환
const uploadImageToServer = (blob) => {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.withCredentials = false;
    xhr.open("POST", "/cmm/editor/attach"); // 동일한 엔드포인트 사용

    // 성공 처리
    xhr.onload = () => {
      if (xhr.status === 403) {
        reject({ message: "HTTP Error: " + xhr.status, remove: true });
        return;
      }

      if (xhr.status < 200 || xhr.status >= 300) {
        reject("HTTP Error: " + xhr.status);
        return;
      }

      try {
        const json = JSON.parse(xhr.responseText);

        if (!json || typeof json.location !== "string") {
          reject("Invalid JSON: " + xhr.responseText);
          return;
        }
        resolve(json.location); // 이미지 URL 반환
      } catch (e) {
        reject("Error parsing response: " + e.message);
      }
    };

    // 에러 처리
    xhr.onerror = () => {
      reject("Image upload failed due to a XHR Transport error. Code: " + xhr.status);
    };

    // FormData 생성 및 파일 추가
    const formData = new FormData();
    formData.append("attach", blob, blob.name || "image.png");

    // 서버로 전송
    xhr.send(formData);
  });
};

var tinymce;
function initTinyEditor(targer) {
  tinymce.init({
    selector: "#" + targer,
    content_style: `
		    body { font-family: 'Pretendard' !important; }
		`,
    font_family_formats:
      "Pretendard=Pretendard,sans-serif;맑은고딕=Malgun Gothic;나눔스퀘어=NanumSquare;Arial=arial,helvetica,sans-serif; Courier New=courier new,courier,monospace;",
    language: "ko_KR",
    plugins: [
      "anchor",
      "autolink",
      "charmap",
      "emoticons",
      "image",
      "link",
      "lists",
      "searchreplace",
      "table",
      "visualblocks",
      "wordcount",
      "code",
      "preview",
    ],
    toolbar:
      "undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image table | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat code preview",
    paste_data_images: true, // 이미지 붙여넣기 설정 활성화
    automatic_uploads: true,
    relative_urls: false,
    images_upload_handler: fn_image_upload_handler,
    init_instance_callback: function (editor) {
      customInitEditor(editor, targer);
    },
  });
}

const fn_image_upload_handler = (blobInfo, progress) =>
  new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.withCredentials = false; // Disable credentials
    xhr.open("POST", "/cmm/editor/attach"); // Updated to Spring endpoint

    // Monitor upload progress
    xhr.upload.onprogress = (e) => {
      if (e.lengthComputable) {
        progress((e.loaded / e.total) * 100); // Update progress as percentage
      }
    };

    // Handle successful upload
    xhr.onload = () => {
      if (xhr.status === 403) {
        reject({ message: "HTTP Error: " + xhr.status, remove: true });
        return;
      }

      if (xhr.status < 200 || xhr.status >= 300) {
        reject("HTTP Error: " + xhr.status);
        return;
      }

      try {
        const json = JSON.parse(xhr.responseText);

        if (!json || typeof json.location !== "string") {
          reject("Invalid JSON: " + xhr.responseText);
          return;
        }
        resolve(json.location); // Resolve with the image location URL
      } catch (e) {
        reject("Error parsing response: " + e.message);
      }
    };

    // Handle errors during the upload process
    xhr.onerror = () => {
      reject("Image upload failed due to a XHR Transport error. Code: " + xhr.status);
    };

    // Create FormData and append the file to it
    const formData = new FormData();
    formData.append("attach", blobInfo.blob(), blobInfo.filename());

    // Send the form data to the Spring server
    xhr.send(formData);
  });

function dateToStringDot(date) {
  var str = "";
  if (date != null) {
    var temp = moment(date);
    str = temp.format("YYYY.MM.DD");
  }
  return str;
}

function dateToStringDash(date) {
  var str = "";
  if (date != null) {
    var temp = moment(date);
    str = temp.format("YYYY-MM-DD");
  }
  return str;
}

function strDttmToStringDash(strDttm) {
  if (!strDttm || strDttm.length !== 14) return strDttm; // 유효성 검사

  let year = strDttm.substring(0, 4);
  let month = strDttm.substring(4, 6);
  let day = strDttm.substring(6, 8);
  let hour = strDttm.substring(8, 10);
  let minute = strDttm.substring(10, 12);
  let second = strDttm.substring(12, 14);

  return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}

/** 날짜 포멧 양식 확인
 *  if( !_isDateForm($("#searchStartDt").val()) ){
 		alert("올바른 날짜를 넣어주세요");
 		$("#searchStartDt").focus();
 		return;
 	}
 */
function _isDateForm(vDate) {
  return !isNaN(Date.parse(vDate));
}

function validatePassword(password) {
  // 정규식 패턴 정의
  const lengthPattern = /.{5,}/; // 최소 5글자 이상
  const numberPattern = /\d/; // 숫자 포함
  const letterPattern = /[a-zA-Z]/; // 영문 포함
  const specialCharPattern = /[!@#$%^&*(),.?":{}|<>]/; // 특수문자 포함

  // 각 조건을 검사
  if (!lengthPattern.test(password)) {
    return "비밀번호는 최소 5글자 이상이어야 합니다.";
  }

  if (!numberPattern.test(password)) {
    return "비밀번호에 숫자가 포함되어야 합니다.";
  }

  if (!letterPattern.test(password)) {
    return "비밀번호에 영문이 포함되어야 합니다.";
  }

  if (!specialCharPattern.test(password)) {
    return "비밀번호에 특수문자가 포함되어야 합니다.";
  }

  return "sucess";
}

/**
 * 전화번호 마스킹 함수
 * @param {string} phoneNumber - 마스킹할 전화번호
 * @returns {string} - 마스킹된 전화번호
 */
function maskPhoneNumber(phoneNumber) {
  if (phoneNumber == "" || phoneNumber == "null" || phoneNumber == null) return "";

  phoneNumber = phoneNumber.replaceAll("-","");;
 
  // 숫자만 추출
  var cleaned = phoneNumber.replace(/\D/g, "");

  if (cleaned.startsWith("82")) cleaned = "0" + cleaned.substring(2);

  // 숫자의 길이가 10자리 또는 11자리인 경우만 처리
  if (cleaned.length === 10) {
	console.log(phoneNumber);
	 
    // 예: 010-1234-5678 -> 010-****-5678
    return cleaned.replace(/(\d{3})(\d{3})(\d{4})/, "$1-****-$3");
  } else if (cleaned.length === 11) {
    // 예: 010-1234-5678 -> 010-****-5678
    return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, "$1-****-$3");
  } else if (cleaned.length === 16) {
    // 예: +82 10-2674-0126 -> 010-****-5678
    return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, "$1-****-$3");
  }

  // 처리 불가능한 경우 원본 반환
  return phoneNumber;
}



/**
 * 이름 마스킹 함수
 * @param {string} name - 마스킹할 이름
 * @returns {string} - 마스킹된 이름
 */
function maskName(name) {
  if (name == "" || name == null || name == "null") {
    // 이름이 한 글자인 경우 그대로 반환
    return name;
  }

  if (name.length <= 1) {
    // 이름이 한 글자인 경우 그대로 반환
    return name;
  }

  if (name.length === 2) {
    // 이름이 두 글자인 경우 첫 글자만 남기고 마스킹
    return name[0] + "*";
  }

  // 이름이 세 글자 이상인 경우 첫 글자와 마지막 글자만 남기고 나머지를 마스킹
  return name[0] + "*".repeat(name.length - 2) + name[name.length - 1];
}

function truncateText(str, maxLength) {
  if (str == "" || str == null) {
    return "";
  }

  if (str.length > maxLength) {
    return str.substr(0, maxLength) + "...";
  }
  return str;
}

function getBenefitProvidor(val) {
  var provider = "없음";

  if (val == "telecom") {
    provider = "통신사 혜택";
  } else if (val == "semo") {
    provider = "세모통 혜택";
  } else if (val == "internet") {
    provider = "인터넷 혜택";
  }
  return provider;
}
