// 탭 메뉴 초기화 함수
const initTabMenu = () => {
  const tabMenus = document.querySelectorAll(".tab-menu");

  if (!tabMenus.length) return; // 탭 메뉴가 없으면 실행하지 않음

  tabMenus.forEach((menu) => {
    const tabs = menu.querySelectorAll("li");

    // 탭 클릭 이벤트 핸들러
    const handleTabClick = (clickedTab) => {
      tabs.forEach((tab) => tab.classList.remove("active"));
      clickedTab.classList.add("active");
    };

    // 각 탭에 이벤트 리스너 추가
    tabs.forEach((tab) => {
      tab.addEventListener("click", () => handleTabClick(tab));
    });
  });
};

// DOM이 로드된 후 초기화
document.addEventListener("DOMContentLoaded", initTabMenu);

// 동적으로 탭이 추가될 경우를 위한 재초기화 함수 노출
window.reinitTabMenu = initTabMenu;

function periodButtonInit() {
  var loc = location.pathname;
  if (loc == "/pbm/plan/list") {
    $('.period-button[value="30"]').trigger("click");
  } else {
    $('.period-button[value="180"]').trigger("click");
  }
}

// 페이지 로드 시 초기화
function initializePage() {
  try {
    const display = document.querySelector(".timer");
    if (display) {
      const threeMinutes = 60 * 3;
      startTimer(threeMinutes, display);
    }

    handlePeriodButtonStyle();
    handlePeriodButtonStyleSearch();
    handlePeriodButtonStyleCreate();
    handleCheckboxAllSelect();
    handleSubmenuToggle();
    sidebarToggle();
    periodButtonInit();
    //if($(".period-button").length > 1) {
    //$(".period-button")[$(".period-button").length - 1].click()
    //}
  } catch (error) {
    console.error("Error initializing page:", error);
  }
}

// DOMContentLoaded 이벤트를 사용하여 더 안전하게 초기화
document.addEventListener("DOMContentLoaded", initializePage);

/* ******************************************************** */

// 기간 버튼 스타일 핸들러
// function handlePeriodButtonStyle() {
//   const periodButtons = document.querySelectorAll('.period-button');

//   if (!periodButtons.length) {
//     console.log('Period buttons not found');
//     return;
//   }

//   periodButtons.forEach(button => {
// 	button.addEventListener('click', () => {
//       try {
//         periodButtons.forEach(btn => {
//           btn.style.background = 'white';
//           btn.style.borderColor = '#e2e8f0';
//           btn.style.color = '#64748b';

//         });

//         button.style.background = "var(--menu-active)";
//         button.style.borderColor = "var(--primary-color)";
//         button.style.color = "var(--primary-color)";
// 		if ( !button.classList.contains('month-btn') ) {
// 			var periodVal = $(button).val();
// 			if($('#searchEndDt').length){
// 				$('#searchEndDt').val(getToday());
// 			}

// 			if($('#searchStartDt').length){
// 				periodVal = periodVal * -1;
// 				$('#searchStartDt').val(addDate(periodVal));
// 			}
// 		}
//       } catch (error) {
//         console.error('Error handling period button style:', error);
//       }

//     });
//   });
// }

