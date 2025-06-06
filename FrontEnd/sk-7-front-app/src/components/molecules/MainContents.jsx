// 메인 컨텐츠
// 코딩 테스트 문제 + 카테고리 + 검색

import { useCallback, useEffect, useState } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import CategoryBar from "../atoms/CategoryBar";
import ProblemItem from "../atoms/ProblemItem";
import { getProblemList } from "../../api/getProblemList";
import { Link } from "react-router-dom";

// - title: String (제목 키워드 포함)
// - category: String (JAVA, PYTHON, …)
// - level: Integer (난이도)
// - status: String (solved / unsolved)
// - page: Integer (페이지 번호)
// - size: Integer (페이지 크기)
// - sort: String (id,desc 등)

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

    // 검색 타이틀
    const [title, setTitle] = useState("");

    // 페이지 넘버
    const [pageNumber, setPageNumber] = useState(1);
    // 문제들 (전역 관리할지 변수에 저장할지 아직 결정 못함)
    const [problems, setProblems] = useState([]);

    // 로딩 상태
    const [loading, setLoading] = useState(false);
    // 에러 메세지 상태
    const [error, setError] = useState("");
    // 카테고리 초기화 상태 플래그
    const [isResetting, setIsResetting] = useState(false);

    // 문제 리스트 요청 함수
    const fetchProblems = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const data = await getProblemList({
                title: title,
                category: category,
                level: level,
                status: status,
                page: pageNumber,
                sort: sort,
            });
            setProblems(data.problems || []);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    }, [sort, status, pageNumber, level, category, title]);

    // 카테고리 변경될 시 리스트 다시 받아오기
    useEffect(() => {
        if (isResetting) {
            const timeout = setTimeout(() => {
                fetchProblems();
                // 초기화 플래그 OFF
                setIsResetting(false);
            }, 0);
            return () => clearTimeout(timeout);
        }

        if (!isResetting) {
            fetchProblems();
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
        setLevel(null); // 레벨 기본값
        setTitle(""); // 검색어 기본값
        setPageNumber(1); // 페이지도 1로 초기화
        console.log("======초기화 끝======");
    }, [setSort, setStatus, setCategory, setLevel, setTitle, setPageNumber]);

    // 검색
    const onSearch = useCallback(() => {
        console.log("검색");
        setPageNumber(1);
        fetchProblems();
    }, [fetchProblems]);

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
                            <ProblemItem key={item.id} {...item} />
                        ))}

                    <Link to="/solve">문제 풀이 페이지로</Link>
                </div>
            </div>
        </div>
    );
}
