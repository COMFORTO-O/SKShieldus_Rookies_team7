import React, { useState } from 'react';

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

const ProblemSearchBar = ({ onReset, onSearch, title, setTitle }) => {
    const [selectedSort, setSelectedSort] = useState(sortOptions[0].value);
    const [selectedLanguage, setSelectedLanguage] = useState('');

    const handleSearch = () => {
        onSearch({ title, sort: selectedSort, language: selectedLanguage });
    };

    const handleReset = () => {
        setTitle('');
        setSelectedSort(sortOptions[0].value);
        setSelectedLanguage('');
        onReset();
    };

    return (
        <div className="bg-white p-4 rounded-lg shadow-md flex flex-col md:flex-row items-center justify-between space-y-4 md:space-y-0 md:space-x-4">
            {/* 제목 검색 입력 필드 */}
            <div className="flex-grow w-full md:w-auto">
                <label htmlFor="title-search" className="sr-only">제목 검색</label>
                <input
                    id="title-search"
                    type="text"
                    placeholder="제목으로 검색..."
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </div>

            {/* 정렬 옵션 드롭다운 */}
            <div className="w-full md:w-auto">
                <label htmlFor="sort-option" className="sr-only">정렬 기준</label>
                <select
                    id="sort-option"
                    value={selectedSort}
                    onChange={(e) => setSelectedSort(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-md bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    {sortOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>

            {/* 언어 옵션 드롭다운 */}
            <div className="w-full md:w-auto">
                <label htmlFor="language-option" className="sr-only">언어 선택</label>
                <select
                    id="language-option"
                    value={selectedLanguage}
                    onChange={(e) => setSelectedLanguage(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-md bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">모든 언어</option> {/* 기본값 */}
                    {languageOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>

            {/* 검색 및 초기화 버튼 */}
            <div className="flex w-full md:w-auto space-x-2">
                <button
                    onClick={handleSearch}
                    className="w-1/2 md:w-auto bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
                >
                    검색
                </button>
                <button
                    onClick={handleReset}
                    className="w-1/2 md:w-auto bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-300 focus:ring-opacity-50"
                >
                    초기화
                </button>
            </div>
        </div>
    );
};

export default ProblemSearchBar;