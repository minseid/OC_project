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

// 화면비율에 따른 반응형
function adjustImageTransform() {
    const img = document.querySelector('.main-image');
    const whole_container = document.querySelector('.whole_container');
    const title = document.querySelector('#title');
    if(window.innerHeight < window.innerWidth) {
        title.classList.remove('fs-1');
        title.classList.add('fs-2');
        whole_container.style.maxWidth = window.innerHeight * 0.71 + 'px';
        img.style.maxWidth = window.innerHeight * 0.71 + 'px';
        img.style.transform = 'translate(0, -1vh)';
    } else {
        whole_container.style.maxWidth = '500px';
        img.style.maxWidth = '500px';
        img.style.transform = 'translate(-6vw, -5vh)';
        if(whole_container.scrollHeight > window.innerHeight) {
            whole_container.style.setProperty('margin-top', (whole_container.scrollHeight - window.innerHeight)/3*-2 + 'px', 'important')
        }
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

function kakaoToOut() {
    var userAgent = navigator.userAgent || navigator.vendor || window.opera;
    const target_url = location.href;

    if(userAgent.match(/kakaotalk/i)) {
        location.href = 'kakaotalk://web/openExternal?url='+encodeURIComponent(target_url);
    }
}
// 브라우저 크기가 변경될 때마다 호출되도록 설정
window.addEventListener('resize', () => {
    adjustImageTransform();
});

window.addEventListener('load', () => {
    const openLink = document.getElementById('openLink');
    const installLink = document.getElementById('installLink');
    if (openLink) {
        openLink.addEventListener('click', function (event) {
            checkDeviceAndRedirect(event);
        });
    }
    if(installLink) {
        installLink.addEventListener('click', redirectToAppStore)
    }
    adjustImageTransform();
    kakaoToOut();
})