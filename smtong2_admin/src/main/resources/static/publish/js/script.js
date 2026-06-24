// 탭 메뉴 초기화 함수
const initTabMenu = () => {
  const tabMenus = document.querySelectorAll('.tab-menu');
  
  if (!tabMenus.length) return; // 탭 메뉴가 없으면 실행하지 않음

  tabMenus.forEach(menu => {
      const tabs = menu.querySelectorAll('li');
      
      // 탭 클릭 이벤트 핸들러
      const handleTabClick = (clickedTab) => {
          tabs.forEach(tab => tab.classList.remove('active'));
          clickedTab.classList.add('active');
      };

      // 각 탭에 이벤트 리스너 추가
      tabs.forEach(tab => {
          tab.addEventListener('click', () => handleTabClick(tab));
      });
  });
};

// DOM이 로드된 후 초기화
document.addEventListener('DOMContentLoaded', initTabMenu);

// 동적으로 탭이 추가될 경우를 위한 재초기화 함수 노출
window.reinitTabMenu = initTabMenu;


// 페이지 로드 시 초기화
function initializePage() {
  try {
    const display = document.querySelector('.timer');
    if (display) {
      const threeMinutes = 60 * 3;
      startTimer(threeMinutes, display);
    }

    handlePeriodButtonStyle();
    handleCheckboxAllSelect();
    handleSubmenuToggle();
    sidebarToggle();
  } catch (error) {
    console.error('Error initializing page:', error);
  }
}

// DOMContentLoaded 이벤트를 사용하여 더 안전하게 초기화
document.addEventListener('DOMContentLoaded', initializePage);

/* ******************************************************** */

// 기간 버튼 스타일 핸들러
function handlePeriodButtonStyle() {
  const periodButtons = document.querySelectorAll('.period-button');
  
  if (!periodButtons.length) {
    console.log('Period buttons not found');
    return;
  }

  periodButtons.forEach(button => {
    button.addEventListener('click', () => {
      try {
        periodButtons.forEach(btn => {
          btn.style.background = 'white';
          btn.style.borderColor = '#e2e8f0';
          btn.style.color = '#64748b';
        });
        
        button.style.background = "var(--menu-active)";
        button.style.borderColor = "var(--primary-color)";
        button.style.color = "var(--primary-color)";
      } catch (error) {
        console.error('Error handling period button style:', error);
      }
    });
  });
}

// 체크박스 전체 선택 핸들러
function handleCheckboxAllSelect() {
  const headerCheckbox = document.querySelector('thead .form-check-input');
  
  if (!headerCheckbox) {
    console.log('Header checkbox not found');
    return;
  }

  headerCheckbox.addEventListener('change', function() {
    try {
      const bodyCheckboxes = document.querySelectorAll('tbody .form-check-input');
      
      if (!bodyCheckboxes.length) {
        console.log('Body checkboxes not found');
        return;
      }

      bodyCheckboxes.forEach(checkbox => {
        checkbox.checked = this.checked;
      });
    } catch (error) {
      console.error('Error handling checkbox selection:', error);
    }
  });
}

// 타이머 기능
function startTimer(duration, display) {
  if (!display) {
    console.log('Timer display element not found');
    return;
  }

  let timer = duration;
  const interval = setInterval(function () {
    try {
      const minutes = parseInt(timer / 60, 10);
      const seconds = parseInt(timer % 60, 10);

      display.textContent = minutes + ":" + (seconds < 10 ? "0" : "") + seconds;

      if (--timer < 0) {
        clearInterval(interval);
        display.textContent = "0:00";
      }
    } catch (error) {
      console.error('Error updating timer:', error);
      clearInterval(interval);
    }
  }, 1000);

  return interval; // 필요한 경우 타이머를 중지할 수 있도록 반환
}


// 공통 알림 모달 함수
const showAlert = (message) => {
  const modalEl = document.getElementById('commonAlertModal');
  const messageEl = document.getElementById('commonAlertMessage');
  
  // 메시지 설정
  messageEl.textContent = message;
  
  // 모달 표시
  const modal = new bootstrap.Modal(modalEl);
  modal.show();
  
  // 모달이 닫힐 때 메시지 초기화 (옵션)
  modalEl.addEventListener('hidden.bs.modal', function () {
      messageEl.textContent = '';
  });
};
// 사용 예시:
// showAlert('이미 등록된 아이디입니다.');


// Confirm 모달 함수
const showConfirm = (message, confirmCallback, cancelCallback, title = '확인') => {
  const modalEl = document.getElementById('commonConfirmModal');
  const messageEl = document.getElementById('commonConfirmMessage');
  const titleEl = modalEl.querySelector('.modal-title');  // title 엘리먼트 선택
  const confirmBtn = document.getElementById('confirmModalConfirmBtn');
  const cancelBtn = document.getElementById('confirmModalCancelBtn');
  
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
  });
  
  newCancelBtn.addEventListener('click', () => {
    modal.hide();
    if (typeof cancelCallback === 'function') {
      cancelCallback();
    }
  });
  
  modalEl.addEventListener('hidden.bs.modal', function () {
    messageEl.innerHTML = '';
    titleEl.textContent = '확인';  // 모달이 닫힐 때 title 초기화
  });
  
  modal.show();
};