function handlePeriodButtonStyle() {
  const periodButtons = document.querySelectorAll(".period-button");

  if (!periodButtons.length) {
    console.log("Period buttons not found");
    return;
  }

  periodButtons.forEach((button) => {
    button.addEventListener("click", () => {
      try {
        // data-group 속성 확인
        const group = button.getAttribute("data-group");

        // 버튼 스타일 처리 대상 선택
        let buttonsToReset;

        if (group) {
          // data-group이 있는 경우 같은 그룹의 버튼들만 선택
          buttonsToReset = document.querySelectorAll(`.period-button[data-group="${group}"]`);
        } else {
          // data-group이 없는 경우 (기존 코드와 호환) 모든 버튼 선택
          buttonsToReset = document.querySelectorAll(".period-button:not([data-group])");
        }

        // 선택된 버튼들의 스타일 초기화
        buttonsToReset.forEach((btn) => {
          btn.style.background = "white";
          btn.style.borderColor = "#e2e8f0";
          btn.style.color = "#64748b";
        });

        // 클릭된 버튼 스타일 변경
        button.style.background = "var(--menu-active)";
        button.style.borderColor = "var(--primary-color)";
        button.style.color = "var(--primary-color)";

        if (!button.classList.contains("month-btn")) {
          var periodVal = $(button).val();

          // 그룹에 따라 다른 입력 필드 업데이트
          if (group === "publish") {
            // 게시기간 필드 업데이트
            if ($("#publishEndDt").length) {
              $("#publishEndDt").val(getToday());
            }
            if ($("#publishStartDt").length) {
              periodVal = periodVal * -1;
              $("#publishStartDt").val(addDate(periodVal));
            }
          } else {
            if ($("#searchEndDt").length) {
              $("#searchEndDt").val(getToday());
            }
            if ($("#searchStartDt").length) {
              periodVal = periodVal * -1;
              $("#searchStartDt").val(addDate(periodVal));
            }
          }
        }
      } catch (error) {
        console.error("Error handling period button style:", error);
      }
    });
  });
}

//
function handlePeriodButtonStyleSearch() {
  const periodButtons = document.querySelectorAll(".search-dt");

  if (!periodButtons.length) {
    console.log("Period buttons not found");
    return;
  }

  periodButtons.forEach((button) => {
    button.addEventListener("click", () => {
      try {
        periodButtons.forEach((btn) => {
          btn.style.background = "white";
          btn.style.borderColor = "#e2e8f0";
          btn.style.color = "#64748b";
        });

        button.style.background = "var(--menu-active)";
        button.style.borderColor = "var(--primary-color)";
        button.style.color = "var(--primary-color)";
        var periodVal = $(button).val();
        if ($("#searchEndDt").length) {
          $("#searchEndDt").val(getToday());
        }

        if ($("#searchStartDt").length) {
          periodVal = periodVal * -1;
          $("#searchStartDt").val(addDate(periodVal));
        }
      } catch (error) {
        console.error("Error handling period button style:", error);
      }
    });
  });
}

function handlePeriodButtonStyleCreate() {
  const periodButtons = document.querySelectorAll(".create-dt");

  if (!periodButtons.length) {
    console.log("Period buttons not found");
    return;
  }

  periodButtons.forEach((button) => {
    button.addEventListener("click", () => {
      try {
        periodButtons.forEach((btn) => {
          btn.style.background = "white";
          btn.style.borderColor = "#e2e8f0";
          btn.style.color = "#64748b";
        });

        button.style.background = "var(--menu-active)";
        button.style.borderColor = "var(--primary-color)";
        button.style.color = "var(--primary-color)";
        var periodVal = $(button).val();
        if ($("#searchRegEndDt").length) {
          $("#searchRegEndDt").val(getToday());
        }

        if ($("#searchRegStartDt").length) {
          periodVal = periodVal * -1;
          $("#searchRegStartDt").val(addDate(periodVal));
        }
      } catch (error) {
        console.error("Error handling period button style:", error);
      }
    });
  });
}

// 체크박스 전체 선택 핸들러
function handleCheckboxAllSelect() {
  const headerCheckbox = document.querySelector("thead .form-check-input");

  if (!headerCheckbox) {
    console.log("Header checkbox not found");
    return;
  }

  headerCheckbox.addEventListener("change", function () {
    try {
      const bodyCheckboxes = document.querySelectorAll("tbody .form-check-input");

      if (!bodyCheckboxes.length) {
        console.log("Body checkboxes not found");
        return;
      }
      bodyCheckboxes.forEach((checkbox) => {
        checkbox.checked = this.checked;
      });
    } catch (error) {
      console.error("Error handling checkbox selection:", error);
    }
  });
}

