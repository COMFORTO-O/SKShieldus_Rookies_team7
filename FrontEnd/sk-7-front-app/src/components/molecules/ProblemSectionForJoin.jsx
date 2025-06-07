import useCodeStore from "../../store/useCodeStore";
import ChatComponent from "../atoms/ChatComponent";
import ChatJoinComponent from "../atoms/ChatJoinComponent";

function ProblemSectionForJoin({ detail, roomId }) {
    // 코드 상태
    const { code, setCode, resetCode } = useCodeStore();
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
                    <ChatJoinComponent roomId={roomId} />
                </section>
            </section>
        </div>
    );
}

export default ProblemSectionForJoin;
