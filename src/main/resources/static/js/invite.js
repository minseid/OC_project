function adjustContentScale() {
    const image = document.querySelector('.main-image');
    const margin = document.querySelector('.z-1');
    const windowHeight = window.innerHeight;

    let scale = 1;

    scale = image.scrollHeight*0.8;
    margin.style.marginBottom = scale + 'px';
}

function redirectToAppStore() {
    // userAgent를 통해 안드로이드와 iOS 구분
    var userAgent = navigator.userAgent || navigator.vendor || window.opera;

    // Android 기기
    if (/android/i.test(userAgent)) {
        window.location.href = "https://www.naver.com";  // 안드로이드 링크
    }
    // iOS 기기
    else if (/iPhone|iPad|iPod/i.test(userAgent)) {
        window.location.href = "https://www.google.com";  // iOS 링크
    }
    else {
        // 기타 기기 (웹 브라우저 등)
        alert("앱 설치를 지원하는 모바일 기기에서 접속해 주세요.");
    }
}

// 이미지에 변환 효과를 적용하는 함수
function adjustImageTransform() {
    const img = document.querySelector('.main-image');
    if (window.innerWidth > 800) {
        img.style.transform = 'translate(0, -1vh)';  // 브라우저 너비가 800px보다 크면 translateX를 0으로 설정
    } else {
        img.style.transform = 'translate(-6vw, -5vh)';  // 그렇지 않으면 translateX를 -5vw로 설정
    }
}

function checkDeviceAndRedirect(event) {
    event.preventDefault();
    // 사용자 에이전트 확인
    const userAgent = navigator.userAgent.toLowerCase();

    // 모바일 기기인지 확인 (Android, iOS 확인)
    const isAndroid = userAgent.indexOf("android") > -1;
    const isIOS = /iphone|ipod|ipad/i.test(userAgent);

    // Android 기기
    if (/android/i.test(userAgent)||/iPhone|iPad|iPod/i.test(userAgent)) {
        window.location.href = event.target.getAttribute('href');
    }
    else {
        // 기타 기기 (웹 브라우저 등)
        alert("앱 설치를 지원하는 모바일 기기에서 접속해 주세요.");
    }
}

// 브라우저 크기가 변경될 때마다 호출되도록 설정
window.addEventListener('resize', () => {
    adjustImageTransform();
    //adjustContentScale();
});

window.addEventListener('load', () => {
    adjustImageTransform();
    //adjustContentScale();
})