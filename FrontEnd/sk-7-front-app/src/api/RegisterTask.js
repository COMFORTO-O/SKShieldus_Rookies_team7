// 회원가입 요청 함수
import axios from "axios";

export async function RegisterTask({ email, encryptedPassword, name, phone }) {
    try {
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/account/register`,
            {
                email: email,
                password: encryptedPassword,
                name: name,
                phone: phone,
            },
            {
                headers: {
                    "Content-Type": "application/json",
                },
            }
        );

        if (response.status !== 200) {
            throw new Error("회원가입 요청에 실패했습니다.");
        }
        return response.data;
    } catch (error) {
        // 에러 출력
        console.error(error);
        throw error;
    }
}
