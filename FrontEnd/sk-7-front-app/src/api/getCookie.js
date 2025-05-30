// 특정 이름의 쿠키 값을 가져오는 헬퍼 함수
export function getCookie(name) {
    const cookies = document.cookie.split(";"); // 모든 쿠키를 ';' 기준으로 나눔
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim(); // 각 쿠키의 앞뒤 공백 제거
        // 원하는 이름으로 시작하는 쿠키인지 확인
        if (cookie.startsWith(name + "=")) {
            return cookie.substring(name.length + 1); // '=' 다음의 값 부분만 반환
        }
    }
    return null; // 해당 이름의 쿠키가 없으면 null 반환
}
