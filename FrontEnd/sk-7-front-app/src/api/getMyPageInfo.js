// 토큰으로 마이페이지 정보 가져오기

import axios from "axios";

export default async function getMyPageInfo() {
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/mypage/mypage`,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }
        );

        console.log("응답 결과 : ", response.data);

        // axios에서는 response.ok 대신 status 확인
        if (response.status === 200) {
            console.log("마이페이지 정보 가져오기 성공");
            return response.data.data;
        } else {
            console.error("마이페이지 정보 가져오기 실패", response.status);
            throw new Error("마이페이지 정보 가져오기 실패");
        }
    } catch (error) {
        throw (
            error.response?.data?.message ||
            error.message ||
            "마이페이지 정보 가져오기 실패"
        );
    }
}
