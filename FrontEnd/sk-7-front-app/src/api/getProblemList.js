import axios from "axios";

export const getProblemList = async ({
    title,
    category,
    level,
    status,
    page,
}) => {
    console.log(
        `요청 : {\n${title}\n${category}\n${level}\n${status}\n${page}`
    );
    try {
        const isSolved = status ? "solved" : "unsolved";

        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/problem/page`,
            {
                title: title,
                category: category,
                level: level,
                status: isSolved,
                page: page,
            },
            {
                headers: {
                    "Content-Type": "application/json",
                },
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
