// 문제 아이템

const levelColor = {
    5: "bg-levelColor-5 text-gray-100",
    4: "bg-levelColor-4 text-gray-200",
    3: "bg-levelColor-3 text-gray-400",
    2: "bg-levelColor-2 text-gray-600",
    1: "bg-levelColor-1 text-gray-600",
};



// ProblemItem 컴포넌트 정의
const ProblemItem = ({ id, level, title, category, passRate, solved, createdAt, onClick }) => {
    // 난이도에 따른 색상 매핑 (예시, 실제 사용 시에는 `levelColor` 객체 필요)
    const levelColor = {
        1: "bg-green-200 text-green-800",
        2: "bg-blue-200 text-blue-800",
        3: "bg-yellow-200 text-yellow-800",
        4: "bg-orange-200 text-orange-800",
        5: "bg-red-200 text-red-800",
    };

    return (
        <div
            className="grid grid-cols-[60px_90px_2fr_1.2fr_90px_90px_100px] gap-4 items-center px-6 py-3 cursor-pointer hover:bg-gray-50 transition-colors duration-200"
            onClick={() => onClick && onClick(id)}
        >



            {/* 번호 */}
            <div className="text-center font-medium text-gray-700">{id}</div>
            {/* 난이도 */}
            <div
                className={`text-center px-2 py-1 rounded-full text-xs font-bold ${
                    levelColor[level] || "bg-gray-300 text-gray-800"
                }`}
            >
                Lv. {level}
            </div>

            {/* 문제 제목 */}
            <div className="pl-4 font-semibold text-blue-700 flex items-center">
                <span className="hover:underline">{title}</span>
                {solved && (
                    <span className="ml-2 px-2 py-0.5 bg-green-500 text-white text-xs font-bold rounded-full">
                        SOLVED
                    </span>
                )}
            </div>

            {/* 카테고리 */}
            <div className="text-center text-gray-600">{category}</div>


            {/* 정답률 */}
            <div className="text-center font-medium text-gray-700">{passRate}</div>

            {/* 생성일 */}
            <div className="text-center text-sm text-gray-500">{createdAt}</div>
        </div>
    );
};

export default ProblemItem;
