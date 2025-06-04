import { useState } from "react";

function ProblemSection({ detail }) {
    const [chatMessages, setChatMessages] = useState([
        // 서버에서 받아 온 객체
        { text: "안녕하세요.", isUser: false },
        { text: "반갑습니다.", isUser: true },
    ]);

    const [inputValue, setInputValue] = useState("");

    const handleSendMessage = () => {
        if (inputValue.trim() === "") return;

        const newMessage = { text: inputValue, isUser: true };
        setChatMessages([...chatMessages, newMessage]);
        setInputValue("");
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleSendMessage();
        }
    };

    return (
        <div className="p-3 overflow-auto bg-gray-700 border-y border-r border-gray-600">
            <section className="flex flex-col h-full gap-2">
                <section className="flex-1 flex flex-col overflow-auto">
                    <div className="text-white mb-2 font-bold text-xl">
                        {detail.title}
                    </div>
                    <div className="w-full h-[390px] resize-none bg-gray-700 text-white overflow-auto p-2">
                        {detail.detail}
                    </div>
                </section>

                <section className="flex-1 text-white flex flex-col">
                    <div className="mb-2 font-bold text-xl">채팅</div>

                    {/* 채팅 메시지 출력 영역 */}
                    <div className=" bg-gray-900 overflow-auto p-2 rounded border border-gray-700 space-y-2 h-[390px]">
                        <div className="pt-3">
                            <span className="text-gray-400 text-sm">나 </span>
                            <span className="bg-gray-800 p-2 rounded w-fit max-w-[70%]">
                                안녕하세요.
                            </span>
                        </div>
                        <div className="pt-3">
                            <span className="text-gray-400 text-sm">상대 </span>
                            <span className="bg-blue-800 p-2 rounded w-fit max-w-[70%]">
                                반갑습니다.
                            </span>
                        </div>
                        {/* {chatMessages.map((msg, idx) => (
                            <div
                                key={idx}
                                className={`p-2 rounded max-w-[70%] ${
                                    msg.isUser
                                        ? "bg-blue-600 self-end"
                                        : "bg-gray-800"
                                }`}
                            >
                                {msg.text}
                            </div>
                        ))} */}
                    </div>

                    {/* 입력창 */}
                    <div className="mt-2 flex gap-2 pb-5">
                        <input
                            type="text"
                            placeholder="메시지를 입력하세요"
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                            onKeyDown={handleKeyDown}
                            className="flex-1 p-2 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none"
                        />
                        <button
                            type="submit"
                            onClick={handleSendMessage}
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
