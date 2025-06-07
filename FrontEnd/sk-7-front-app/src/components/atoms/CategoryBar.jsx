import { useEffect, useState, useRef } from "react";
import useCategoryStore from "../../store/useCategoryStore";

const sortOptions = [
    { label: "최신순", value: "createdAt" },
    { label: "ID 순", value: "id" },
    { label: "레벨순", value: "level" },
];

const languageOptions = [
    { label: "JAVA", value: "Java" },
    { label: "PYTHON", value: "Python" },
    { label: "C", value: "C" },
    { label: "HTML", value: "HTML" },
    { label: "ALGORITHM", value: "Algorithm" },
    { label: "string", value: "string" }, // 'string'은 일반적으로 언어 카테고리로 사용되지 않으므로 필요에 따라 제거하거나 다른 의미로 변경하세요.
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
        <div className="w-full bg-white p-6 rounded-lg shadow-md border border-gray-100">
            <div className="flex flex-wrap items-center gap-4 mb-4">
                {/* 정렬 박스 */}
                <select
                    value={sort}
                    onChange={(e) => {
                        setSort(e.target.value);
                    }}
                    className="flex-grow min-w-[120px] md:flex-grow-0 border border-gray-300 rounded-md px-4 py-2 bg-gray-50 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out appearance-none bg-no-repeat bg-right-center pr-8"
                    style={{ backgroundImage: `url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'%3E%3Cpath fill-rule='evenodd' d='M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z' clip-rule='evenodd' /%3E%3C/svg%3E")`, backgroundSize: '1.25rem' }}
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
                    className="flex-grow min-w-[120px] md:flex-grow-0 border border-gray-300 rounded-md px-4 py-2 bg-gray-50 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out appearance-none bg-no-repeat bg-right-center pr-8"
                    style={{ backgroundImage: `url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'%3E%3Cpath fill-rule='evenodd' d='M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z' clip-rule='evenodd' /%3E%3C/svg%3E")`, backgroundSize: '1.25rem' }}
                >
                    <option value="">전체 레벨</option>
                    {[1, 2, 3, 4, 5].map((lv) => (
                        <option key={lv} value={lv}>
                            {lv} 레벨
                        </option>
                    ))}
                </select>

                {/* 언어 복수 선택 셀렉트 박스 */}
                <div className="relative flex-grow min-w-[150px] md:flex-grow-0" ref={langRef}>
                    <button
                        type="button"
                        className="w-full border border-gray-300 rounded-md px-4 py-2 bg-gray-50 text-gray-700 text-left flex items-center justify-between focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out"
                        onClick={handleLangToggle}
                    >
                        <span>
                            {category.length === 0
                                ? "언어 선택"
                                : category.join(", ")}
                        </span>
                        <svg className={`w-4 h-4 text-gray-500 transition-transform duration-200 ${langOpen ? 'rotate-180' : ''}`} fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd"></path></svg>
                    </button>
                    {langOpen && (
                        <div
                            className="absolute z-20 mt-1 bg-white border border-gray-200 rounded-md shadow-lg w-full max-h-60 overflow-y-auto transform origin-top animate-fade-in"
                            onClick={(e) => e.stopPropagation()}
                        >
                            {languageOptions.map((opt) => (
                                <label
                                    key={opt.value}
                                    className="flex items-center px-4 py-2 hover:bg-blue-50 cursor-pointer text-gray-800"
                                >
                                    <input
                                        type="checkbox"
                                        checked={category.includes(opt.value)}
                                        onChange={() =>
                                            handleLangCheck(opt.value)
                                        }
                                        className="form-checkbox h-4 w-4 text-blue-600 rounded focus:ring-blue-500 transition duration-150 ease-in-out"
                                    />
                                    <span className="ml-2 text-sm">{opt.label}</span>
                                </label>
                            ))}
                        </div>
                    )}
                </div>

                {/* 푼 문제/안 푼 문제 셀렉트 박스 */}
                <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    className="flex-grow min-w-[120px] md:flex-grow-0 border border-gray-300 rounded-md px-4 py-2 bg-gray-50 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out appearance-none bg-no-repeat bg-right-center pr-8"
                    style={{ backgroundImage: `url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'%3E%3Cpath fill-rule='evenodd' d='M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z' clip-rule='evenodd' /%3E%3C/svg%3E")`, backgroundSize: '1.25rem' }}
                >
                    <option value="unsolved">안 푼 문제</option>
                    <option value="solved">푼 문제</option>
                </select>

                {/* 새로고침 버튼 */}
                <button
                    onClick={onReset}
                    className="p-2 rounded-md bg-gray-100 hover:bg-gray-200 text-gray-600 hover:text-gray-800 transition duration-200 ease-in-out flex items-center justify-center border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 flex-shrink-0"
                    title="초기화"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004 12c0 2.21.894 4.204 2.342 5.658m0 0l2.88 2.88M19 20v-5h-.582m0 0a8.001 8.001 0 00-15.356-2A8.001 8.001 0 0020 12c0-2.21-.894-4.204-2.342-5.658"></path></svg>
                </button>
            </div>

            {/* 검색 인풋 및 버튼 */}
            <form
                className="flex flex-wrap items-center gap-3 w-full"
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
                    }}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") {
                            e.preventDefault();
                            onSearch();
                        }
                    }}
                    placeholder="문제 제목으로 검색..."
                    className="flex-1 min-w-[200px] border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out text-gray-800 placeholder-gray-400"
                />
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition duration-200 ease-in-out font-semibold shadow-md"
                >
                    검색
                </button>
            </form>
        </div>
    );
};

export default CategoryBar;