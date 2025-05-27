import { useCallback, useState } from "react";
import useCategoryStore from "../../store/useCategoryStore";

// 정렬, 카테고리, 푼 문제 제외 체크박스 등
const languages = ["Java", "C", "Python", "Javascript"];

const sortOptions = [
    { label: "최신순", value: "recent" },
    { label: "레벨순", value: "level" },
    { label: "정답률순", value: "accuragy" },
];

const orderOptions = [
    { label: "오름차순", value: "asc" },
    { label: "내림차순", value: "desc" },
];

const CategoryBar = ({ onRefresh, onSearch }) => {
    // 상태
    const {
        sort,
        selectedLanguages,
        setSort,
        setSelectedLanguages,
        order = "desc",
        setOrder,
    } = useCategoryStore();

    // 검색어
    const [search, setSearch] = useState("");

    // 인자로 넘길 콜백 함수 최적화
    // 정렬 방식
    const handleSortChange = useCallback(
        (e) => {
            setSort(e.target.value);
        },
        [setSort]
    );

    // 오름/내림차순
    const handleOrderChange = useCallback(
        (e) => {
            setOrder && setOrder(e.target.value);
        },
        [setOrder]
    );

    // 언어 선택
    const handleLangToggle = useCallback(
        (lang) => {
            let next;
            if (selectedLanguages.includes(lang)) {
                next = selectedLanguages.filter((l) => l !== lang);
            } else {
                next = [...selectedLanguages, lang];
            }
            setSelectedLanguages(next);
        },
        [selectedLanguages, setSelectedLanguages]
    );

    // 새로고침 버튼 클릭
    const handleRefresh = () => {
        // onRefresh 함수는 MainContents 에서 받아옴
        if (onRefresh) onRefresh();
    };

    // 검색 인풋 엔터 또는 버튼 클릭
    const handleSearch = () => {
        // onSearch 도 동일
        if (onSearch) onSearch(search);
    };

    return (
        <div className="w-full">
            <div className="flex flex-row gap-4 items-center">
                {/* 정렬 박스 */}
                <select
                    value={sort}
                    onChange={handleSortChange}
                    className="border rounded px-3 py-2 transition"
                >
                    {sortOptions.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>

                {/* 오름/내림차순 박스 */}
                <select
                    value={order}
                    onChange={handleOrderChange}
                    className="border rounded px-3 py-2"
                >
                    {orderOptions.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>

                {/* 언어 선택 박스 */}
                <div className="flex gap-2 flex-wrap">
                    {languages.map((lang) => (
                        <button
                            key={lang}
                            type="button"
                            onClick={() => handleLangToggle(lang)}
                            className={`px-3 py-1 rounded border ${
                                selectedLanguages.includes(lang)
                                    ? "bg-primary text-white border-primary"
                                    : "bg-white text-black border-gray-300"
                            } transition`}
                        >
                            {lang}
                        </button>
                    ))}
                </div>

                {/* 새로고침 버튼 */}
                <button
                    onClick={handleRefresh}
                    className="ml-2 px-3 py-1 rounded border bg-gray-100 hover:bg-gray-200"
                    title="새로고침"
                >
                    🔄
                </button>
            </div>

            {/* 검색 인풋 */}
            <div className="flex items-center gap-2 mt-2 w-full">
                <input
                    type="text"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                    placeholder="문제 검색"
                    className="border rounded px-2 py-1 flex-1"
                />
                <button
                    onClick={handleSearch}
                    className="ml-1 px-2 py-1 rounded bg-secondary hover:bg-primary text-black hover:text-white"
                >
                    검색
                </button>
            </div>
        </div>
    );
};

export default CategoryBar;
