import useCodeStore from "../../store/useCodeStore";
import ChatComponent from "../atoms/ChatComponent";
import { useRef, useState, useEffect, useCallback } from "react";

function ProblemSection({ detail, chatComponentRef }) {
    // 코드 상태
    const { code, setCode, resetCode } = useCodeStore();

    const editorRef = useRef(null);
    // 리사이저 상태
    const [editorSectionHeightPercent, setEditorSectionHeightPercent] =
        useState(80); // 에디터 영역
    const isResizingVertical = useRef(null);
    const resizableContainerRef = useRef(null); // 에디터와 결과창을 포함하는 컨테이너

    // 리사이저 이벤트 핸들러
    const handleMouseDownOnVerticalResizer = useCallback((e) => {
        e.preventDefault();
        isResizingVertical.current = true;
        document.body.style.cursor = "row-resize";
        document.body.style.userSelect = "none";
    }, []);
    const handleMouseMoveVertical = useCallback((e) => {
        if (!isResizingVertical.current || !resizableContainerRef.current) {
            return;
        }
        const containerRect =
            resizableContainerRef.current.getBoundingClientRect();
        // containerRect.top 부터 마우스 y 위치까지의 높이를 계산
        let newEditorHeight = e.clientY - containerRect.top;
        let newEditorHeightPercent =
            (newEditorHeight / containerRect.height) * 100;

        // 높이 제한 (예: 에디터 20% ~ 80%)
        const minHeightPercent = 0;
        const maxHeightPercent = 100;
        newEditorHeightPercent = Math.max(
            minHeightPercent,
            Math.min(newEditorHeightPercent, maxHeightPercent)
        );

        setEditorSectionHeightPercent(newEditorHeightPercent);

        // 에디터 레이아웃 강제 업데이트 (필요할 경우)
        if (editorRef.current) {
            editorRef.current.layout();
        }
    }, []); // editorRef.current는 dependency array에 넣지 않아도 됨 (ref는 변경되어도 리렌더링 X)
    const handleMouseUpVertical = useCallback(() => {
        if (isResizingVertical.current) {
            isResizingVertical.current = false;
            document.body.style.cursor = "default";
            document.body.style.userSelect = "auto";
        }
    }, []);

    // Effect for vertical resizer global listeners
    useEffect(() => {
        window.addEventListener("mousemove", handleMouseMoveVertical);
        window.addEventListener("mouseup", handleMouseUpVertical);
        return () => {
            window.removeEventListener("mousemove", handleMouseMoveVertical);
            window.removeEventListener("mouseup", handleMouseUpVertical);
            if (isResizingVertical.current) {
                // 컴포넌트 언마운트 시 정리
                document.body.style.cursor = "default";
                document.body.style.userSelect = "auto";
            }
        };
    }, [handleMouseMoveVertical, handleMouseUpVertical]);
    return (
        <div className="flex-1 h-full overflow-auto bg-gray-700 border-y border-r border-gray-600 ">
            <div
                ref={resizableContainerRef}
                className="flex-1 min-h-0 overflow-hidden flex flex-col h-full gap-2"
            >
                <section
                    className="min-h-0 relative overflow-auto"
                    style={{ height: `${editorSectionHeightPercent}%` }}
                >
                    <div className="text-white mb-2 font-bold text-xl">
                        {detail?.title}
                    </div>
                    <div className="w-full h-[390px] resize-none bg-gray-700 text-white overflow-auto p-2">
                        {detail?.detail}
                    </div>
                </section>

                {/* 세로 리사이저 핸들 */}
                <div
                    className="h-1.5 hover:h-2 bg-gray-600 hover:bg-blue-500 cursor-row-resize flex-none
                               transition-all duration-100 ease-in-out"
                    onMouseDown={handleMouseDownOnVerticalResizer}
                    onTouchStart={handleMouseDownOnVerticalResizer}
                ></div>

                <section className="px-2 flex flex-col min-h-0 overflow-auto">
                    {/* 채팅 메시지 출력 영역 */}
                    <ChatComponent
                        p_id={detail?.id}
                        lang={detail?.category.code}
                        ref={chatComponentRef}
                    />
                </section>
            </div>
        </div>
    );
}

export default ProblemSection;
