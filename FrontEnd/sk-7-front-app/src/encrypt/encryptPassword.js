import { JSEncrypt } from "jsencrypt";

export function encryptPassword(password) {
    const pk = import.meta.env.VITE_PUBLIC_KEY.replace(/\\n/g, "\n");

    const pk2 = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlIXzApAhtg0zrG9v5XqR
NcvqE7opNnlaK3j7OSNO/HfQmzDLeSPWRXDIQtjzG9imVGa5eeIf7qJQH9RfuMeE
EUla8D0ZLXyZN68GzHMhKDA7y5lRFl+uYBmGWkd/54V60YWXEG7mBJdmtqMK7/qb
5erlKl6WL0UWuNd6XyaLqrYbK7Zw1w2It9iYRJp1zMZ4sA5DicyrKcvnR99XsQN3
yTYrpcpxwLInYqMUBP7kvGy1WGU5rRVgv2dwJXUmSV/1tVbeVYfugaPAUql3PfI/
BTW11ehxFv8twmdX93GU8OFrrxVl/DL5tJjFLVO+4Jg53D1zGzJuegxI7RAl7CsX
PQIDAQAB
-----END PUBLIC KEY-----`;

    // JSEncrypt 객체 생성
    const encrypt = new JSEncrypt();

    // 공개키 설정
    encrypt.setPublicKey(pk2);

    // 패스워드 암호화
    // encrypt.encrypt() 메서드는 암호화된 결과를 Base64 인코딩된 문자열로 반환
    const encryptedPassword = encrypt.encrypt(password);

    // console.log("공개키 : ", pk2);
    // console.log("rsa 결과 : ", encryptedPassword);

    if (encryptedPassword) {
        return encryptedPassword;
    } else {
        console.error("암호화 실패 : Check public key or password.");
        return null; // 또는 오류 처리
    }
}
