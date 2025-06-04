import { useState } from "react";

function ProblemSection({ detail }) {
    // 채팅창 활성화 플래그
    const [chatFlag, setChatFlag] = useState(false);

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
                    <div className="mb-2 font-bold text-xl">채팅</div>

                    {/* 채팅 메시지 출력 영역 */}
                    <div className=" bg-gray-900 overflow-auto p-2 rounded border border-gray-700 space-y-2 h-[200px]"></div>

                    {/* 입력창 */}
                    <div className="mt-2 flex gap-2 pb-5">
                        <input
                            type="text"
                            placeholder="메시지를 입력하세요"
                            className="flex-1 p-2 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none"
                        />
                        <button
                            type="submit"
                            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded text-white"
                        >
                            전송
                        </button>
                    </div>
                </section>
            </section>
        </div>
    );
}

export default ProblemSection;