// 공통 알림 모달 함수
const showAlert = (message, confirmCallback) => {
  const modalEl = document.getElementById("commonAlertModal");
  const messageEl = document.getElementById("commonAlertMessage");
  const modalBtn = document.getElementById("confirm");

  // 메시지 설정
  messageEl.textContent = message;

  // 모달 표시
  const modal = new bootstrap.Modal(modalEl);
  modal.show();

  // hidden - focus error 대응
  modalBtn.addEventListener("click", () => {
    modalBtn.blur();
  });

  // 모달이 닫힐 때 메시지 초기화 (옵션)
  modalEl.addEventListener("hidden.bs.modal", function () {
    messageEl.textContent = "";
    if (typeof confirmCallback === "function") {
      confirmCallback();
    } else if (typeof confirmCallback === "object") {
      confirmCallback.focus();
    }
  });
};

// 사용 예시:
// showAlert('이미 등록된 아이디입니다.');

// Confirm 모달 함수
const showConfirm = (message, confirmCallback, cancelCallback, title = "확인") => {
  const modalEl = document.getElementById("commonConfirmModal");
  const messageEl = document.getElementById("commonConfirmMessage");
  const titleEl = modalEl.querySelector(".modal-title"); // title 엘리먼트 선택
  const confirmBtn = document.getElementById("confirmModalConfirmBtn");
  const cancelBtn = document.getElementById("confirmModalCancelBtn");

  messageEl.innerHTML = message;
  titleEl.textContent = title; // title 설정

  const modal = new bootstrap.Modal(modalEl);

  const newConfirmBtn = confirmBtn.cloneNode(true);
  const newCancelBtn = cancelBtn.cloneNode(true);
  confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
  cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);

  newConfirmBtn.addEventListener("click", () => {
    modal.hide();
    if (typeof confirmCallback === "function") {
      confirmCallback();
    }
    $("#confirmModalConfirmBtn").blur();
    newConfirmBtn.blur();
  });

  newCancelBtn.addEventListener("click", () => {
    modal.hide();
    if (typeof cancelCallback === "function") {
      cancelCallback();
    }
    $("#confirmModalCancelBtn").blur();
    newCancelBtn.blur();
  });

  modalEl.addEventListener("hidden.bs.modal", function () {
    messageEl.innerHTML = "";
    titleEl.textContent = "확인"; // 모달이 닫힐 때 title 초기화
  });

  modal.show();
};

// 서브메뉴 토글 핸들러
function handleSubmenuToggle() {
  const menuItems = document.querySelectorAll(".menu-item.has-submenu .menu-link");

  if (!menuItems.length) {
    console.log("Menu items not found");
    return;
  }

  menuItems.forEach((link) => {
    link.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        const menuItem = link.parentElement;
        const isOpen = menuItem.classList.contains("open");

        // 다른 모든 서브메뉴 닫기
        document.querySelectorAll(".menu-item.has-submenu").forEach((item) => {
          item.classList.remove("open");
        });

        // 클릭된 메뉴 토글
        if (!isOpen) {
          menuItem.classList.add("open");
        }
      } catch (error) {
        console.error("Error handling submenu toggle:", error);
      }
    });
  });
}

function sidebarToggle() {
  const toggleBtn = document.querySelector(".toggle-sidebar");
  const sidebarWrapper = document.querySelector(".sidebarWrapper");
  const leftSidebar = document.querySelector(".left-sidebar");
  const mainContent = document.querySelector(".main-content");

  if (!leftSidebar || !sidebarWrapper) {
    console.log("Sidebar elements not found");
    return;
  }

  // 사이드바 토글 기능
  toggleBtn.addEventListener("click", () => {
    sidebarWrapper.classList.toggle("collapsed");
    leftSidebar.classList.toggle("collapsed");

    // mainContent가 존재할 경우에만 margin 조정
    if (mainContent) {
      mainContent.style.marginLeft = sidebarWrapper.classList.contains("collapsed") ? "60px" : "250px";
    }
  });
}

