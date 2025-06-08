import ProblemSection from "../components/molecules/ProblemSection";
import CodeEditorSection from "../components/molecules/CodeEditorSection";
import { useParams } from "react-router-dom";
import { useState, useEffect, useRef, useCallback } from "react";
import getProblemDetail from "../api/getProblemDetail";

const MD_BREAKPOINT = 768;

function SolvePage() {
    // /solve/{id}
    const { id } = useParams();

    const [data, setData] = useState(null);

    const chatComponentRef = useRef(null); // ChatComponent의 메서드 호출용 ref

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // 리사이저 상태
    const [isMobileView, setIsMobileView] = useState(
        window.innerWidth < MD_BREAKPOINT
    );
    const [leftPanelWidth, setLeftPanelWidth] = useState(50); // 초기는 반반
    const isResizingHorizontal = useRef(false); // 수평 리사이징 상태
    const mainContainerRef = useRef(null);

    // CodeEditorSection에서 사용자가 코드를 로컬에서 변경했을 때 호출될 콜백 함수
    const handleLocalCodeEdit = useCallback((newCodeFromEditor) => {
        if (
            chatComponentRef.current &&
            typeof chatComponentRef.current.sendCodeUpdateFromParent ===
                "function"
        ) {
            chatComponentRef.current.sendCodeUpdateFromParent(
                newCodeFromEditor
            );
        }
    }, []); // chatComponentRef는 ref

    useEffect(() => {
        const checkMobileView = () => {
            const mobile = window.innerWidth < MD_BREAKPOINT;
            setIsMobileView(mobile);
            if (mobile) {
                setLeftPanelWidth(50); // 또는 모바일용 다른 레이아웃 처리
            }
        };
        window.addEventListener("resize", checkMobileView);
        checkMobileView(); // Initial check on mount
        return () => window.removeEventListener("resize", checkMobileView);
    }, []);

    // --- 수평 리사이저 이벤트 핸들러 (Desktop Only) ---
    const handleMouseDownOnHorizontalResizer = useCallback(
        (e) => {
            if (isMobileView) return; // Resizer only for desktop
            e.preventDefault();
            isResizingHorizontal.current = true;
            document.body.style.cursor = "col-resize"; // 수평 리사이징 커서
            document.body.style.userSelect = "none";
        },
        [isMobileView]
    );

    const handleMouseMoveHorizontal = useCallback(
        (e) => {
            if (
                !isResizingHorizontal.current ||
                !mainContainerRef.current ||
                isMobileView
            ) {
                return;
            }
            const containerRect =
                mainContainerRef.current.getBoundingClientRect();
            let newWidth =
                ((e.clientX - containerRect.left) / containerRect.width) * 100;

            // 너비 제한 (예: 20% ~ 80%)
            const minWidthPercent = 20;
            const maxWidthPercent = 80;
            newWidth = Math.max(
                minWidthPercent,
                Math.min(newWidth, maxWidthPercent)
            );
            setLeftPanelWidth(newWidth);
        },
        [isMobileView]
    ); // mainContainerRef.current는 ref이므로 의존성 배열에 필요 없음

    const handleMouseUpHorizontal = useCallback(() => {
        if (isResizingHorizontal.current) {
            isResizingHorizontal.current = false;
            document.body.style.cursor = "default";
            document.body.style.userSelect = "auto";
        }
    }, []);

    // 수평 크기 조정을 위한 글로벌 마우스 이동/업 리스너
    useEffect(() => {
        if (isMobileView) {
            if (isResizingHorizontal.current) {
                // 모바일 전환 시 정리
                isResizingHorizontal.current = false;
                document.body.style.cursor = "default";
                document.body.style.userSelect = "auto";
            }
            return;
        }

        window.addEventListener("mousemove", handleMouseMoveHorizontal);
        window.addEventListener("mouseup", handleMouseUpHorizontal);
        return () => {
            window.removeEventListener("mousemove", handleMouseMoveHorizontal);
            window.removeEventListener("mouseup", handleMouseUpHorizontal);
            if (isResizingHorizontal.current) {
                // 언마운트 시 정리
                document.body.style.cursor = "default";
                document.body.style.userSelect = "auto";
            }
        };
    }, [isMobileView, handleMouseMoveHorizontal, handleMouseUpHorizontal]);

    useEffect(() => {
        setLoading(true);
        setError("");
        getProblemDetail(id)
            .then((res) => setData(res))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen bg-gray-100">
                <span className="text-gray-500 text-lg">
                    문제 정보를 불러오는 중...
                </span>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex flex-col items-center justify-center h-screen bg-gray-100 p-4">
                <span className="text-red-600 text-xl font-semibold mb-2">
                    오류 발생
                </span>
                <span className="text-red-500 text-center">{error}</span>
                <button
                    onClick={() => window.location.reload()}
                    className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                >
                    새로고침
                </button>
            </div>
        );
    }
    return (
        <div className="flex flex-col h-full">
            <header className="bg-gray-700 text-white p-4 text-xl font-bold">
                문제 ID : {id}
            </header>
            <main
                className={`flex flex-1 min-h-0 ${
                    isMobileView
                        ? "flex-col overflow-y-auto"
                        : "flex-row overflow-hidden"
                }`}
                ref={mainContainerRef}
            >
                {/* Problem Section */}
                <div
                    className={`relative bg-white shadow-sm 
                                ${
                                    isMobileView
                                        ? "w-full min-h-[50vh] border-b border-gray-300 overflow-y-auto" // 모바일: 세로 분할, 자체 스크롤
                                        : "h-full overflow-hidden"
                                }`} // 데스크탑: 가로 분할, 내부 스크롤은 ProblemSection이 담당
                    style={
                        isMobileView
                            ? {} // 모바일에서는 flex로 크기 조절 또는 고정 높이
                            : { width: `${leftPanelWidth}%`, flex: "none" }
                    }
                >
                    {/* ProblemSection 자체가 내부 스크롤을 갖도록 설계 */}
                    <ProblemSection
                        detail={data}
                        // ChatComponent에 필요한 props 전달
                        p_id={id} // ChatComponent가 방 생성/참여 시 문제 ID를 알 수 있도록
                        lang={data?.language || "default"} // ChatComponent가 언어 정보를 알 수 있도록 (API 응답 구조에 따라 수정)
                        chatComponentRef={chatComponentRef} // ChatComponent 인스턴스 접근용
                    />
                </div>

                {/* Horizontal Resizer */}
                {!isMobileView && (
                    <div
                        className="w-1.5 hover:w-2 bg-gray-600 hover:bg-blue-500 cursor-col-resize
                                   flex items-center justify-center flex-none
                                   transition-colors duration-100 ease-in-out"
                        onMouseDown={handleMouseDownOnHorizontalResizer}
                    >
                        <div className="w-px h-10 bg-gray-500 opacity-60 rounded-full"></div>
                    </div>
                )}

                {/* Code Editor Section */}
                <div
                    className={`relative flex-1 bg-gray-700 
                                ${
                                    isMobileView
                                        ? "w-full min-h-[50vh] flex flex-col"
                                        : "h-full flex flex-col overflow-hidden"
                                }`}
                >
                    {/* CodeEditorSection이 h-full과 flex flex-col 등을 통해 부모를 채우도록 설계 */}
                    <CodeEditorSection
                        detail={data}
                        onLocalCodeEdit={handleLocalCodeEdit}
                    />
                </div>
            </main>
        </div>
    );
}

export default SolvePage;
