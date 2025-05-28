// 로그인 요청 함수

import axios from "axios";

export async function loginTask({ email, encryptedPassword }) {
    try {
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/account/login`,
            {
                email,
                password: encryptedPassword,
            }
        );
        return response.data;
    } catch (error) {
        // 에러 출력
        console.error(error);
        throw error;
    }
}