function toggleDropdown() {
  document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function (event) {
  if (!event.target.matches(".btnDrop")) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    for (var i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains("show")) {
        openDropdown.classList.remove("show");
      }
    }
  }
};

function toggleDetailSearch() {
  const detailArea = document.getElementById("detailSearchArea");
  const button = event.currentTarget;
  const icon = button.querySelector("i");

  if (detailArea.style.display === "none") {
    detailArea.style.display = "block";
    icon.classList.remove("fa-chevron-down");
    icon.classList.add("fa-chevron-up");
  } else {
    detailArea.style.display = "none";
    icon.classList.remove("fa-chevron-up");
    icon.classList.add("fa-chevron-down");
  }
}

/* bnf */
class BenefitManagerSystem {
  constructor() {
    this.benefits = [];
    this.currentIndex = null;
    this.selectModal = null;
    this.MAX_BENEFITS = 999; // 최대 혜택 개수 설정
    this.init();
  }

  // 기존 init 메서드 수정
  init() {
    // 기존 코드 유지
    this.selectModal = new BenefitSelectModal(this);
    bfsm = this.selectModal;

    const addBtn = document.getElementById("bnfAddBtn");
    if (addBtn) {
      addBtn.addEventListener("click", () => this.handleAddClick());
    }

    this.renderInputs();
  }

  // 추가 버튼 클릭 핸들러 추가
  handleAddClick() {
    if (this.benefits.length >= this.MAX_BENEFITS) {
      alert(this.MAX_BENEFITS + "개 이상 등록할 수 없습니다");
      return;
    }
    this.showSelectModal();
  }

  showSelectModal() {
    this.selectModal.showModal();
  }

  addBenefit(benefit, selVal) {
    if (this.benefits.length >= this.MAX_BENEFITS) {
      alert(this.MAX_BENEFITS + "개 이상 등록할 수 없습니다");
      return;
    }
    let data = {
      text: benefit.benefit,
      value: benefit.selVal,
      provider: benefit.provider,
      cate01: benefit.cate01,
      cate02: benefit.cate02,
    };
    var orderNo = this.benefits.length + 1;
    var saveId = setPlanBenefit(selVal, orderNo);
    data.mapId = saveId;

    this.benefits.push(data);

    this.renderInputs();
  }

  removeBenefit(index) {
    var mngId = $("#txtBenefit" + index).attr("data-benefitmapid");
    deletePlanBenefit(mngId);
    this.benefits.splice(index, 1);
    this.renderInputs();
  }

  moveBenefit(index, direction) {
    if (direction === "up" && index > 0) {
      [this.benefits[index], this.benefits[index - 1]] = [this.benefits[index - 1], this.benefits[index]];
    } else if (direction === "down" && index < this.benefits.length - 1) {
      [this.benefits[index], this.benefits[index + 1]] = [this.benefits[index + 1], this.benefits[index]];
    }
    this.renderInputs();
    setPlanBenefitOrder();
  }

