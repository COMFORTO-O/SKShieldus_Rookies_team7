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
    sort,
}) => {

    // URLSearchParams 객체 생성
    const params = new URLSearchParams();

    if (title !== undefined && title !== null && title !== "")
        params.append("title", title);
    if (level !== undefined && level !== null && level !== 0)
        params.append("level", level);
    if (status !== undefined && status !== null && status !== "")
        params.append("status", status);
    if (page !== undefined && page !== null) params.append("page", page - 1);
    if (size !== undefined && size !== null) params.append("size", size); // size 파라미터 추가
    if (sort !== undefined && sort !== null && sort !== "")
        params.append("sort", sort);

    // category 처리 (,로 연결해 String으로 구성)
    if (category) {
        if (Array.isArray(category)) {
            // 빈 배열이 아니면 콤마로 join해서 하나의 문자열로 전달 (공백 없이)
            if (category.length > 0) {
                params.append(
                    "category",
                    category
                        .map((c) => c.toUpperCase().replace(/\s+/g, ""))
                        .join(",")
                );
            }
        } else if (typeof category === "string" && category !== "") {
            params.append(
                "category",
                category.toUpperCase().replace(/\s+/g, "")
            );
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

        // content만 반환
        return response.data?.data;
    } catch (error) {
        // 에러 출력
        throw (
            error.response?.data?.message ||
            error.message ||
            "목록 불러오기 실패"
        );
    }
};
