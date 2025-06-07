import { useEffect, useState, useRef, useCallback } from "react";
import useCategoryStore from "../../store/useCategoryStore";
import axios from "axios";

// --- 상수 정의 ---
// 정렬 옵션
const sortOptions = [
    { label: "최신순", value: "createdAt,desc" },
    { label: "오래된 순", value: "createdAt,asc" },
    { label: "문제 ID 순", value: "id,asc" },
    { label: "레벨 높은 순", value: "level,desc" },
    { label: "레벨 낮은 순", value: "level,asc" },
    { label: "정답률 높은 순", value: "passRate,desc" },
    { label: "정답률 낮은 순", value: "passRate,asc" },
];

// 난이도 옵션
const levelOptions = [
    { label: "전체 레벨", value: "" },
    { label: "1 레벨", value: 1 },
    { label: "2 레벨", value: 2 },
    { label: "3 레벨", value: 3 },
    { label: "4 레벨", value: 4 },
    { label: "5 레벨", value: 5 },
];

const CategoryBar = ({ onReset, onSearch, title, setTitle }) => {
    // Zustand 스토어에서 상태와 액션 가져오기
    const {
        sort, setSort,
        category, setCategory, // category가 단일 값으로 변경됨
        level, setLevel,
    } = useCategoryStore();

    // 드롭다운 열림/닫힘 상태 및 ref 관리
    const [sortOpen, setSortOpen] = useState(false);
    const sortRef = useRef(null);

    const [categoryOpen, setCategoryOpen] = useState(false);
    const categoryRef = useRef(null);

    const [levelOpen, setLevelOpen] = useState(false);
    const levelRef = useRef(null);

    // API를 통해 로드될 카테고리 옵션 상태
    const [apiCategoryOptions, setApiCategoryOptions] = useState([]);
    const [isLoadingCategories, setIsLoadingCategories] = useState(true);
    const [categoryFetchError, setCategoryFetchError] = useState(null);

    // 모든 드롭다운의 외부 클릭을 처리하는 공통 함수
    const handleOutsideClick = useCallback((event) => {
        if (sortRef.current && !sortRef.current.contains(event.target)) setSortOpen(false);
        if (categoryRef.current && !categoryRef.current.contains(event.target)) setCategoryOpen(false);
        if (levelRef.current && !levelRef.current.contains(event.target)) setLevelOpen(false);
    }, []);

    // 모든 드롭다운에 대해 외부 클릭 이벤트 리스너 등록/해제
    useEffect(() => {
        document.addEventListener("mousedown", handleOutsideClick);
        return () => {
            document.removeEventListener("mousedown", handleOutsideClick);
        };
    }, [handleOutsideClick]);

    // 카테고리 목록 API 호출 함수
    const fetchCategories = useCallback(async () => {
        setIsLoadingCategories(true);
        setCategoryFetchError(null);
        try {
            const response = await axios.get("http://localhost:8080/api/problem/category");
            const fetchedOptions = response.data.data.map(cat => ({
                label: cat.description,
                value: cat.code
            }));
            setApiCategoryOptions(fetchedOptions);
        } catch (error) {
            console.error("카테고리 목록 불러오기 실패:", error);
            setCategoryFetchError("카테고리 정보를 불러오는 데 실패했습니다.");
            setApiCategoryOptions([]);
        } finally {
            setIsLoadingCategories(false);
        }
    }, []);

    // 컴포넌트 마운트 시 카테고리 목록 로드
    useEffect(() => {
        fetchCategories();
    }, [fetchCategories]);

    // 드롭다운 토글 함수들
    const handleToggle = (setter, isOpen) => () => setter(!isOpen);
    const handleSortSelect = (value) => { setSort(value); setSortOpen(false); };
    const handleLevelSelect = (value) => { setLevel(value); setLevelOpen(false); };

    // 변경된 부분: 카테고리 단일 선택 핸들러
    const handleCategorySelect = (val) => {
        setCategory(val); // 선택된 단일 카테고리 값으로 설정
        setCategoryOpen(false); // 선택 후 드롭다운 닫기
    };

    // 선택된 값 표시 텍스트
    const selectedSortText = sortOptions.find(opt => opt.value === sort)?.label || "정렬 기준";
    const selectedLevelText = levelOptions.find(opt => opt.value === level)?.label || "전체 레벨";

    // 변경된 부분: selectedCategoryText (단일 선택에 맞게 변경)
    const selectedCategoryText = category === ""
        ? "카테고리 선택"
        : apiCategoryOptions.find(opt => opt.value === category)?.label || category;


    // 커스텀 드롭다운 렌더링을 위한 헬퍼 컴포넌트
    const CustomDropdown = ({ label, currentText, isOpen, toggle, options, onSelect, type, isLoading, error, fetchRetry, selectedValue // 이 부분을 추가
                            }) => (
        <div className="relative w-full sm:flex-1 md:w-auto" ref={
            label === "정렬 기준" ? sortRef :
                label === "난이도" ? levelRef :
                    label === "카테고리" ? categoryRef :
                        null
        }>
            <label className="form-control w-full">
                <div className="label">
                    <span className="label-text text-sm font-medium text-gray-700">{label}</span>
                </div>
                <button
                    type="button"
                    className="btn btn-ghost select-bordered w-full text-gray-800 pr-4 flex items-center"
                    onClick={toggle}
                    disabled={isLoading}
                >
                    <div className="flex-1 truncate text-left">
                        {isLoading ? `${label} 불러오는 중...` :
                            error ? `${label} 로드 실패` :
                                currentText}
                    </div>
                    {!isLoading && !error && (
                        <svg className={`w-4 h-4 text-gray-500 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''} ml-2`} fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd"></path></svg>
                    )}
                </button>
            </label>
            {isOpen && (
                <div
                    className="absolute z-20 top-full mt-1 bg-base-100 border border-gray-200 rounded-box shadow-lg w-full max-h-60 overflow-y-auto transform origin-top animate-fade-in"
                    onClick={(e) => e.stopPropagation()}
                >
                    {isLoading ? (
                        <p className="p-4 text-center text-gray-500">불러오는 중...</p>
                    ) : error ? (
                        <div className="p-4 text-center text-error-content bg-error-content/10 rounded-box">
                            <p>{error}</p>
                            <button
                                onClick={fetchRetry}
                                className="btn btn-xs btn-outline btn-error mt-2"
                            >
                                다시 시도
                            </button>
                        </div>
                    ) : options.length > 0 ? (
                        <ul className="menu p-2">
                            {options.map((opt) => (
                                <li key={opt.value}>
                                    <label className="label cursor-pointer p-0 w-full hover:bg-base-200 rounded-box">
                                        {/* 변경된 부분: type이 'radio'면 radio 버튼, 아니면 checkbox */}
                                        <input
                                            type={type === "checkbox" ? "checkbox" : "radio"}
                                            // 변경된 부분: selectedValue prop과 비교
                                            checked={selectedValue === opt.value}
                                            onChange={() => onSelect(opt.value)}
                                            className={`${type === "checkbox" ? "checkbox" : "radio"} checkbox-primary mr-2`}
                                            name={`${label}-option`} // 같은 그룹의 라디오 버튼에 고유한 name 설정
                                        />
                                        <span className="label-text flex-1">{opt.label}</span>
                                    </label>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="p-4 text-center text-gray-500">사용 가능한 옵션이 없습니다.</p>
                    )}
                </div>
            )}
        </div>
    );


    return (
        <div className="w-full bg-white p-6 rounded-2xl shadow-xl border border-gray-50 mb-8 transform transition-all duration-300 hover:shadow-2xl hover:border-blue-200">
            <div className="flex flex-wrap items-center gap-4 mb-6">
                {/* 정렬 드롭다운 */}
                <CustomDropdown
                    label="정렬 기준"
                    currentText={selectedSortText}
                    isOpen={sortOpen}
                    toggle={handleToggle(setSortOpen, sortOpen)}
                    options={sortOptions}
                    onSelect={handleSortSelect}
                    type="radio"
                    selectedValue={sort} // 추가: 현재 선택된 값 전달
                />

                {/* 난이도 드롭다운 */}
                <CustomDropdown
                    label="난이도"
                    currentText={selectedLevelText}
                    isOpen={levelOpen}
                    toggle={handleToggle(setLevelOpen, levelOpen)}
                    options={levelOptions}
                    onSelect={handleLevelSelect}
                    type="radio"
                    selectedValue={level} // 추가: 현재 선택된 값 전달
                />

                {/* 카테고리 드롭다운 (변경됨) */}
                <CustomDropdown
                    label="카테고리"
                    currentText={selectedCategoryText}
                    isOpen={categoryOpen}
                    toggle={handleToggle(setCategoryOpen, categoryOpen)}
                    options={apiCategoryOptions}
                    onSelect={handleCategorySelect} // 변경: 단일 선택 핸들러
                    type="radio" // 변경: 라디오 버튼으로 설정
                    isLoading={isLoadingCategories}
                    error={categoryFetchError}
                    fetchRetry={fetchCategories}
                    selectedValue={category} // 추가: 현재 선택된 값 전달
                />

                {/* 문제 상태 드롭다운 (제거됨) */}
            </div>

            {/* 검색 입력 필드 및 버튼 */}
            <form
                className="flex flex-col sm:flex-row items-end gap-3 w-full mt-6 pt-4 border-t border-gray-200"
                onSubmit={(e) => {
                    e.preventDefault();
                    onSearch();
                }}
            >
                <label className="form-control w-full sm:flex-1">
                    <div className="label">
                        <span className="label-text text-sm font-medium text-gray-700">문제 제목 검색</span>
                    </div>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                e.preventDefault();
                                onSearch();
                            }
                        }}
                        placeholder="제목으로 문제 검색..."
                        className="input input-bordered w-full bg-base-100 text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </label>
                <div className="flex gap-3 w-full sm:w-auto mt-4 sm:mt-0">
                    <button
                        type="submit"
                        className="btn btn-primary px-8 flex-1 sm:flex-none transition duration-300 transform hover:scale-105 shadow-md flex items-center justify-center gap-2"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
                        검색
                    </button>
                    <button
                        type="button"
                        onClick={onReset}
                        className="btn btn-ghost px-8 flex-1 sm:flex-none transition duration-300 transform hover:scale-105 flex items-center justify-center gap-2 text-gray-600 hover:text-gray-800"
                        title="모든 필터 초기화"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004 12c0 2.21.894 4.204 2.342 5.658m0 0l2.88 2.88M19 20v-5h-.582m0 0a8.001 8.001 0 00-15.356-2A8.001 8.001 0 0020 12c0-2.21-.894-4.204-2.342-5.658"></path></svg>
                        초기화
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CategoryBar;