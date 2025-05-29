// 토큰으로 사용자 정보 가져오기

import axios from "axios";

export default async function getUserInfo() {
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/account/info`
        );
        return response.data;
    } catch (error) {
        // 에러 출력
        throw (
            error.response?.data?.message ||
            error.message ||
            "정보 가져오기 오류"
        );
    }
}
