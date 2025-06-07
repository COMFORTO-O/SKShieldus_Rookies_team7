import { useCallback, useEffect, useState } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import CategoryBar from "../atoms/CategoryBar";
import ProblemItem from "../atoms/ProblemItem";
import { getProblemList } from "../../api/getProblemList";
import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";

export default function MainContents() {
    // Zustand 스토어에서 필터 관련 상태와 액션을 가져옵니다.
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

    // 사용자 인증 상태 및 토큰을 가져옵니다.
    const { isLoggedIn, accessToken } = useAuthStore();
    const navigate = useNavigate();

    // 문제 클릭 시 동작을 정의합니다. (로그인 여부 확인 후 페이지 이동)
    const handleProblemClick = (problemId) => {
        if (!isLoggedIn || !accessToken) {
            alert("문제 풀이는 로그인이 필요합니다. 로그인 페이지로 이동합니다.");
            navigate("/login");
            return;
        }
        navigate(`/solve/${problemId}`);
    };

    // 문제 제목 검색을 위한 로컬 상태
    const [title, setTitle] = useState("");
    // 현재 페이지 번호
    const [pageNumber, setPageNumber] = useState(1);
    // 불러온 문제 목록 데이터
    const [problems, setProblems] = useState([]);
    // 전체 페이지 수
    const [totalPages, setTotalPages] = useState(0);
    // 로딩 상태
    const [loading, setLoading] = useState(false);
    // 에러 메시지
    const [error, setError] = useState("");
    // 필터 초기화 중임을 나타내는 상태
    const [isResetting, setIsResetting] = useState(false);

    // 문제 목록을 API로부터 비동기적으로 불러오는 함수
    const fetchProblems = useCallback(
        async (currentPage) => {
            setLoading(true);
            setError(""); // 새로운 요청 시 에러 상태 초기화
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

                // 현재 페이지가 전체 페이지 수를 초과하면 마지막 페이지로 설정
                if (
                    currentPage > (data?.totalPages || 0) &&
                    (data?.totalPages || 0) > 0
                ) {
                    setPageNumber(data.totalPages);
                }
            } catch (err) {
                console.error("문제 목록 불러오기 실패:", err);
                setError(err); // 에러 객체 자체를 저장하여 상세 정보 표시
            } finally {
                setLoading(false);
            }
        },
        [sort, status, level, category, title] // 의존성 배열에 필터 상태 포함
    );

    // 필터 상태 또는 페이지 번호 변경 시 문제 목록을 다시 불러옵니다.
    useEffect(() => {
        if (isResetting) {
            // 필터 초기화 시 약간의 딜레이 후 데이터를 불러옵니다.
            const timeout = setTimeout(() => {
                fetchProblems(1); // 초기화 시 1페이지부터 다시 시작
                setIsResetting(false);
            }, 0); // 짧은 딜레이로 상태 반영 보장
            return () => clearTimeout(timeout);
        }

        // 초기화 중이 아닐 때만 현재 페이지 기준으로 문제를 불러옵니다.
        if (!isResetting) {
            fetchProblems(pageNumber);
        }
    }, [sort, status, level, category, pageNumber, isResetting, fetchProblems]);

    // 모든 필터를 초기화하고 문제 목록을 새로고침하는 함수
    const onRefresh = useCallback(() => {
        setIsResetting(true); // 초기화 상태 시작
        setSort("createdAt,desc"); // 최신순으로 초기화
        setStatus("unsolved"); // 문제 상태를 '미해결'로 초기화 (API에 따라 조정)
        setCategory(""); // 카테고리를 빈 문자열로 초기화 (단일 선택)
        setLevel(""); // 레벨을 빈 문자열로 초기화 ("전체 레벨"에 해당)
        setTitle(""); // 검색 제목 초기화
        setPageNumber(1); // 페이지 번호 1로 초기화
    }, [setSort, setStatus, setCategory, setLevel, setTitle, setPageNumber]);

    // 검색 버튼 클릭 시 1페이지부터 다시 검색을 시작하는 함수
    const onSearch = useCallback(() => {
        setPageNumber(1); // 검색 시 1페이지부터 시작
        fetchProblems(1); // 새로운 검색어로 1페이지 문제 목록 불러오기
    }, [fetchProblems]);

    // 페이지 번호 변경 핸들러
    const handlePageChange = (newPage) => {
        if (
            newPage >= 1 && // 1페이지보다 작아질 수 없음
            newPage <= totalPages && // 전체 페이지보다 커질 수 없음
            newPage !== pageNumber && // 현재 페이지와 다를 경우에만 변경
            !loading // 로딩 중이 아닐 때만 변경 가능
        ) {
            setPageNumber(newPage);
        }
    };

    // 페이지네이션 번호들을 렌더링하는 함수
    const renderPageNumbers = () => {
        const pageNumbers = [];
        const maxPagesToShow = 5; // 한 번에 표시할 최대 페이지 번호 수
        let startPage, endPage;

        if (totalPages <= maxPagesToShow) {
            // 전체 페이지가 표시할 수 있는 최대 페이지 수보다 적으면 모두 표시
            startPage = 1;
            endPage = totalPages;
        } else {
            // 현재 페이지를 중심으로 페이지 번호 범위 계산
            if (pageNumber <= Math.ceil(maxPagesToShow / 2)) {
                // 앞쪽에 있을 경우 1부터 시작
                startPage = 1;
                endPage = maxPagesToShow;
            } else if (
                pageNumber + Math.floor(maxPagesToShow / 2) >=
                totalPages
            ) {
                // 뒤쪽에 있을 경우 전체 페이지에서 역산
                startPage = totalPages - maxPagesToShow + 1;
                endPage = totalPages;
            } else {
                // 중간에 있을 경우 현재 페이지를 중심으로
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
                            ? "bg-blue-600 text-white shadow-md transform scale-105" // 현재 페이지 강조 스타일
                            : "bg-white text-gray-700 border border-gray-300 hover:bg-blue-50 hover:text-blue-600" // 기본 페이지 스타일
                    } ${loading ? "cursor-not-allowed opacity-60" : ""}`}
                >
                    {i}
                </button>
            );
        }
        return pageNumbers;
    };

    return (
        <div className="flex flex-col h-full p-4 sm:p-6 bg-gray-50 min-h-screen">
            {/* 카테고리/검색 바 섹션 */}
            <div className="mb-8">
                <CategoryBar
                    onReset={onRefresh}
                    onSearch={onSearch}
                    title={title}
                    setTitle={setTitle}
                />
            </div>

            {/* --- 문제 리스트 컨테이너 --- */}
            <div className="bg-white shadow-xl rounded-2xl overflow-hidden border border-gray-100 flex-1 flex flex-col">
                {/* 문제 목록 헤더 */}
                <div className="grid grid-cols-[80px_100px_1fr_90px_90px] gap-4 items-center px-6 py-4 bg-gradient-to-r from-blue-50 to-indigo-50 text-blue-800 border-b border-blue-200 text-sm font-bold uppercase tracking-wide">
                    <span className="text-center">상태</span>
                    <span className="text-center">문제 ID</span> {/* 문제 ID 추가 */}
                    <span className="pl-4">문제 제목</span>
                    <span className="text-center">난이도</span>
                    <span className="text-center">정답률</span>
                </div>

                {/* 문제 리스트 내용 */}
                <div className="divide-y divide-gray-100 flex-1">
                    {loading && (
                        <div className="flex flex-col items-center justify-center py-12 text-gray-500 text-lg bg-white h-full">
                            <svg className="animate-spin h-12 w-12 text-blue-500 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            <p className="font-semibold">문제 목록을 불러오는 중입니다...</p>
                            <p className="text-sm text-gray-600 mt-2">잠시만 기다려 주세요.</p>
                        </div>
                    )}
                    {error && (
                        <div className="flex flex-col items-center justify-center py-12 text-red-500 text-lg bg-red-50 rounded-b-xl h-full">
                            <p className="text-xl font-bold mb-2">🚨 오류 발생!</p>
                            <p className="text-md text-red-600">{error.message || "알 수 없는 오류가 발생했습니다."}</p>
                            <p className="text-sm text-gray-600 mt-3">문제 정보를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.</p>
                            <button
                                onClick={() => fetchProblems(pageNumber)}
                                className="btn btn-sm btn-outline btn-error mt-5 transition duration-300 hover:scale-105"
                            >
                                다시 시도하기
                            </button>
                        </div>
                    )}
                    {!loading && !error && problems.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-12 text-gray-500 text-lg bg-gray-50 rounded-b-xl h-full">
                            <p className="text-xl font-semibold mb-2">😭 해당 조건에 맞는 문제가 없습니다.</p>
                            <p className="text-md text-gray-600">검색 조건을 변경하거나 모든 필터를 초기화해보세요.</p>
                            <button
                                onClick={onRefresh}
                                className="btn btn-sm btn-outline btn-info mt-5 transition duration-300 hover:scale-105"
                                title="모든 필터 초기화"
                            >
                                필터 초기화
                            </button>
                        </div>
                    )}
                    {!loading && !error && problems.length > 0 &&
                        problems.map((item) => (
                            <ProblemItem
                                key={item.id}
                                id={item.id} // 문제 ID 추가
                                title={item.title}
                                category={item.category?.description || 'N/A'} // 카테고리 설명 (없으면 'N/A')
                                level={item.level}
                                passRate={item.passRate}
                                solved={item.solved ?? false} // solved가 null이면 false로 처리
                                onClick={() => handleProblemClick(item.id)}
                            />
                        ))}
                </div>
            </div>

            {/* 페이지네이션 섹션 */}
            {!loading && !error && totalPages > 0 && (
                <div className="flex justify-center items-center mt-8 space-x-4">
                    <button
                        onClick={() => handlePageChange(pageNumber - 1)}
                        disabled={pageNumber === 1 || loading}
                        className="btn btn-outline btn-primary px-5 py-2 rounded-lg transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md"
                    >
                        이전 페이지
                    </button>
                    <div className="flex space-x-2">
                        {renderPageNumbers()}
                    </div>
                    <button
                        onClick={() => handlePageChange(pageNumber + 1)}
                        disabled={pageNumber === totalPages || loading}
                        className="btn btn-outline btn-primary px-5 py-2 rounded-lg transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md"
                    >
                        다음 페이지
                    </button>
                </div>
            )}
        </div>
    );
}