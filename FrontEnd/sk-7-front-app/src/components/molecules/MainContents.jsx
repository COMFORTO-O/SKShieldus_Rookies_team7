import { useCallback, useEffect, useState } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import CategoryBar from "../atoms/CategoryBar";
import ProblemItem from "../atoms/ProblemItem";
import { getProblemList } from "../../api/getProblemList";
import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";

export default function MainContents() {
    const {
        sort,
        status,
        level,
        category,
        setSort,
        setStatus,
        setCategory,
        setLevel,
    } = useCategoryStore();

    const { isLoggedIn, accessToken } = useAuthStore();
    const navigate = useNavigate();

    const handleProblemClick = (problemId) => {
        if (!isLoggedIn || !accessToken) {
            alert("인증이 필요합니다. 로그인 페이지로 이동합니다.");
            navigate("/login");
            return;
        }
        navigate(`/solve/${problemId}`);
    };

    const [title, setTitle] = useState("");
    const [pageNumber, setPageNumber] = useState(1);
    const [problems, setProblems] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [isResetting, setIsResetting] = useState(false);

    const fetchProblems = useCallback(
        async (currentPage) => {
            setLoading(true);
            setError("");
            try {
                const data = await getProblemList({
                    title: title,
                    category: category,
                    level: level,
                    status: status,
                    page: currentPage,
                    sort: sort,
                });
                setProblems(data?.content || []);
                setTotalPages(data?.totalPages || 0);
                if (
                    currentPage > (data?.totalPages || 0) &&
                    (data?.totalPages || 0) > 0
                ) {
                    setPageNumber(data.totalPages);
                }
            } catch (err) {
                setError(err);
            } finally {
                setLoading(false);
            }
        },
        [sort, status, level, category, title]
    );

    useEffect(() => {
        if (isResetting) {
            const timeout = setTimeout(() => {
                fetchProblems(1);
                setIsResetting(false);
            }, 0);
            return () => clearTimeout(timeout);
        }

        if (!isResetting) {
            fetchProblems(pageNumber);
        }
    }, [sort, status, level, category, pageNumber, isResetting, fetchProblems]);

    const onRefresh = useCallback(() => {
        setIsResetting(true);
        setSort("createdAt");
        setStatus("unsolved");
        setCategory([]);
        setLevel(null);
        setTitle("");
        setPageNumber(1);
    }, [setSort, setStatus, setCategory, setLevel, setTitle, setPageNumber]);

    const onSearch = useCallback(() => {
        setPageNumber(1);
        fetchProblems(1);
    }, [fetchProblems]);

    const handlePageChange = (newPage) => {
        if (
            newPage >= 1 &&
            newPage <= totalPages &&
            newPage !== pageNumber &&
            !loading
        ) {
            setPageNumber(newPage);
        }
    };

    const renderPageNumbers = () => {
        const pageNumbers = [];
        const maxPagesToShow = 5;
        let startPage, endPage;

        if (totalPages <= maxPagesToShow) {
            startPage = 1;
            endPage = totalPages;
        } else {
            if (pageNumber <= Math.ceil(maxPagesToShow / 2)) {
                startPage = 1;
                endPage = maxPagesToShow;
            } else if (
                pageNumber + Math.floor(maxPagesToShow / 2) >=
                totalPages
            ) {
                startPage = totalPages - maxPagesToShow + 1;
                endPage = totalPages;
            } else {
                startPage = pageNumber - Math.floor(maxPagesToShow / 2);
                endPage = pageNumber + Math.floor(maxPagesToShow / 2);
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            pageNumbers.push(
                <button
                    key={i}
                    onClick={() => handlePageChange(i)}
                    disabled={loading || pageNumber === i}
                    className={`px-4 py-2 mx-1 rounded-lg transition-all duration-200 text-sm font-medium ${
                        pageNumber === i
                            ? "bg-blue-600 text-white shadow-md transform scale-105" // 현재 페이지 강조
                            : "bg-white text-gray-700 border border-gray-300 hover:bg-blue-50 hover:text-blue-600" // 기본 페이지 호버 효과
                    } ${loading ? "cursor-not-allowed opacity-60" : ""}`}
                >
                    {i}
                </button>
            );
        }
        return pageNumbers;
    };

    return (
        <div className="flex flex-col h-full">
            {/* 카테고리 바 */}
            <div className="mb-6">
                <CategoryBar
                    onReset={onRefresh}
                    onSearch={onSearch}
                    title={title}
                    setTitle={setTitle}
                />
            </div>

            {/* 문제 리스트 컨테이너 */}
            <div className="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-200 flex-1">
                {/* 문제 목록 헤더 */}
                <div className="grid grid-cols-[max-content_1fr_max-content_max-content] gap-4 items-center px-6 py-4 bg-gray-50 text-gray-600 border-b border-gray-200 text-sm font-semibold uppercase tracking-wide">
                    <span className="w-16 text-center">상태</span>
                    <span className="pl-4">문제</span>
                    <span className="w-24 text-center">난이도</span>
                    <span className="w-24 text-center">정답률</span>
                </div>

                {/* 문제 리스트 내용 */}
                <div className="divide-y divide-gray-100">
                    {loading && (
                        <div className="text-center py-12 text-gray-500 text-lg flex flex-col items-center justify-center">
                            <svg className="animate-spin h-10 w-10 text-blue-500 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            <p>문제 목록을 불러오는 중...</p>
                        </div>
                    )}
                    {error && (
                        <div className="text-center py-12 text-red-500 text-lg">
                            <p>오류 발생: {error.message || "알 수 없는 오류"}</p>
                            <p className="text-sm text-gray-500 mt-2">잠시 후 다시 시도해주세요.</p>
                        </div>
                    )}
                    {!loading && !error && problems.length === 0 && (
                        <div className="text-center py-12 text-gray-400 text-lg">
                            <p>해당 조건에 맞는 문제가 없습니다.</p>
                            <p className="text-sm mt-2">검색 조건을 변경해보세요.</p>
                        </div>
                    )}
                    {!loading &&
                        !error &&
                        problems.map((item) => (
                            <ProblemItem
                                key={item.id}
                                {...item}
                                onClick={handleProblemClick}
                                p_id={item.id}
                            />
                        ))}
                </div>
            </div>

            {/* 페이지네이션 */}
            {!loading && !error && totalPages > 0 && (
                <div className="flex justify-center items-center mt-8 space-x-3">
                    <button
                        onClick={() => handlePageChange(pageNumber - 1)}
                        disabled={pageNumber === 1 || loading}
                        className="px-5 py-2 rounded-lg border border-gray-300 bg-white text-gray-700 hover:bg-gray-100 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
                    >
                        이전
                    </button>
                    {renderPageNumbers()}
                    <button
                        onClick={() => handlePageChange(pageNumber + 1)}
                        disabled={pageNumber === totalPages || loading}
                        className="px-5 py-2 rounded-lg border border-gray-300 bg-white text-gray-700 hover:bg-gray-100 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
                    >
                        다음
                    </button>
                </div>
            )}
        </div>
    );
}