  renderInputs() {
    const container1 = document.getElementById("bnfInputs-telecom");
    const container2 = document.getElementById("bnfInputs-semo");
    const container3 = document.getElementById("bnfInputs-internet");

    if (!container1 || !container2 || !container3) return;

    container1.innerHTML = "";
    container2.innerHTML = "";
    container3.innerHTML = "";

    this.benefits.forEach((benefit, index) => {
      const inputWrapper = document.createElement("div");
      inputWrapper.className = "bnf-manager__input-wrapper";

      const input = document.createElement("input");
      input.type = "text";
      input.id = "txtBenefit" + index;
      input.className = "bnf-manager__input";
      input.value = benefit.text;
      input.readOnly = true;
      input.setAttribute("data-benefitId", benefit.value); // input의 type 속성 설정
      input.setAttribute("data-order", index); // input의 type 속성 설정
      input.setAttribute("data-benefitMapId", benefit.mapId); // input의 type 속성 설정

      const btnGroup = document.createElement("div");
      btnGroup.className = "bnf-manager__btn-group";

      const upBtn = document.createElement("button");
      upBtn.className = `bnf-manager__btn ${index === 0 ? "bnf-manager__btn--disabled" : ""}`;
      upBtn.textContent = "▲";
      upBtn.disabled = index === 0;
      upBtn.onclick = () => this.moveBenefit(index, "up");

      const downBtn = document.createElement("button");
      downBtn.className = `bnf-manager__btn ${index === this.benefits.length - 1 ? "bnf-manager__btn--disabled" : ""}`;
      downBtn.textContent = "▼";
      downBtn.disabled = index === this.benefits.length - 1;
      downBtn.onclick = () => this.moveBenefit(index, "down");

      const deleteBtn = document.createElement("button");
      deleteBtn.className = "bnf-manager__btn";
      deleteBtn.textContent = "X";
      deleteBtn.onclick = () => this.removeBenefit(index);

      btnGroup.appendChild(upBtn);
      btnGroup.appendChild(downBtn);
      btnGroup.appendChild(deleteBtn);

      inputWrapper.appendChild(input);
      inputWrapper.appendChild(btnGroup);

      if (benefit.provider == "telecom") {
        container1.appendChild(inputWrapper);
      } else if (benefit.provider == "semo") {
        container2.appendChild(inputWrapper);
      } else if (benefit.provider == "internet") {
        container3.appendChild(inputWrapper);
      }
    });

    // 추가 버튼 상태 업데이트
    const addBtn = document.getElementById("bnfAddBtn");
    if (addBtn) {
      addBtn.disabled = this.benefits.length >= this.MAX_BENEFITS;
      addBtn.className = `bnf-manager__btn ${
        this.benefits.length >= this.MAX_BENEFITS ? "bnf-manager__btn--disabled" : ""
      }`;
    }
  }
}

class BenefitSelectModal {
  constructor(manager) {
    this.manager = manager;
    this.init();
  }

  init() {
    this.addEventListeners();
    this.handleRowSelection();
  }

  addEventListeners() {
    const closeBtn = document.querySelector(".bnf-select__close");
    if (closeBtn) {
      closeBtn.addEventListener("click", () => this.closeModal());
    }

    const applyBtn = document.querySelector(".bnf-select__btn--apply");
    if (applyBtn) {
      applyBtn.addEventListener("click", () => this.closeModal());
    }

    const confirmBtn = document.querySelector(".bnf-select__btn--confirm");
    if (confirmBtn) {
      confirmBtn.addEventListener("click", () => this.confirmSelection());
    }

    const searchBtn = document.querySelector(".bnf-select__search-btn");
    if (searchBtn) {
      searchBtn.addEventListener("click", () => this.handleSearch());
    }
  }

  handleRowSelection() {
    const rows = document.querySelectorAll(".bnf-select__row");
    rows.forEach((row) => {
      row.addEventListener("click", () => {
        rows.forEach((r) => r.classList.remove("bnf-select__row--selected"));
        row.classList.add("bnf-select__row--selected");
        const radio = row.querySelector(".bnf-select__radio");
        if (radio) radio.checked = true;
      });
    });
  }

  handleSearch() {
    const searchInput = document.querySelector(".bnf-select__input");
    if (searchInput) {
      const searchValue = searchInput.value;
    }
  }

