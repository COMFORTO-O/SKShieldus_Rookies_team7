import PropTypes from "prop-types";

export default function RoomItem({
    id,
    title,
    owner,
    problemId,
    language,
    memberCount,
    onJoin,
}) {
    return (
        <div
            className="bg-white shadow-lg rounded-lg p-4 cursor-pointer hover:shadow-xl transition-shadow duration-200 flex flex-col justify-between"
            onClick={onJoin} // div 전체를 클릭 가능하게 하거나, 내부에 버튼을 만들 수 있음
        >
            <div>
                <div>방 ID: {id}</div>
                <div>문제 ID: {problemId}</div>
                <h3
                    className="text-lg font-semibold text-gray-800 mb-1 truncate"
                    title={title}
                >
                    {title}
                </h3>
                <p className="text-xs text-gray-500 mb-1">
                    개설자: {owner.name}
                </p>
                {language && (
                    <p className="text-xs text-gray-500 mb-1">
                        언어: {language}
                    </p>
                )}
            </div>
            <div className="flex justify-between items-center mt-2">
                {memberCount !== undefined && (
                    <span className="text-xs text-gray-600">
                        {memberCount}명 참여중
                    </span>
                )}
                <button
                    className="px-3 py-1 bg-blue-500 text-white text-xs rounded hover:bg-blue-600"
                    onClick={(e) => {
                        e.stopPropagation();
                        onJoin();
                    }} // 버튼 클릭 시 이벤트 전파 중지
                >
                    참여하기
                </button>
            </div>
        </div>
    );
}

RoomItem.propTypes = {
    id: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    owner: PropTypes.shape({
        name: PropTypes.string.isRequired,
    }).isRequired,
    problemId: PropTypes.string.isRequired,
};
