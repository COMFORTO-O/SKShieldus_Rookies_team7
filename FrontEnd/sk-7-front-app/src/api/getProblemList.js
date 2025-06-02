import axios from "axios";

// - title: String (제목 키워드 포함)
// - category: String (JAVA, PYTHON, …)
// - level: Integer (난이도)
// - status: String (solved / unsolved)
// - page: Integer (페이지 번호)
// - size: Integer (페이지 크기)
// - sort: String (id,desc 등)

export const getProblemList = async ({
    title,
    category,
    level,
    status,
    page,
    size = 10,
    sort = "id",
}) => {
    // 각 상태를 보기 좋게 출력
    console.log("=== 문제 리스트 요청 상태 ===");
    console.log("title:", title);
    console.log("category:", category);
    console.log("level:", level);
    console.log("status:", status);
    console.log("page:", page);
    console.log("size:", size);
    console.log("sort:", sort);
    console.log("===========================");
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/problem`,
            {
                params: {
                    title: title,
                    category: category,
                    level: level,
                    status: status,
                    page: page,
                    sort: sort,
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem(
                        "accessToken"
                    )}`,
                    "Content-Type": "application/json",
                    // Access-Control-Allow-Origin: "*" 추가
                    "Access-Control-Allow-Origin": "*",
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