  confirmSelection() {
    const selectedRow = document.querySelector(".bnf-select__row--selected");
    if (selectedRow && this.manager) {
      const benefitCell = selectedRow.querySelector("#hndBemefitTile");
      const hndBenefitIdInput = selectedRow.querySelector("#hndBemefitId");
      const hndProvidet = selectedRow.querySelector("#hndProvidet");
      const hndCate01 = selectedRow.querySelector("#hndCate01");
      const hndCate02 = selectedRow.querySelector("#hndCate02");

      console.log(benefitCell);

      if (benefitCell) {
        const benefit = benefitCell.value;
        const selVal = hndBenefitIdInput.value;
        const provider = hndProvidet.value;
        const cate01 = hndCate01.value;
        const cate02 = hndCate02.value;

        let data = {
          benefit: benefit,
          selVal: selVal,
          provider: provider,
          cate01: cate01,
          cate02: cate02,
        };

        // 혜택 추가 전에 최대 개수 체크는 manager에서 처리
        this.manager.addBenefit(data, selVal);
        showAlert("등록되었습니다.");
      }
    } else {
      showAlert("항목을 선택해주세요.");
      return;
    }
    this.closeModal();
  }

  closeModal() {
    const overlay = document.querySelector(".bnf-select__overlay");
    const modal = document.querySelector(".bnf-select");
    if (overlay) overlay.style.display = "none";
    if (modal) modal.style.display = "none";
  }

  showModal() {
    const overlay = document.querySelector(".bnf-select__overlay");
    const modal = document.querySelector(".bnf-select");
    if (overlay) overlay.style.display = "block";
    if (modal) modal.style.display = "block";
  }
}

const BannerUploader = {
  trigger(inputId) {
    document.getElementById(inputId).click();
  },

  preview(input, previewId) {
    const previewEl = document.getElementById(previewId);
    previewEl.innerHTML = "";

    if (input.files && input.files[0]) {
      //02.12 최의규 image file check 기능 추가
      const file = input.files[0];
      const validExtensions = ["svg", "jpg", "jpeg", "png"];
      const fileExtension = file.name.split(".").pop().toLowerCase();

      if (!validExtensions.includes(fileExtension)) {
        alert("이미지 형태의 파일을 선택해주세요.");
        input.value = "";
        return;
      } else {
        const reader = new FileReader();
        reader.onload = (e) => {
          const img = document.createElement("img");
          img.src = e.target.result;
          img.classList.add("uploader__preview-image");
          previewEl.appendChild(img);
          // 부모 요소에 active 상태 클래스 추가
          input.closest(".event-banner__uploader").classList.add("uploader--active");
        };
        reader.readAsDataURL(input.files[0]);

        if ($("#fileText1").length > 0) {
          $("#fileText1").val(file.name);
        }
      }
    }
  },
  getfilename(input, previewId) {
    const previewEl = document.getElementById(previewId);
    previewEl.innerHTML = "";
    if (input.files && input.files[0]) {
      const file = input.files[0]; // 선택된 파일
      const fileName = file.name; // 파일명 가져오기
      // 파일명을 표시
      const fileNameElement = document.createElement("p");
      fileNameElement.textContent = `파일명: ${fileName}`;
      previewEl.appendChild(fileNameElement);
    }
  },
  getfilenameExcel(input, previewId) {
    const file = input.files[0];
    const validExtensions = ["xls", "xlsx"];
    const fileExtension = file.name.split(".").pop().toLowerCase();

    if (!validExtensions.includes(fileExtension)) {
      alert("엑셀 파일을 선택해주세요.");
      input.value = "";
      return;
    } else {
      const previewEl = document.getElementById(previewId);
      previewEl.innerHTML = "";
      if (input.files && input.files[0]) {
        const file = input.files[0]; // 선택된 파일
        const fileName = file.name; // 파일명 가져오기
        // 파일명을 표시
        const fileNameElement = document.createElement("p");
        fileNameElement.textContent = `파일명: ${fileName}`;
        previewEl.appendChild(fileNameElement);
      }
    }
  },
  getfilenameExcelEar(input, previewId) {
    const file = input.files[0];
    const validExtensions = ["xls", "xlsx"];
    const fileExtension = file.name.split(".").pop().toLowerCase();

    if (!validExtensions.includes(fileExtension)) {
      alert("엑셀 파일을 선택해주세요.");
      input.value = "";
      return;
    } else {
      const previewEl = document.getElementById(previewId);
      previewEl.innerHTML = "";
      if (input.files && input.files[0]) {
        const file = input.files[0]; // 선택된 파일
        const fileName = file.name; // 파일명 가져오기
        // 파일명을 표시
        const fileNameElement = document.createElement("span");
        fileNameElement.textContent = `${fileName}`;
        previewEl.appendChild(fileNameElement);
      }
    }
  },
};

