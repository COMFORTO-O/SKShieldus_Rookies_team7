import axios from "axios";

export const getProblemList = async ({
    title,
    category,
    level,
    status,
    page,
}) => {
    console.log(
        `요청\ntitle:${title}\ncategory:${category}\nlevel:${level}\nstatus:${status}\npage:${page}`
    );
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/problem/page`,
            {
                params: {
                    title: title,
                    category: category,
                    level: level,
                    status: status,
                    page: page,
                },
                headers: {
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }
        );

        return response.data;
    } catch (error) {
        // 에러 출력
        throw (
            error.response?.data?.message ||
            error.message ||
            "목록 불러오기 실패"
        );
    }
};
