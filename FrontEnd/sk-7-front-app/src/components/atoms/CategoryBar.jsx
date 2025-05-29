import { useEffect } from "react";
import useCategoryStore from "../../store/useCategoryStore";

// // 정렬, 카테고리, 푼 문제 제외 체크박스 등

const sortOptions = [
    { label: "최신순", value: "recent" },
    { label: "레벨순", value: "level" },
    { label: "정답률순", value: "accuragy" },
];

const CategoryBar = ({ onReset, onSearch }) => {
    // 상태
    const {
        sort,
        setSort,
        searchKeyword,
        level,
        status,
        setStatus,
        setSearchKeyword,
        setLevel,
    } = useCategoryStore();

    useEffect(() => {
        console.log({
            sort,
            level,
            status,
        });
    }, [sort, level, status]);

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
                    onSubmit={onSearch}
                >
                    <input
                        type="text"
                        value={searchKeyword}
                        onChange={(e) => setSearchKeyword(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && onSearch()}
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