// service_add.html 요금제 추가/이동/삭제 UI
document.addEventListener("DOMContentLoaded", function () {
  const container = document.querySelector(".cloneitems__outer");

  if (container) {
    // 초기 템플릿 저장
    const templateItem = container.querySelector(".cloneitems").cloneNode(true);

    // 초기 라디오 name과 id 업데이트
    updateAllRadioNames();

    // 항목 추가 이벤트 처리
    container.addEventListener("click", function (e) {
      if (e.target.classList.contains("btn-primary")) {
        const items = container.querySelectorAll(".cloneitems");
        if (items.length >= 10) return; // 최대 3개까지만 허용

        // items가 없을 경우 저장된 템플릿 사용, 있을 경우 첫번째 아이템 복제
        //const newItem = items.length === 0 ? templateItem.cloneNode(true) : items[0].cloneNode(true);

        const newItem = templateItem.cloneNode(true);

        // 버튼 상태 업데이트
        updateButtons(newItem);
        // 추가된 항목 초기화
        newItem.querySelector(".main-deal-id").value = "0";
        newItem.querySelector("input[name=planContent]").value = "";
        newItem.querySelector(".select-company").value = "";
        newItem.querySelector("input[name=linkUrl]").value = "";

        // 추가 버튼 다음에 새 아이템 삽입
        const addButton = container.querySelector(".btn-primary");
        addButton.after(newItem);

        updateAllItemsButtons();
        updateAllRadioNames(); // 새 아이템 추가 후 라디오 이름 업데이트
      }
    });

    // 항목 삭제 이벤트 처리
    container.addEventListener("click", function (e) {
      if (e.target.classList.contains("btn-danger")) {
        e.target.closest(".cloneitems").remove();
        updateAllItemsButtons();
        updateAllRadioNames(); // 삭제 후 라디오 이름 업데이트
      }
    });

    // 위치 변경 버튼 이벤트 처리
    container.addEventListener("click", function (e) {
      if (e.target.textContent === "▲" || e.target.textContent === "▼") {
        const currentItem = e.target.closest(".cloneitems");
        const items = Array.from(container.querySelectorAll(".cloneitems"));
        const currentIndex = items.indexOf(currentItem);

        if (e.target.textContent === "▲" && currentIndex > 0) {
          container.insertBefore(currentItem, items[currentIndex - 1]);
        } else if (e.target.textContent === "▼" && currentIndex < items.length - 1) {
          container.insertBefore(currentItem, items[currentIndex + 1].nextSibling);
        }

        updateAllItemsButtons();
        updateAllRadioNames(); // 위치 변경 후 라디오 이름 업데이트
      }
    });

    function updateButtons(item) {
      const btnBox = item.querySelector(".cloneitems__btnbox");
      const addBtn = item.querySelector(".btn-primary");

      if (addBtn) addBtn.remove();
    }

    function updateAllItemsButtons() {
      const items = Array.from(container.querySelectorAll(".cloneitems"));

      items.forEach((item, index) => {
        const btnBox = item.querySelector(".cloneitems__btnbox");

        if (index === 0) {
          btnBox.innerHTML = '<button class="btn btn-dark" style="min-width:50px;">▼</button>';
        } else if (index === items.length - 1) {
          btnBox.innerHTML = '<button class="btn btn-warning" style="min-width:50px;">▲</button>';
        } else {
          btnBox.innerHTML = `
                      <button class="btn btn-warning" style="min-width:50px;">▲</button>
                      <button class="btn btn-dark" style="min-width:50px;">▼</button>
                  `;
        }
      });
    }

    function updateAllRadioNames() {
      const timestamp = new Date().getTime(); // 고유성을 위한 타임스탬프
      const items = Array.from(container.querySelectorAll(".cloneitems"));

      items.forEach((item, itemIndex) => {
        const radios = item.querySelectorAll('input[type="radio"]');
        const uniqueGroupName = `exposure_${timestamp}_${itemIndex}`; // 고유한 name 생성

        radios.forEach((radio, radioIndex) => {
          const oldId = radio.id;
          const baseId = radio.id.replace(/\d+.*$/, ""); // 숫자로 끝나는 부분 제거
          const newId = `${baseId}${itemIndex}_${radioIndex}_${timestamp}`;

          radio.id = newId;
          radio.name = uniqueGroupName;

          const label = item.querySelector(`label[for="${oldId}"]`);
          if (label) {
            label.setAttribute("for", newId);
          }
        });
      });
    }

    // 초기화
    updateAllItemsButtons();
  }
});

