import axios from "axios";

export default async function getProblemDetail(id) {
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/problem/detail/${id}`
        );
        return response?.data?.data;
    } catch (e) {
        throw new Error("문제 상세 정보 불러오기 오류", e);
    }
}
