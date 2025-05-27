// 메인 컨텐츠
// 코딩 테스트 문제 + 카테고리 + 검색

import { useEffect } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import CategoryBar from "../atoms/CategoryBar";
import ProblemItem from "../atoms/ProblemItem";

export default function MainContents() {
    // 전역 상태 가져오기
    const { sort, selectedLanguages } = useCategoryStore();

    // 카테고리 변경될 시 리스트 다시 받아오기
    useEffect(() => {
        // 리스트 받아오기 ( API )
        console.log(`sort = ${sort}\nlangs=${selectedLanguages}`);
    }, [sort, selectedLanguages]);

    return (
        <div className="h-full">
            <div className="h-auto mt-20">
                <CategoryBar />
            </div>
            <div className="border-solid border-2 mt-5">
                <ProblemItem
                    title={"새로운 문제1"}
                    level={"어려움"}
                    languages={["Java", "Python"]}
                />
                <ProblemItem
                    title={"새로운 문제2"}
                    level={"중간"}
                    languages={["Java", "Python"]}
                />
                <ProblemItem
                    title={"새로운 문제3"}
                    level={"쉬움"}
                    languages={["Java", "Python"]}
                />
            </div>
        </div>
    );
}