function initDateSetControl() {
  // dateset 라디오 버튼이 존재하는지 확인
  const datesetRadios = document.querySelectorAll('input[name="datesetitem"]');
  if (datesetRadios.length === 0) return;

  // 이벤트 리스너 등록
  datesetRadios.forEach((radio) => {
    radio.addEventListener("change", function () {
      const dateTimeInputs = document.querySelectorAll('input[type="date"], input[type="time"]');
      const isDateRangeEnabled = document.getElementById("dateset2").checked;

      dateTimeInputs.forEach((input) => {
        input.disabled = !isDateRangeEnabled;
      });
    });
  });
}

document.addEventListener("DOMContentLoaded", initDateSetControl);

function initFileUpload() {
  // 파일 input 요소들을 찾음
  const fileInputs = document.querySelectorAll(".file-input");
  if (fileInputs.length === 0) return;

  // 초기 이미지 확인 및 설정
  function initializePreview(inputNum) {
    const previewImg = document.getElementById(`preview${inputNum}`);
    const textInput = document.getElementById(`fileText${inputNum}`);

	
    if (previewImg && previewImg.src && previewImg.src !== window.location.href) {
      previewImg.style.display = "block";
      // URL에서 파일명 추출
      const fileName = previewImg.src.split("/").pop();
      if (textInput) {
        textInput.value = fileName;
      }
    }
  }

  fileInputs.forEach((fileInput) => {
	
    // 초기 이미지 확인
    const inputNum = fileInput.id.replace("fileInput", "");
    initializePreview(inputNum);

    fileInput.addEventListener("change", function (e) {
		
      const inputNum = this.id.replace("fileInput", "");
      const textInput = document.getElementById(`fileText${inputNum}`);
      const previewImg = document.getElementById(`preview${inputNum}`);
	  
      if (!textInput || !previewImg) return;

      const file = e.target.files[0];

      if (file) {
        const validExtensions = ["svg", "jpg", "jpeg", "png"];
        const fileExtension = file.name.split(".").pop().toLowerCase();

        if (!validExtensions.includes(fileExtension)) {
          alert("이미지 형태의 파일을 선택해주세요.");
          this.value = "";
          textInput.value = "";
          previewImg.style.display = "none";
          return;
        }

        textInput.value = file.name;

        const reader = new FileReader();
        reader.onload = function (e) {
          previewImg.src = e.target.result;
          previewImg.style.display = "block";
        };
        reader.readAsDataURL(file);
      } else {
        textInput.value = "";
        previewImg.style.display = "none";
      }
    });
  });
}

// DOM이 로드된 후 초기화 함수 실행
document.addEventListener("DOMContentLoaded", initFileUpload);