// 서브메뉴 토글 핸들러
function handleSubmenuToggle() {
  const menuItems = document.querySelectorAll('.menu-item.has-submenu .menu-link');

  if (!menuItems.length) {
    console.log('Menu items not found');
    return;
  }

  menuItems.forEach(link => {
    link.addEventListener('click', (e) => {
      try {
        e.preventDefault();
        const menuItem = link.parentElement;
        const isOpen = menuItem.classList.contains('open');

        // 다른 모든 서브메뉴 닫기
        document.querySelectorAll('.menu-item.has-submenu').forEach(item => {
          item.classList.remove('open');
        });

        // 클릭된 메뉴 토글
        if (!isOpen) {
          menuItem.classList.add('open');
        }
      } catch (error) {
        console.error('Error handling submenu toggle:', error);
      }
    });
  });
}

function sidebarToggle() {
  const toggleBtn = document.querySelector('.toggle-sidebar');
  const sidebarWrapper = document.querySelector('.sidebarWrapper');
  const leftSidebar = document.querySelector('.left-sidebar');
  const mainContent = document.querySelector('.main-content');

  if (!leftSidebar || !sidebarWrapper) {
      console.log('Sidebar elements not found');
      return;
  }

  // 사이드바 토글 기능
  toggleBtn.addEventListener('click', () => {
      sidebarWrapper.classList.toggle('collapsed');
      leftSidebar.classList.toggle('collapsed');

      // mainContent가 존재할 경우에만 margin 조정
      if (mainContent) {
          mainContent.style.marginLeft = sidebarWrapper.classList.contains('collapsed') 
              ? '60px' 
              : '250px';
      }
  });
}

function toggleDropdown() {
  document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.btnDrop')) {
      var dropdowns = document.getElementsByClassName("dropdown-content");
      for (var i = 0; i < dropdowns.length; i++) {
          var openDropdown = dropdowns[i];
          if (openDropdown.classList.contains('show')) {
              openDropdown.classList.remove('show');
          }
      }
  }
}

function toggleDetailSearch() {
    const detailArea = document.getElementById('detailSearchArea');
    const button = event.currentTarget;
    const icon = button.querySelector('i');
    
    if (detailArea.style.display === 'none') {
        detailArea.style.display = 'block';
        icon.classList.remove('fa-chevron-down');
        icon.classList.add('fa-chevron-up');
    } else {
        detailArea.style.display = 'none';
        icon.classList.remove('fa-chevron-up');
        icon.classList.add('fa-chevron-down');
    }
}


/* bnf */
class BenefitManagerSystem {
  constructor() {
      this.benefits = [];
      this.currentIndex = null;
      this.selectModal = null;
      this.MAX_BENEFITS = 3; // 최대 혜택 개수 설정
      this.init();
      this.initManageButton();
  }

  initManageButton() {
    const manageBtn = document.getElementById('bnfManageBtn');
    if (manageBtn) {
        manageBtn.addEventListener('click', () => {
            this.showManageModal();
        });
    }
  }

  showManageModal() {
      const overlay = document.getElementById('bnfManageOverlay');
      const modal = document.getElementById('bnfManageModal');
      if (overlay) overlay.style.display = 'block';
      if (modal) modal.style.display = 'block';
  }

  hideManageModal() {
      const overlay = document.getElementById('bnfManageOverlay');
      const modal = document.getElementById('bnfManageModal');
      if (overlay) overlay.style.display = 'none';
      if (modal) modal.style.display = 'none';
  }

  // 혜택관리 모달 초기화 및 이벤트 바인딩
  initManageModal() {
      const closeBtn = document.querySelector('#bnfManageModal .bnf-select__close');
      if (closeBtn) {
          closeBtn.addEventListener('click', () => this.hideManageModal());
      }
      const closeBtn2 = document.querySelector('#bnfManageModal .bnf-select-close2');
      if (closeBtn2) {
          closeBtn2.addEventListener('click', () => this.hideManageModal());
      }

      const closeFooterBtn = document.querySelector('#bnfManageModal .bnf-select__btn--apply');
      if (closeFooterBtn) {
          closeFooterBtn.addEventListener('click', () => this.hideManageModal());
      }

      const overlay = document.getElementById('bnfManageOverlay');
      if (overlay) {
          overlay.addEventListener('click', () => this.hideManageModal());
      }

      // 수정 버튼 이벤트 리스너
      const editBtns = document.querySelectorAll('#bnfManageModal .bnf-select__edit-btn');
      editBtns.forEach(btn => {
          btn.addEventListener('click', (e) => {
              const row = e.target.closest('.bnf-select__row');
              const title = row.querySelector('.bnf-select__td:nth-child(2)').textContent;
              console.log('수정 클릭:', title);
              // 여기에 수정 로직 추가
          });
      });
  }


  // 기존 init 메서드 수정
  init() {
      // 기존 코드 유지
      this.selectModal = new BenefitSelectModal(this);
      this.initManageButton();
      this.initManageModal();

      const addBtn = document.getElementById('bnfAddBtn');
      if (addBtn) {
          addBtn.addEventListener('click', () => this.handleAddClick());
      }

      this.renderInputs();
  }

