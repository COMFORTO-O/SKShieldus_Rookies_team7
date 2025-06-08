import ChatJoinComponent from "../atoms/ChatJoinComponent";
import PropTypes from "prop-types";

function ProblemSectionForJoin({ detail, roomId, chatComponentRef }) {
    return (
        <div className="flex-1 h-full p-3 overflow-auto bg-gray-700 border-y border-r border-gray-600 ">
            <section className="flex flex-col h-full gap-2">
                <section className="flex-1 flex flex-col overflow-auto">
                    <div className="text-white mb-2 font-bold text-xl">
                        {detail.title}
                    </div>
                    <div className="w-full h-[390px] resize-none bg-gray-700 text-white overflow-auto p-2">
                        {detail.detail}
                    </div>
                </section>

                <section className="flex-none text-white flex flex-col">
                    {/* 채팅 메시지 출력 영역 */}
                    <ChatJoinComponent roomId={roomId} ref={chatComponentRef} />
                </section>
            </section>
        </div>
    );
}

ProblemSectionForJoin.propTypes = {
    detail: PropTypes.shape({
        id: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
            .isRequired,
        title: PropTypes.string,
        detail: PropTypes.string,
        category: PropTypes.shape({
            code: PropTypes.string.isRequired,
            description: PropTypes.string,
            id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        }),
        level: PropTypes.number,
        memberName: PropTypes.string,
        solved: PropTypes.bool,
        testCase: PropTypes.array,
        createdAt: PropTypes.string,
        updatedAt: PropTypes.string,
    }).isRequired,
    roomId: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
        .isRequired,
    chatComponentRef: PropTypes.oneOfType([
        PropTypes.func,
        PropTypes.shape({ current: PropTypes.any }),
    ]),
};

export default ProblemSectionForJoin;
