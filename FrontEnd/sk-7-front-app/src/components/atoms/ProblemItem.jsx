// 문제 아이템

const levelColor = {
    어려움: "bg-levelColor-hard text-white",
    중간: "bg-levelColor-medium text-primary",
    쉬움: "bg-levelColor-easy text-vanila",
};

const ProblemItem = ({ level, title, p_id, createdAt, onClick }) => {
    return (
        <div
            className="flex items-center justify-between border rounded-lg px-4 py-3 mb-2 cursor-pointer hover:bg-secondary transition"
            onClick={() => onClick && onClick(p_id)}
        >
            <div className="flex-1">
                {/* 제목 */}
                <span className=" ml-4 font-semibold">{title}</span>
                {/* 난이도 */}
                <span
                    className={`ml-4 px-2 py-1 rounded text-xs font-bold  ${
                        levelColor[level] || "bg-gray-300"
                    }`}
                >
                    {level}
                </span>
            </div>
            {/* 사용 가능 언어 */}
            {/* <div className="flex gap-1 mr-4">
                {languages.map((lang) => (
                    <span
                        key={lang}
                        className="px-2 py-0.5 rounded bg-base-100 border text-xs text-black"
                    >
                        {lang}
                    </span>
                ))}
            </div> */}
            {/* 등록 시간 */}
            <span className="text-xs text-gray-400">{createdAt}</span>
            {/* 정답률 */}
        </div>
    );
};

export default ProblemItem;