  // 추가 버튼 클릭 핸들러 추가
  handleAddClick() {
      if (this.benefits.length >= this.MAX_BENEFITS) {
          alert('3개 이상 등록할 수 없습니다');
          return;
      }
      this.showSelectModal();
  }

  showSelectModal() {
      this.selectModal.showModal();
  }

  addBenefit(benefit) {
      if (this.benefits.length >= this.MAX_BENEFITS) {
          alert('3개 이상 등록할 수 없습니다');
          return;
      }
      this.benefits.push(benefit);
      this.renderInputs();
  }

  removeBenefit(index) {
      this.benefits.splice(index, 1);
      this.renderInputs();
  }

  moveBenefit(index, direction) {
      if (direction === 'up' && index > 0) {
          [this.benefits[index], this.benefits[index - 1]] = 
          [this.benefits[index - 1], this.benefits[index]];
      } else if (direction === 'down' && index < this.benefits.length - 1) {
          [this.benefits[index], this.benefits[index + 1]] = 
          [this.benefits[index + 1], this.benefits[index]];
      }
      this.renderInputs();
  }

  renderInputs() {
      const container = document.getElementById('bnfInputs');
      if (!container) return;

      container.innerHTML = '';

      this.benefits.forEach((benefit, index) => {
          const inputWrapper = document.createElement('div');
          inputWrapper.className = 'bnf-manager__input-wrapper';

          const input = document.createElement('input');
          input.type = 'text';
          input.className = 'bnf-manager__input';
          input.value = benefit;
          input.readOnly = true;

          const btnGroup = document.createElement('div');
          btnGroup.className = 'bnf-manager__btn-group';

          const upBtn = document.createElement('button');
          upBtn.className = `bnf-manager__btn ${index === 0 ? 'bnf-manager__btn--disabled' : ''}`;
          upBtn.textContent = '▲';
          upBtn.disabled = index === 0;
          upBtn.onclick = () => this.moveBenefit(index, 'up');

          const downBtn = document.createElement('button');
          downBtn.className = `bnf-manager__btn ${index === this.benefits.length - 1 ? 'bnf-manager__btn--disabled' : ''}`;
          downBtn.textContent = '▼';
          downBtn.disabled = index === this.benefits.length - 1;
          downBtn.onclick = () => this.moveBenefit(index, 'down');

          const deleteBtn = document.createElement('button');
          deleteBtn.className = 'bnf-manager__btn';
          deleteBtn.textContent = 'X';
          deleteBtn.onclick = () => this.removeBenefit(index);

          btnGroup.appendChild(upBtn);
          btnGroup.appendChild(downBtn);
          btnGroup.appendChild(deleteBtn);

          inputWrapper.appendChild(input);
          inputWrapper.appendChild(btnGroup);
          container.appendChild(inputWrapper);
      });

      // 추가 버튼 상태 업데이트
      const addBtn = document.getElementById('bnfAddBtn');
      if (addBtn) {
          addBtn.disabled = this.benefits.length >= this.MAX_BENEFITS;
          addBtn.className = `bnf-manager__btn ${this.benefits.length >= this.MAX_BENEFITS ? 'bnf-manager__btn--disabled' : ''}`;
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
      const closeBtn = document.querySelector('.bnf-select__close');
      if (closeBtn) {
          closeBtn.addEventListener('click', () => this.closeModal());
      }

      const applyBtn = document.querySelector('.bnf-select__btn--apply');
      if (applyBtn) {
          applyBtn.addEventListener('click', () => this.closeModal());
      }

      const confirmBtn = document.querySelector('.bnf-select__btn--confirm');
      if (confirmBtn) {
          confirmBtn.addEventListener('click', () => this.confirmSelection());
      }

      const searchBtn = document.querySelector('.bnf-select__search-btn');
      if (searchBtn) {
          searchBtn.addEventListener('click', () => this.handleSearch());
      }

      /* 혜택 등록 */
      const addBtn = document.querySelector('#btnBenefitAdd');
      if (addBtn) {
          addBtn.addEventListener('click', () => this.addShowModal());
      }
      const addCloseBtn = document.querySelector('.bnf-select-close3');
      if (addCloseBtn) {
          addCloseBtn.addEventListener('click', () => this.addHideModal());
      }
      const addCloseBtn2 = document.querySelector('#bnfAddModal .bnf-select__close');
      if (addCloseBtn2) {
          addCloseBtn2.addEventListener('click', () => this.addHideModal());
      }

      /* 혜택 수정 */
      const modifyBtns = document.querySelectorAll('.bnf-btn-modify');
      if (modifyBtns.length > 0) {
          modifyBtns.forEach((btn) => {
              btn.addEventListener('click', () => this.modifyShowModal());
          });
      }
      const modifyCloseBtn = document.querySelector('.bnf-select-close4');
      if (modifyCloseBtn) {
        modifyCloseBtn.addEventListener('click', () => this.modifyHideModal());
      }
      const modifyCloseBtn2 = document.querySelector('#bnfModifyModal .bnf-select__close');
      if (modifyCloseBtn2) {
        modifyCloseBtn2.addEventListener('click', () => this.modifyHideModal());
      }
  }

  handleRowSelection() {
      const rows = document.querySelectorAll('.bnf-select__row');
      rows.forEach(row => {
          row.addEventListener('click', () => {
              rows.forEach(r => r.classList.remove('bnf-select__row--selected'));
              row.classList.add('bnf-select__row--selected');
              const radio = row.querySelector('.bnf-select__radio');
              if (radio) radio.checked = true;
          });
      });
  }

  handleSearch() {
      const searchInput = document.querySelector('.bnf-select__input');
      if (searchInput) {
          const searchValue = searchInput.value;
          console.log('Searching for:', searchValue);
      }
  }

  confirmSelection() {
      const selectedRow = document.querySelector('.bnf-select__row--selected');
      if (selectedRow && this.manager) {
          const benefitCell = selectedRow.querySelector('.bnf-select__td:nth-child(2)');
          if (benefitCell) {
              const benefit = benefitCell.textContent;
              // 혜택 추가 전에 최대 개수 체크는 manager에서 처리
              this.manager.addBenefit(benefit);
          }
      }
      this.closeModal();
  }

  closeModal() {
      const overlay = document.querySelector('.bnf-select__overlay');
      const modal = document.querySelector('.bnf-select');
      if (overlay) overlay.style.display = 'none';
      if (modal) modal.style.display = 'none';
  }

  showModal() {
      const overlay = document.querySelector('.bnf-select__overlay');
      const modal = document.querySelector('.bnf-select');
      if (overlay) overlay.style.display = 'block';
      if (modal) modal.style.display = 'block';
  }

  addShowModal() {
      const modal = document.querySelector('#bnfManageModal');
      if (modal) modal.style.display = 'none';

      const modalAdd = document.querySelector('#bnfAddModal');
      if (modalAdd) modalAdd.style.display = 'block';
  }
  addHideModal() {
    const modal = document.querySelector('#bnfAddModal');
    if (modal) modal.style.display = 'none';
  }

  modifyShowModal() {
    const modal = document.querySelector('#bnfManageModal');
    if (modal) modal.style.display = 'none';

    const modalmodify = document.querySelector('#bnfModifyModal');
    if (modalmodify) modalmodify.style.display = 'block';
  }
  modifyHideModal() {
    const modal = document.querySelector('#bnfModifyModal');
    if (modal) modal.style.display = 'none';
  }
}


document.addEventListener('DOMContentLoaded', () => {
  const benefitManager = new BenefitManagerSystem();
});

/* 사은품 */
/* Gift Manager System */
class GiftManagerSystem {
  constructor() {
      this.gifts = [];
      this.currentIndex = null;
      this.selectModal = null;
      this.MAX_GIFTS = 3; // 최대 사은품 개수 설정
      this.init();
      this.initManageButton();
  }

