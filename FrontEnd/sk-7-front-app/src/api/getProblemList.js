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

    // URLSearchParams 객체 생성
    const params = new URLSearchParams();

    if (title !== undefined && title !== null && title !== "")
        params.append("title", title);
    if (level !== undefined && level !== null) params.append("level", level);
    if (status !== undefined && status !== null && status !== "")
        params.append("status", status);
    if (page !== undefined && page !== null) params.append("page", page);
    if (size !== undefined && size !== null) params.append("size", size); // size 파라미터 추가
    if (sort !== undefined && sort !== null && sort !== "")
        params.append("sort", sort);

    // category 처리 (,로 연결해 String으로 구성)
    if (category) {
        if (Array.isArray(category)) {
            // 빈 배열이 아니면 콤마로 join해서 하나의 문자열로 전달
            if (category.length > 0) {
                params.append("category", category.join(","));
            }
        } else if (typeof category === "string" && category !== "") {
            params.append("category", category);
        }
    }

    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/problem`,
            {
                params: params,
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
