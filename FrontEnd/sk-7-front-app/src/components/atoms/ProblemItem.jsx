// 문제 아이템

const levelColor = {
    3: "bg-levelColor-hard text-gray-300",
    2: "bg-levelColor-medium text-gray-400",
    1: "bg-levelColor-easy text-gray-400",
};

const ProblemItem = ({ level, title, p_id, onClick }) => {
    return (
        <div
            className="flex items-center justify-between border rounded-lg px-4 py-3 mb-2 cursor-pointer hover:border-primary hover:border-2"
            onClick={() => onClick && onClick(p_id)}
        >
            {/* 상태 */}
            <div className="w-8">state</div>

            {/* 제목 */}
            <span className="flex-1 ml-4 font-semibold">{title}</span>

            {/* 난이도 */}
            <span
                className={`mx-5 ml-4 px-2 py-1 rounded text-xs font-bold  ${
                    levelColor[level] || "bg-gray-300"
                }`}
            >
                Level {level}
            </span>
            {/* 정답률 */}
            <span className="">정답률</span>
        </div>
    );
};

export default ProblemItem;