  initManageButton() {
    const manageBtn = document.getElementById('giftManageBtn');
    if (manageBtn) {
        manageBtn.addEventListener('click', () => {
            this.showManageModal();
        });
    }
  }

  showManageModal() {
      const overlay = document.getElementById('giftManageOverlay');
      const modal = document.getElementById('giftManageModal');
      if (overlay) overlay.style.display = 'block';
      if (modal) modal.style.display = 'block';
  }

  hideManageModal() {
      const overlay = document.getElementById('giftManageOverlay');
      const modal = document.getElementById('giftManageModal');
      if (overlay) overlay.style.display = 'none';
      if (modal) modal.style.display = 'none';
  }

  initManageModal() {
      const closeBtn = document.querySelector('#giftManageModal .gift-select__close');
      if (closeBtn) {
          closeBtn.addEventListener('click', () => this.hideManageModal());
      }
      const closeBtn2 = document.querySelector('#giftManageModal .gift-select-close2');
      if (closeBtn2) {
          closeBtn2.addEventListener('click', () => this.hideManageModal());
      }

      const closeFooterBtn = document.querySelector('#giftManageModal .gift-select__btn--apply');
      if (closeFooterBtn) {
          closeFooterBtn.addEventListener('click', () => this.hideManageModal());
      }

      const overlay = document.getElementById('giftManageOverlay');
      if (overlay) {
          overlay.addEventListener('click', () => this.hideManageModal());
      }

      // 수정 버튼 이벤트 리스너
      const editBtns = document.querySelectorAll('#giftManageModal .gift-select__edit-btn');
      editBtns.forEach(btn => {
          btn.addEventListener('click', (e) => {
              const row = e.target.closest('.gift-select__row');
              const title = row.querySelector('.gift-select__td:nth-child(2)').textContent;
              console.log('수정 클릭:', title);
          });
      });
  }

  init() {
      this.selectModal = new GiftSelectModal(this);
      this.initManageButton();
      this.initManageModal();

      const addBtn = document.getElementById('giftAddBtn');
      if (addBtn) {
          addBtn.addEventListener('click', () => this.handleAddClick());
      }

      this.renderInputs();
  }

  handleAddClick() {
      if (this.gifts.length >= this.MAX_GIFTS) {
          alert('3개 이상 등록할 수 없습니다');
          return;
      }
      this.showSelectModal();
  }

  showSelectModal() {
      this.selectModal.showModal();
  }

