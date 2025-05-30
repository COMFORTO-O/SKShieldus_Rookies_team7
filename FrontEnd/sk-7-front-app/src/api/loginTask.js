// 로그인 요청 함수

import axios from "axios";

export async function loginTask({ email, encryptedPassword }) {
    try {
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/account/login`,
            {
                email: email,
                password: encryptedPassword,
            },
            {
                headers: {
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }
        );

        return { success: true, data: response.data };
    } catch (error) {
        // 에러 출력
        // 에러 코드가 401 (Unauthorized)
        if (error?.status === 401) {
            throw {
                success: false,
                status: error?.status,
                message: "아이디 또는 비밀번호 오류",
            };
        } else {
            throw {
                success: false,
                status: error?.status,
                message: "로그인 실패",
            };
        }
    }
}
