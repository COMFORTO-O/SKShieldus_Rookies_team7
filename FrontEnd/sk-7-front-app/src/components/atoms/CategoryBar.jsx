import { useEffect, useState, useRef } from "react";
import useCategoryStore from "../../store/useCategoryStore";

// // 정렬, 카테고리, 푼 문제 제외 체크박스 등

const sortOptions = [
    { label: "최신순", value: "recent" },
    { label: "레벨순", value: "level" },
    { label: "정답률순", value: "accuracy" },
];

const languageOptions = [
    { label: "Java", value: "Java" },
    { label: "Python", value: "Python" },
    { label: "C++", value: "C++" },
    { label: "JavaScript", value: "JavaScript" },
];

const CategoryBar = ({ onReset, onSearch, title, setTitle }) => {
    // 상태
    const {
        sort,
        setSort,
        category,
        level,
        status,
        setStatus,
        setCategory,
        setLevel,
    } = useCategoryStore();
    // 언어 셀렉터 박스 ON/OFF
    const [langOpen, setLangOpen] = useState(false);

    // 언어 셀렉터 박스 ref
    const langRef = useRef(null);
    // 바깥 클릭 시 닫기
    useEffect(() => {
        const handleClick = (e) => {
            if (langRef.current && !langRef.current.contains(e.target)) {
                setLangOpen(false);
            }
        };
        if (langOpen) {
            document.addEventListener("mousedown", handleClick);
        }
        return () => document.removeEventListener("mousedown", handleClick);
    }, [langOpen]);

    // 셀렉트 박스 Toggle 핸들러
    const handleLangToggle = () => setLangOpen((v) => !v);

    const handleLangCheck = (val) => {
        if (category.includes(val)) {
            setCategory(category.filter((c) => c !== val));
        } else {
            setCategory([...category, val]);
        }
    };

    return (
        <div className="w-full">
            <div className="flex flex-row gap-4 items-center">
                {/* 정렬 박스 */}
                <select
                    value={sort}
                    onChange={(e) => {
                        setSort(e.target.value);
                    }}
                    className="border rounded px-3 py-2 transition"
                >
                    {sortOptions.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>

                {/* 레벨 박스 */}
                <select
                    value={level}
                    onChange={(e) => setLevel(Number(e.target.value))}
                    className="border rounded px-3 py-2 transition"
                >
                    <option value="">전체 레벨</option>
                    {[1, 2, 3, 4, 5].map((lv) => (
                        <option key={lv} value={lv}>
                            {lv} 레벨
                        </option>
                    ))}
                </select>

                {/* 언어 복수 선택 셀렉트 박스 */}
                <div className="relative" ref={langRef}>
                    <button
                        type="button"
                        className="border rounded px-3 py-2 transition bg-white min-w-[120px] text-left"
                        onClick={handleLangToggle}
                    >
                        {category.length === 0
                            ? "언어 선택"
                            : category.join(", ")}
                        {/* https://www.w3schools.com/charsets/tryit.asp?deci=128899 */}
                        <span className="float-right ml-2">&#128899;</span>
                    </button>
                    {langOpen && (
                        <div
                            className="absolute z-10 mt-1 bg-white border rounded shadow w-40 max-h-48 overflow-auto transition"
                            onClick={(e) => e.stopPropagation()}
                        >
                            {languageOptions.map((opt) => (
                                <label
                                    key={opt.value}
                                    className="flex items-center px-3 py-2 hover:bg-gray-100 cursor-pointer"
                                >
                                    <input
                                        type="checkbox"
                                        checked={category.includes(opt.value)}
                                        onChange={() =>
                                            handleLangCheck(opt.value)
                                        }
                                        className="mr-2"
                                    />
                                    {opt.label}
                                </label>
                            ))}
                        </div>
                    )}
                </div>

                {/* 푼 문제/안 푼 문제 셀렉트 박스 */}
                <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    className="border rounded px-3 py-2 transition ml-2"
                >
                    <option value="unsolved">안 푼 문제</option>
                    <option value="solved">푼 문제</option>
                </select>

                {/* 새로고침 버튼 */}
                <button
                    onClick={onReset}
                    className="ml-2 px-3 py-1 rounded border bg-gray-100 hover:bg-gray-200 flex items-center"
                    title="초기화"
                >
                    <i className="bx bx-refresh-ccw bx-tada hover:scale-105" />
                </button>
            </div>

            {/* 검색 인풋 */}
            <div>
                <form
                    className="flex items-center gap-2 mt-2 w-full"
                    onSubmit={(e) => {
                        e.preventDefault();
                        onSearch();
                    }}
                >
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => {
                            setTitle(e.target.value);
                            // console.log(`title: ${title}`);
                        }}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                e.preventDefault();
                                onSearch();
                            }
                        }}
                        placeholder="문제 검색"
                        className="border rounded px-2 py-1 flex-1"
                    />
                    <button
                        type="submit"
                        className="ml-1 px-2 py-1 rounded bg-secondary hover:bg-primary text-black hover:text-white"
                    >
                        검색
                    </button>
                </form>
            </div>
        </div>
    );
};

export default CategoryBar;