  addGift(gift) {
      if (this.gifts.length >= this.MAX_GIFTS) {
          alert('3개 이상 등록할 수 없습니다');
          return;
      }
      this.gifts.push(gift);
      this.renderInputs();
  }

  removeGift(index) {
      this.gifts.splice(index, 1);
      this.renderInputs();
  }

  moveGift(index, direction) {
      if (direction === 'up' && index > 0) {
          [this.gifts[index], this.gifts[index - 1]] = 
          [this.gifts[index - 1], this.gifts[index]];
      } else if (direction === 'down' && index < this.gifts.length - 1) {
          [this.gifts[index], this.gifts[index + 1]] = 
          [this.gifts[index + 1], this.gifts[index]];
      }
      this.renderInputs();
  }

  renderInputs() {
      const container = document.getElementById('giftInputs');
      if (!container) return;

      container.innerHTML = '';

      this.gifts.forEach((gift, index) => {
          const inputWrapper = document.createElement('div');
          inputWrapper.className = 'gift-manager__input-wrapper';

          const input = document.createElement('input');
          input.type = 'text';
          input.className = 'gift-manager__input';
          input.value = gift;
          input.readOnly = true;

          const btnGroup = document.createElement('div');
          btnGroup.className = 'gift-manager__btn-group';

          const upBtn = document.createElement('button');
          upBtn.className = `gift-manager__btn ${index === 0 ? 'gift-manager__btn--disabled' : ''}`;
          upBtn.textContent = '▲';
          upBtn.disabled = index === 0;
          upBtn.onclick = () => this.moveGift(index, 'up');

          const downBtn = document.createElement('button');
          downBtn.className = `gift-manager__btn ${index === this.gifts.length - 1 ? 'gift-manager__btn--disabled' : ''}`;
          downBtn.textContent = '▼';
          downBtn.disabled = index === this.gifts.length - 1;
          downBtn.onclick = () => this.moveGift(index, 'down');

          const deleteBtn = document.createElement('button');
          deleteBtn.className = 'gift-manager__btn';
          deleteBtn.textContent = 'X';
          deleteBtn.onclick = () => this.removeGift(index);

          btnGroup.appendChild(upBtn);
          btnGroup.appendChild(downBtn);
          btnGroup.appendChild(deleteBtn);

          inputWrapper.appendChild(input);
          inputWrapper.appendChild(btnGroup);
          container.appendChild(inputWrapper);
      });

      const addBtn = document.getElementById('giftAddBtn');
      if (addBtn) {
          addBtn.disabled = this.gifts.length >= this.MAX_GIFTS;
          addBtn.className = `gift-manager__btn ${this.gifts.length >= this.MAX_GIFTS ? 'gift-manager__btn--disabled' : ''}`;
      }
  }
}


class GiftSelectModal {
  constructor(manager) {
      this.manager = manager;
      this.init();
  }

  init() {
      this.addEventListeners();
      this.handleRowSelection();
  }

  addEventListeners() {
      const closeBtn = document.querySelector('.gift-select__close');
      if (closeBtn) {
          closeBtn.addEventListener('click', () => this.closeModal());
      }

      const applyBtn = document.querySelector('.gift-select__btn--apply');
      if (applyBtn) {
          applyBtn.addEventListener('click', () => this.closeModal());
      }

      const confirmBtn = document.querySelector('.gift-select__btn--confirm');
      if (confirmBtn) {
          confirmBtn.addEventListener('click', () => this.confirmSelection());
      }

      const searchBtn = document.querySelector('.gift-select__search-btn');
      if (searchBtn) {
          searchBtn.addEventListener('click', () => this.handleSearch());
      }

      /* 사은품 등록 */
      const addBtn = document.querySelector('#btnGiftAdd');
      if (addBtn) {
          addBtn.addEventListener('click', () => this.addShowModal());
      }
      const addCloseBtn = document.querySelector('.gift-select-close3');
      if (addCloseBtn) {
          addCloseBtn.addEventListener('click', () => this.addHideModal());
      }
      const addCloseBtn2 = document.querySelector('#giftAddModal .gift-select__close');
      if (addCloseBtn2) {
          addCloseBtn2.addEventListener('click', () => this.addHideModal());
      }

      /* 사은품 수정 */
      const modifyBtns = document.querySelectorAll('.gift-btn-modify');
      if (modifyBtns.length > 0) {
          modifyBtns.forEach((btn) => {
              btn.addEventListener('click', () => this.modifyShowModal());
          });
      }
      const modifyCloseBtn = document.querySelector('.gift-select-close4');
      if (modifyCloseBtn) {
        modifyCloseBtn.addEventListener('click', () => this.modifyHideModal());
      }
      const modifyCloseBtn2 = document.querySelector('#giftModifyModal .gift-select__close');
      if (modifyCloseBtn2) {
        modifyCloseBtn2.addEventListener('click', () => this.modifyHideModal());
      }
  }

  handleRowSelection() {
      const rows = document.querySelectorAll('.gift-select__row');
      rows.forEach(row => {
          row.addEventListener('click', () => {
              rows.forEach(r => r.classList.remove('gift-select__row--selected'));
              row.classList.add('gift-select__row--selected');
              const radio = row.querySelector('.gift-select__radio');
              if (radio) radio.checked = true;
          });
      });
  }

