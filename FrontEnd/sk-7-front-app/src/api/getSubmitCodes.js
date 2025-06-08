// /src/api/getSubmitCodes.js

import axios from "axios";

export default async function getSubmitCodes(submitProblemId) {
    try {
        const token = localStorage.getItem("accessToken");

        const response = await axios.get(
            `${
                import.meta.env.VITE_API_URL
            }/api/member/problem/submitcodes/${submitProblemId}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }
        );

        if (response.status === 200) {
            return response.data.data;
        } else {
            throw new Error("제출 코드 불러오기 실패");
        }
    } catch (err) {
        console.error("제출 코드 요청 실패", err);
    }
}
