// 메인 컨텐츠
// 코딩 테스트 문제 + 카테고리 + 검색

import { useCallback, useEffect, useState } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import CategoryBar from "../atoms/CategoryBar";
import ProblemItem from "../atoms/ProblemItem";
import { getProblemList } from "../../api/getProblemList";

export default function MainContents() {
    // 전역 상태 가져오기
    const { sort, status, searchKeyword, level } = useCategoryStore();
    const [pageNumber, setPageNumber] = useState(1);
    const [problems, setProblems] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // 문제 리스트 요청 함수
    const fetchProblems = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const data = await getProblemList({
                title: searchKeyword,
                category: sort,
                level: level,
                status: status,
                page: pageNumber,
            });
            setProblems(data.problems || []);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    }, [searchKeyword, sort, status, pageNumber, level]);

    // 카테고리 변경될 시 리스트 다시 받아오기
    useEffect(() => {
        fetchProblems();
    }, [sort, status, level]);

    // 새로고침
    const onRefresh = useCallback(() => {
        console.log("새로고침");
        fetchProblems();
    }, [fetchProblems]);

    // 검색
    const onSearch = useCallback(() => {
        console.log("검색");
        // setPageNumber(1);
        fetchProblems();
    }, [fetchProblems]);

    return (
        <div className="mx-14">
            <div className="mt-5">
                <CategoryBar onReset={onRefresh} onSearch={onSearch} />
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
                </div>
            </div>
        </div>
    );
}