  handleSearch() {
      const searchInput = document.querySelector('.gift-select__input');
      if (searchInput) {
          const searchValue = searchInput.value;
          console.log('Searching for:', searchValue);
      }
  }

  confirmSelection() {
      const selectedRow = document.querySelector('.gift-select__row--selected');
      if (selectedRow && this.manager) {
          const giftCell = selectedRow.querySelector('.gift-select__td:nth-child(2)');
          if (giftCell) {
              const gift = giftCell.textContent;
              this.manager.addGift(gift);
          }
      }
      this.closeModal();
  }

  closeModal() {
      const overlay = document.querySelector('.gift-select__overlay');
      const modal = document.querySelector('.gift-select');
      if (overlay) overlay.style.display = 'none';
      if (modal) modal.style.display = 'none';
  }

  showModal() {
      const overlay = document.querySelector('.gift-select__overlay');
      const modal = document.querySelector('.gift-select');
      if (overlay) overlay.style.display = 'block';
      if (modal) modal.style.display = 'block';
  }

  addShowModal() {
      const modal = document.querySelector('#giftManageModal');
      if (modal) modal.style.display = 'none';

      const modalAdd = document.querySelector('#giftAddModal');
      if (modalAdd) modalAdd.style.display = 'block';
  }
  
  addHideModal() {
    const modal = document.querySelector('#giftAddModal');
    if (modal) modal.style.display = 'none';
  }

  modifyShowModal() {
    const modal = document.querySelector('#giftManageModal');
    if (modal) modal.style.display = 'none';

    const modalmodify = document.querySelector('#giftModifyModal');
    if (modalmodify) modalmodify.style.display = 'block';
  }
  
  modifyHideModal() {
    const modal = document.querySelector('#giftModifyModal');
    if (modal) modal.style.display = 'none';
  }
}

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
  const giftManager = new GiftManagerSystem();
});



const BannerUploader = {
  trigger(inputId) {
      document.getElementById(inputId).click();
  },

  preview(input, previewId) {
      const previewEl = document.getElementById(previewId);
      previewEl.innerHTML = '';
      
      if (input.files && input.files[0]) {
          const reader = new FileReader();
          
          reader.onload = (e) => {
              const img = document.createElement('img');
              img.src = e.target.result;
              img.classList.add('uploader__preview-image');
              previewEl.appendChild(img);
              
              // 부모 요소에 active 상태 클래스 추가
              input.closest('.event-banner__uploader')
                   .classList.add('uploader--active');
          }
          
          reader.readAsDataURL(input.files[0]);
      }
  }
};



