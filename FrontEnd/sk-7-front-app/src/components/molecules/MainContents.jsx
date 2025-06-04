// 메인 컨텐츠
// 코딩 테스트 문제 + 카테고리 + 검색

import { useCallback, useEffect, useState } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import CategoryBar from "../atoms/CategoryBar";
import ProblemItem from "../atoms/ProblemItem";
import { getProblemList } from "../../api/getProblemList";
import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";

export default function MainContents() {
    // 전역 상태 가져오기
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

    // 페이지 이동 네비게이터
    const navigate = useNavigate();

    // 문제 클릭 시 풀이 페이지로 이동
    const handleProblemClick = (problemId) => {
        if (!isLoggedIn || !accessToken) {
            alert("인증이 필요합니다. 로그인 페이지로 이동합니다.");
            navigate("/login");
            return;
        }
        navigate(`/solve/${problemId}`);
    };

    // 검색 타이틀
    const [title, setTitle] = useState("");

    // 페이지 넘버
    const [pageNumber, setPageNumber] = useState(1);
    // 문제들 (전역 관리할지 변수에 저장할지 아직 결정 못함)
    const [problems, setProblems] = useState([]);
    // 총 페이지 수
    const [totalPages, setTotalPages] = useState(0);
    // 총 문제 개수
    // const [totalElements, setTotalElements] = useState(0);

    // 로딩 상태
    const [loading, setLoading] = useState(false);
    // 에러 메세지 상태
    const [error, setError] = useState("");
    // 카테고리 초기화 상태 플래그
    const [isResetting, setIsResetting] = useState(false);

    // 문제 리스트 요청 함수
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
        [sort, status, pageNumber, level, category, title]
    );

    // 카테고리 변경될 시 리스트 다시 받아오기
    useEffect(() => {
        if (isResetting) {
            const timeout = setTimeout(() => {
                fetchProblems(1);
                // 초기화 플래그 OFF
                setIsResetting(false);
            }, 0);
            return () => clearTimeout(timeout);
        }

        if (!isResetting) {
            fetchProblems(1);
        }
    }, [sort, status, level, category, pageNumber, isResetting]);

    // 카테고리 초기화
    const onRefresh = useCallback(() => {
        console.log("======초기화 시작======");
        // 초기화 플래그 ON
        setIsResetting(true);
        setSort("recent"); // 정렬 기본값
        setStatus("unsolved"); // 상태 기본값
        setCategory([]); // 카테고리(언어) 기본값
        setLevel(0); // 레벨 기본값
        setTitle(""); // 검색어 기본값
        setPageNumber(1); // 페이지도 1로 초기화
        console.log("======초기화 끝======");
    }, [setSort, setStatus, setCategory, setLevel, setTitle, setPageNumber]);

    // 검색
    const onSearch = useCallback(() => {
        console.log("검색");
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
        // ... (페이지네이션 UI 로직은 동일)
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
                    className={`px-3 py-1 mx-1 border rounded ${
                        pageNumber === i
                            ? "bg-blue-500 text-white"
                            : "bg-white hover:bg-gray-100"
                    } ${loading ? "cursor-not-allowed" : ""}`}
                >
                    {i}
                </button>
            );
        }
        return pageNumbers;
    };

    return (
        <div className="mx-14">
            <div className="mt-5">
                <CategoryBar
                    onReset={onRefresh}
                    onSearch={onSearch}
                    title={title}
                    setTitle={setTitle}
                />
            </div>
            <div className="border-solid border-2 mt-5">
                <div className="flex px-4 w-full text-center mb-1">
                    <span className="px-2 py-1 text-xs">상태</span>
                    <span className="flex-1 ml-4 py-1 text-xs">문제</span>
                    <span className="ml-4 px-2 py-1 text-xs">난이도</span>
                    <span className="ml-4 px-2 py-1 text-xs">정답률</span>
                </div>

                <div>
                    {/* 문제 리스트 가져오기 */}
                    {loading && (
                        <div className="text-center py-8 text-gray-500">
                            로딩 중...
                        </div>
                    )}
                    {error && (
                        <div className="text-center py-8 text-red-500">
                            {error}
                        </div>
                    )}
                    {!loading && !error && problems.length === 0 && (
                        <div className="text-center py-8 text-gray-400">
                            문제가 없습니다.
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

            {!loading && !error && totalPages > 0 && (
                <div className="flex justify-center items-center mt-6 mb-4">
                    <button
                        onClick={() => handlePageChange(pageNumber - 1)}
                        disabled={pageNumber === 1 || loading}
                        className="px-3 py-1 mx-1 border rounded bg-white hover:bg-gray-100 disabled:opacity-50"
                    >
                        이전
                    </button>
                    {renderPageNumbers()}
                    <button
                        onClick={() => handlePageChange(pageNumber + 1)}
                        disabled={pageNumber === totalPages || loading}
                        className="px-3 py-1 mx-1 border rounded bg-white hover:bg-gray-100 disabled:opacity-50"
                    >
                        다음
                    </button>
                </div>
            )}
        </div>
    );
}
