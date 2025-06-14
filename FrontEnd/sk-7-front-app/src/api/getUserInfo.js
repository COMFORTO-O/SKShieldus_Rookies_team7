// 토큰으로 사용자 정보 가져오기

import axios from "axios";

export default async function getUserInfo() {
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/member/info`,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem(
                        "accessToken"
                    )}`,
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }
        );

        console.log("응답 결과 : ", response.data);
        if (response.data.status === 200) {
            console.log("사용자 정보 가져오기 성공");
            return response?.data?.data;
        } else {
            console.error("사용자 정보 가져오기 실패", response.status);
            throw new Error("사용자 정보 가져오기 실패");
        }
    } catch (error) {
        // 에러 출력
        throw (
            error.response?.data?.message ||
            error.message ||
            "사용자 정보 가져오기 실패"
        );
    }
}