// service_add.html 요금제 추가/이동/삭제 UI
document.addEventListener('DOMContentLoaded', function() {
  const container = document.querySelector('.cloneitems__outer');
  
  if (container) {
      // 초기 템플릿 저장
      const templateItem = container.querySelector('.cloneitems').cloneNode(true);
      
      // 초기 라디오 name과 id 업데이트
      updateAllRadioNames();

      // 항목 추가 이벤트 처리
      container.addEventListener('click', function(e) {
          if (e.target.classList.contains('btn-primary')) {
              const items = container.querySelectorAll('.cloneitems');
              if (items.length >= 3) return; // 최대 3개까지만 허용

              // items가 없을 경우 저장된 템플릿 사용, 있을 경우 첫번째 아이템 복제
              const newItem = items.length === 0 ? templateItem.cloneNode(true) : items[0].cloneNode(true);
              
              // 버튼 상태 업데이트
              updateButtons(newItem);
              
              // 추가 버튼 다음에 새 아이템 삽입
              const addButton = container.querySelector('.btn-primary');
              addButton.after(newItem);
              
              updateAllItemsButtons();
              updateAllRadioNames(); // 새 아이템 추가 후 라디오 이름 업데이트
          }
      });

      // 항목 삭제 이벤트 처리
      container.addEventListener('click', function(e) {
          if (e.target.classList.contains('btn-danger')) {
              e.target.closest('.cloneitems').remove();
              updateAllItemsButtons();
              updateAllRadioNames(); // 삭제 후 라디오 이름 업데이트
          }
      });

      // 위치 변경 버튼 이벤트 처리
      container.addEventListener('click', function(e) {
          if (e.target.textContent === '▲' || e.target.textContent === '▼') {
              const currentItem = e.target.closest('.cloneitems');
              const items = Array.from(container.querySelectorAll('.cloneitems'));
              const currentIndex = items.indexOf(currentItem);
              
              if (e.target.textContent === '▲' && currentIndex > 0) {
                  container.insertBefore(currentItem, items[currentIndex - 1]);
              } else if (e.target.textContent === '▼' && currentIndex < items.length - 1) {
                  container.insertBefore(currentItem, items[currentIndex + 1].nextSibling);
              }
              
              updateAllItemsButtons();
              updateAllRadioNames(); // 위치 변경 후 라디오 이름 업데이트
          }
      });

      function updateButtons(item) {
          const btnBox = item.querySelector('.cloneitems__btnbox');
          const addBtn = item.querySelector('.btn-primary');
          
          if (addBtn) addBtn.remove();
      }

      function updateAllItemsButtons() {
          const items = Array.from(container.querySelectorAll('.cloneitems'));
          
          items.forEach((item, index) => {
              const btnBox = item.querySelector('.cloneitems__btnbox');
              
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
          const items = Array.from(container.querySelectorAll('.cloneitems'));
          
          items.forEach((item, itemIndex) => {
              const radios = item.querySelectorAll('input[type="radio"]');
              const uniqueGroupName = `exposure_${timestamp}_${itemIndex}`; // 고유한 name 생성
              
              radios.forEach((radio, radioIndex) => {
                  const oldId = radio.id;
                  const baseId = radio.id.replace(/\d+.*$/, ''); // 숫자로 끝나는 부분 제거
                  const newId = `${baseId}${itemIndex}_${radioIndex}_${timestamp}`;
                  
                  radio.id = newId;
                  radio.name = uniqueGroupName;
                  
                  const label = item.querySelector(`label[for="${oldId}"]`);
                  if (label) {
                      label.setAttribute('for', newId);
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
    datesetRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            const dateTimeInputs = document.querySelectorAll('input[type="date"], input[type="time"]');
            const isDateRangeEnabled = document.getElementById('dateset2').checked;
            
            dateTimeInputs.forEach(input => {
                input.disabled = !isDateRangeEnabled;
            });
        });
    });
}

document.addEventListener('DOMContentLoaded', initDateSetControl);


function initFileUpload() {
    // 파일 input 요소들을 찾음
    const fileInputs = document.querySelectorAll('.file-input');
    if (fileInputs.length === 0) return;

    // 초기 이미지 확인 및 설정
    function initializePreview(inputNum) {
        const previewImg = document.getElementById(`preview${inputNum}`);
        const textInput = document.getElementById(`fileText${inputNum}`);
        
        if (previewImg && previewImg.src && previewImg.src !== window.location.href) {
            previewImg.style.display = 'block';
            // URL에서 파일명 추출
            const fileName = previewImg.src.split('/').pop();
            if (textInput) {
                textInput.value = fileName;
            }
        }
    }

    fileInputs.forEach(fileInput => {
        // 초기 이미지 확인
        const inputNum = fileInput.id.replace('fileInput', '');
        initializePreview(inputNum);

        fileInput.addEventListener('change', function(e) {
            const inputNum = this.id.replace('fileInput', '');
            const textInput = document.getElementById(`fileText${inputNum}`);
            const previewImg = document.getElementById(`preview${inputNum}`);
            
            if (!textInput || !previewImg) return;

            const file = e.target.files[0];
            
            if (file) {
                const validExtensions = ['jpg', 'jpeg', 'png'];
                const fileExtension = file.name.split('.').pop().toLowerCase();
                
                if (!validExtensions.includes(fileExtension)) {
                    alert('이미지 형태의 파일을 선택해주세요.');
                    this.value = '';
                    textInput.value = '';
                    previewImg.style.display = 'none';
                    return;
                }
                
                textInput.value = file.name;

                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImg.src = e.target.result;
                    previewImg.style.display = 'block';
                }
                reader.readAsDataURL(file);
            } else {
                textInput.value = '';
                previewImg.style.display = 'none';
            }
        });
    });
}

// DOM이 로드된 후 초기화 함수 실행
document.addEventListener('DOMContentLoaded', initFileUpload);


/* 250221 */
function initFileInput() {
    if (!document.getElementById('fileInput')) return
    document.getElementById('fileInput').addEventListener('change', function(e) {
        const fileNameElement = document.querySelector('.file-name');
        if(this.files.length > 0) {
            const file = this.files[0];
            const fileName = file.name;
            const fileExt = fileName.split('.').pop().toLowerCase();
            
            // 엑셀 파일 확장자 체크
            if(['xls', 'xlsx'].includes(fileExt)) {
                fileNameElement.textContent = fileName;
            } else {
                alert('엑셀 파일이 아닙니다');
                this.value = ''; // 파일 선택 초기화
                fileNameElement.textContent = '파일첨부';
            }
        } else {
            fileNameElement.textContent = '파일첨부';
        }
    });
}


document.addEventListener('DOMContentLoaded', () => {
    initFileInput()
});


/* product 요금제 추가 스크립트 250228 */
document.addEventListener('DOMContentLoaded', function() {
    // 영업 상품 등록 상세 컨텍스트를 지정하여 요소 충돌 방지
    const productDetailSection = document.querySelector('.search-section.detail');
    
    // 해당 섹션이 없으면 실행하지 않음
    if (!productDetailSection) return;
    
    // 요금제 팝업 관련 요소들을 특정 컨텍스트 내에서 찾기
    const bnfAddBtn = productDetailSection.querySelector('#bnfAddBtn');
    const bnfSelectOverlay = productDetailSection.querySelector('.bnf-select__overlay');
    const bnfSelect = productDetailSection.querySelector('.bnf-select');
    
    // 각 요소가 존재하는지 확인
    if (!bnfAddBtn || !bnfSelectOverlay || !bnfSelect) return;
    
    const bnfSelectClose = bnfSelect.querySelector('.bnf-select__close');
    const bnfSelectApply = bnfSelect.querySelector('.bnf-select__btn--apply');
    const bnfSelectConfirm = bnfSelect.querySelector('.bnf-select__btn--confirm');
    const rateplanTable = productDetailSection.querySelector('.table.table-bordered');
    const deleteSelectedBtn = productDetailSection.querySelector('.btn.btn-danger');
    
    // 이 함수 내에서만 사용되는 고유한 네임스페이스를 가진 이벤트 핸들러 함수들
    const ratePlanManagement = {
        // 요금제 팝업 열기
        openPopup: function() {
            bnfSelectOverlay.style.display = 'block';
            bnfSelect.style.display = 'block';
        },
        
        // 요금제 팝업 닫기
        closePopup: function() {
            bnfSelectOverlay.style.display = 'none';
            bnfSelect.style.display = 'none';
            // 라디오 버튼 선택 해제
            bnfSelect.querySelectorAll('.bnf-select__radio').forEach(radio => {
                radio.checked = false;
            });
        },
        
        // 요금제 등록
        confirmRatePlan: function() {
            // 선택된 라디오 버튼 찾기 (특정 컨텍스트 내에서만)
            const selectedRadio = bnfSelect.querySelector('.bnf-select__radio:checked');
            
            if (selectedRadio) {
                // 선택된 행(tr)
                const selectedRow = selectedRadio.closest('.bnf-select__row');
                
                // UUID와 요금제명 가져오기
                const mno = selectedRow.querySelectorAll('.bnf-select__td')[1].textContent;
                const network = selectedRow.querySelectorAll('.bnf-select__td')[2].textContent;
                // 충돌 방지를 위한 고유 접두사 추가
                const uuid = `semotong_${mno}${network}${Math.floor(Math.random() * 10000).toString().padStart(5, '0')}`;
                const ratePlanName = selectedRow.querySelectorAll('.bnf-select__td')[4].textContent.trim();

                // 이미 추가된 UUID인지 확인
                const existingUuids = Array.from(rateplanTable.querySelectorAll('tr')).slice(1).map(row => {
                    const cells = row.querySelectorAll('td');
                    return cells.length > 1 ? cells[1].textContent : '';
                });
                
                if (existingUuids.includes(uuid)) {
                    alert('이미 추가된 요금제입니다.');
                    return;
                }
                
                // 새 행 추가
                const newRow = document.createElement('tr');
                newRow.innerHTML = `
                    <td><input type="checkbox" class="rateplan-checkbox"></td>
                    <td>${uuid}</td>
                    <td><a href="javascript:;">${ratePlanName}</a></td>
                `;
                
                // 테이블에 행 추가
                if (rateplanTable.querySelector('tbody')) {
                    rateplanTable.querySelector('tbody').appendChild(newRow);
                } else {
                    rateplanTable.appendChild(newRow);
                }
                
                // 팝업 닫기
                ratePlanManagement.closePopup();
            } else {
                alert('요금제를 선택해주세요.');
            }
        },
        
        // 선택된 요금제 삭제
        deleteSelectedRatePlans: function() {
            // 모든 체크박스 찾기 (첫 번째 행(헤더)은 제외)
            const checkboxes = Array.from(rateplanTable.querySelectorAll('tr:not(:first-child) input[type="checkbox"]:checked'));
            
            if (checkboxes.length === 0) {
                alert('삭제할 요금제를 선택해주세요.');
                return;
            }
            
            // 사용자 확인
            if (confirm('선택한 요금제를 삭제하시겠습니까?')) {
                // 체크된 행 삭제
                checkboxes.forEach(checkbox => {
                    const row = checkbox.closest('tr');
                    if (row) row.remove();
                });
            }
        },
        
        // 헤더 체크박스 토글
        toggleHeaderCheckbox: function() {
            const headerCheckbox = rateplanTable.querySelector('tr:first-child input[type="checkbox"]');
            if (!headerCheckbox) return;
            
            const isChecked = headerCheckbox.checked;
            // 모든 행의 체크박스 선택
            const allCheckboxes = rateplanTable.querySelectorAll('tr:not(:first-child) input[type="checkbox"]');
            allCheckboxes.forEach(checkbox => {
                checkbox.checked = isChecked;
            });
        }
    };
    
    // 이벤트 리스너 연결
    bnfAddBtn.addEventListener('click', ratePlanManagement.openPopup);
    
    if (bnfSelectClose) {
        bnfSelectClose.addEventListener('click', ratePlanManagement.closePopup);
    }
    
    if (bnfSelectApply) {
        bnfSelectApply.addEventListener('click', ratePlanManagement.closePopup);
    }
    
    if (bnfSelectConfirm) {
        bnfSelectConfirm.addEventListener('click', ratePlanManagement.confirmRatePlan);
    }
    
    if (deleteSelectedBtn) {
        deleteSelectedBtn.addEventListener('click', ratePlanManagement.deleteSelectedRatePlans);
    }
    
    // 헤더 체크박스 클릭 시 모든 체크박스 선택/해제
    const headerCheckbox = rateplanTable.querySelector('tr:first-child input[type="checkbox"]');
    if (headerCheckbox) {
        headerCheckbox.addEventListener('change', ratePlanManagement.toggleHeaderCheckbox);
    }
});