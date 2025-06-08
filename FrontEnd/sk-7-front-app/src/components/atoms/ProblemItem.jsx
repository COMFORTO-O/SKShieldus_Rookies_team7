import PropTypes from "prop-types";

// 문제 아이템
const levelColor = {
    5: "bg-levelColor-5 text-gray-100",
    4: "bg-levelColor-4 text-gray-200",
    3: "bg-levelColor-3 text-gray-400",
    2: "bg-levelColor-2 text-gray-600",
    1: "bg-levelColor-1 text-gray-600",
};

const ProblemItem = ({ level, title, passRate, solved, p_id, onClick }) => {
    return (
        <div
            className="flex items-center justify-between border rounded-lg px-4 py-3 mb-2 cursor-pointer hover:border-primary hover:border-2"
            onClick={() => onClick && onClick(p_id)}
        >
            {/* 상태 */}
            <div className="w-8 text-center">{solved ? "v" : ""}</div>

            {/* 제목 */}
            <span className="flex-1 ml-4 font-semibold">{title}</span>

            {/* 난이도 */}
            <span
                className={`w-16 ml-4 px-2 py-1 rounded text-xs font-bold text-center ${
                    levelColor[level] || "bg-gray-300"
                }`}
            >
                Level {level}
            </span>
            {/* 정답률 */}
            <span className="w-16 ml-4 text-center">{passRate}</span>
        </div>
    );
};

ProblemItem.propTypes = {
    level: PropTypes.number.isRequired,
    title: PropTypes.string.isRequired,
    passRate: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    solved: PropTypes.bool,
    p_id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    onClick: PropTypes.func,
};

export default ProblemItem;
