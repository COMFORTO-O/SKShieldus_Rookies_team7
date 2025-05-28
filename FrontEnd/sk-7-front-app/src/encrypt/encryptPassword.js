import { JSEncrypt } from "jsencrypt";

export function encryptPassword(password) {
    const pk = import.meta.env.VITE_PUBLIC_KEY.replace(/\\n/g, "\n");

    // JSEncrypt 객체 생성
    const encrypt = new JSEncrypt();

    // 공개키 설정
    encrypt.setPublicKey(pk);

    // 패스워드 암호화
    // encrypt.encrypt() 메서드는 암호화된 결과를 Base64 인코딩된 문자열로 반환
    const encryptedPassword = encrypt.encrypt(password);

    // console.log("공개키 : ", pk);
    // console.log("rsa 결과 : ", encryptedPassword);

    // Base64 인코딩
    const base64Password = btoa(encryptedPassword);

    if (base64Password) {
        return base64Password;
    } else {
        console.error("암호화 실패 : Check public key or password.");
        return null; // 또는 오류 처리
    }
}
