import { useState, useEffect, useRef, useCallback } from "react";
import LanguageSelect from "../atoms/LanguageSelect";
import Editor, { useMonaco } from "@monaco-editor/react";
import { Button } from "@mui/material";
import { sendUserCode } from "../../api/sendCode";
import useCodeStore from "../../store/useCodeStore";
import RoleStore from "../../store/RoleStore";
import editByStore from "../../store/editByStore";
import useAuthStore from "../../store/useAuthStore";
import { submitCode } from "../../api/submitCode";

function CodeEditorSection({ detail, onLocalCodeEdit }) {
    // 코드 상태
    const { code, setCode, resetCode } = useCodeStore();
    const { role } = RoleStore();
    const { editingBy } = editByStore();
    const { userEmail: currentUser } = useAuthStore();
    const [languages, setLanguages] = useState(detail.languages);
    const [selectedLanguage, setSelectedLanguage] = useState(
        detail.languages[0].code
    ); // 기본값 설정 예시

    const [result, setResult] = useState(null); // 실행 결과 상태
    const [score, setScore] = useState(0);
    const [scoreMessage, setScoreMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const monaco = useMonaco();
    const editorRef = useRef(null);

    // 리사이저 상태
    const [editorSectionHeightPercent, setEditorSectionHeightPercent] =
        useState(80); // 에디터 영역
    const isResizingVertical = useRef(false);
    const resizableContainerRef = useRef(null); // 에디터와 결과창을 포함하는 컨테이너

    useEffect(() => {
        if (!monaco) return;
    }, [monaco, selectedLanguage]);

    // 에디터 핸들러
    const handleEditorMount = useCallback((editor) => {
        editorRef.current = editor;
    }, []);

    // 사용자가 에디터 내용을 변경했을 때 호출될 함수
    const handleEditorContentChange = useCallback(
        (value) => {
            setCode(value);
            if (onLocalCodeEdit) {
                onLocalCodeEdit(value); // 변경된 코드를 부모에게 알림
            }
        },
        [setCode, onLocalCodeEdit]
    );

    // 실행 버튼 클릭 시
    const handleRunCode = useCallback(async () => {
        if (!detail || !detail.id) {
            setResult({ message: "문제 정보를 가져올 수 없습니다." });
            return;
        }
        setLoading(true);
        setResult("실행 중...");
        try {
            const res = await sendUserCode(detail.id, code, selectedLanguage);
            setResult(res ?? "결과 없음");
        } catch (err) {
            setResult(err || "실행 중 오류 발생");
        } finally {
            setLoading(false);
        }
    }, [detail, code, selectedLanguage]);

    // 초기화 버튼 클릭 시
    const handleResetCode = useCallback(() => {
        if (window.confirm("정말로 초기화하시겠습니까?")) {
            resetCode();

            if (onLocalCodeEdit) {
                onLocalCodeEdit(code); // 초기화된 코드를 전송
            }

            setResult(null);
        }
    }, [resetCode, onLocalCodeEdit, code]);

    // 제출 버튼 클릭 시
    const handleSubmitCode = useCallback(async () => {
        setLoading(true);
        setResult("실행 중...");

        try {
            const res = await submitCode(detail.id, code, selectedLanguage);
            console.log(res)
            setScore(res.score);
            setResult(res ?? "결과 없음");

            if (res.score > 0) {
                setScoreMessage(
                    `정답입니다!${res.score > 0 ? ` (+${res.score}점)` : ""}`
                );
            } else {
                setScoreMessage("틀렸습니다.");
            }
        } catch (err) {
            setResult(err || "제출 중 오류 발생");
            setScoreMessage("");
        } finally {
            setLoading(false);
        }
    }, [detail, code, selectedLanguage, score,setScore]);

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
        const minHeightPercent = 20;
        const maxHeightPercent = 80;
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
        <div className="flex-1 flex flex-col h-full bg-gray-700 border-t border-gray-600">
            {/* 상단 컨트롤 영역 */}
            <div className="flex-none flex justify-end items-center p-2 border-b border-gray-700">
                <div className="w-[150px]">
                    <LanguageSelect
                        languages={languages}
                        selectedLanguage={selectedLanguage}
                        setSelectedLanguage={setSelectedLanguage}
                    />
                </div>
            </div>

            {/* 에디터 영역 + 실행 결과 영역 */}
            <div
                ref={resizableContainerRef}
                className="flex-1 flex flex-col min-h-0 overflow-hidden"
            >
                {/* 에디터 영역 */}
                <section
                    className=" min-h-0 relative"
                    style={{ height: `${editorSectionHeightPercent}%` }}
                >
                    <Editor
                        height="100%"
                        width="100%"
                        language={selectedLanguage}
                        value={code}
                        onChange={handleEditorContentChange} // 사용자가 내용 변경 시 호출될 함수
                        theme="vs-dark"
                        onMount={handleEditorMount}
                        options={{
                            automaticLayout: true,
                            fontSize: 16,
                            scrollBeyondLastLine: false,
                            minimap: { enabled: true },
                            wordWrap: "on", // 자동 줄바꿈
                        }}
                        disabled={
                            role !== "CHAT_AND_EDIT" ||
                            (editingBy && editingBy !== currentUser)
                        }
                    />
                </section>

                {/* 세로 리사이저 핸들 */}
                <div
                    className="h-1.5 hover:h-2 bg-gray-600 hover:bg-blue-500 cursor-row-resize flex-none
                               transition-all duration-100 ease-in-out"
                    onMouseDown={handleMouseDownOnVerticalResizer}
                    onTouchStart={handleMouseDownOnVerticalResizer} // 기본 터치 지원
                ></div>

                {/* 실행 결과 영역 */}
                <div className="flex-1 flex flex-col min-h-[200px]  border-t border-gray-700">
                    <h2 className="text-white text-md font-semibold p-2 bg-gray-700 flex-none">
                        실행 결과
                    </h2>
                    {/* 점수 메시지 표시 */}
                    {scoreMessage && (
                        <div
                            className="p-2 text-center text-lg font-bold"
                            style={{ color: score > 0 ? "#22c55e" : "#ef4444" }}
                        >
                            {scoreMessage}
                        </div>
                    )}
                    <div className="flex-1 bg-gray-900 p-2 overflow-auto text-sm text-white">
                        {loading ? (
                            <div className="p-4 text-center text-gray-400">
                                실행 중...
                            </div>
                        ) : (
                            <div className="space-y-4 p-4">
                                {/* 각 테스트 케이스 결과 사이에 간격, 전체 패딩 */}
                                {result &&
                                result.testCaseResults &&
                                result.testCaseResults.length > 0 ? (
                                    result.testCaseResults.map(
                                        (testCase, index) => (
                                            <div
                                                key={index} // 배열을 매핑할 때는 고유한 key prop이 필수입니다.
                                                className={`p-4 rounded-md shadow ${
                                                    testCase.correct
                                                        ? "bg-green-50 border-green-300"
                                                        : "bg-red-50 border-red-300"
                                                } border`}
                                            >
                                                <div className="flex justify-between items-center mb-2">
                                                    <h3 className="font-semibold text-lg text-gray-800">
                                                        테스트 케이스 #
                                                        {index + 1}
                                                    </h3>
                                                    <span
                                                        className={`px-3 py-1 text-xs font-semibold rounded-full ${
                                                            testCase.correct
                                                                ? "bg-green-200 text-green-800"
                                                                : "bg-red-200 text-red-800"
                                                        }`}
                                                    >
                                                        {testCase.correct
                                                            ? "성공"
                                                            : "실패"}
                                                    </span>
                                                </div>

                                                {/* 입력 */}
                                                {testCase.input !==
                                                    undefined && ( // 입력 값이 있는 경우에만 표시
                                                    <div className="mb-2">
                                                        <p className="text-sm font-medium text-gray-600">
                                                            입력:
                                                        </p>
                                                        <pre className="p-2 bg-gray-100 rounded text-sm text-gray-700 whitespace-pre-wrap break-all">
                                                            {testCase.input}
                                                        </pre>
                                                    </div>
                                                )}

                                                {/* 예상 출력 */}
                                                {testCase.expectedOutput !==
                                                    undefined && ( // 예상 출력 값이 있는 경우에만 표시
                                                    <div className="mb-2">
                                                        <p className="text-sm font-medium text-gray-600">
                                                            예상 출력:
                                                        </p>
                                                        <pre className="p-2 bg-gray-100 rounded text-sm text-gray-700 whitespace-pre-wrap break-all">
                                                            {
                                                                testCase.expectedOutput
                                                            }
                                                        </pre>
                                                    </div>
                                                )}

                                                {/* 실제 출력 또는 에러 메시지 */}
                                                {testCase.error ? (
                                                    <div className="mb-2">
                                                        <p className="text-sm font-medium text-red-600">
                                                            에러:
                                                        </p>
                                                        <pre className="p-2 bg-red-50 border border-red-200 rounded text-sm text-red-700 whitespace-pre-wrap break-all">
                                                            {testCase.error}
                                                        </pre>
                                                    </div>
                                                ) : (
                                                    testCase.actualOutput !==
                                                        undefined && ( // 에러가 없고 실제 출력 값이 있는 경우
                                                        <div className="mb-2">
                                                            <p className="text-sm font-medium text-gray-600">
                                                                실제 출력:
                                                            </p>
                                                            <pre className="p-2 bg-gray-100 rounded text-sm text-gray-700 whitespace-pre-wrap break-all">
                                                                {
                                                                    testCase.actualOutput
                                                                }
                                                            </pre>
                                                        </div>
                                                    )
                                                )}

                                                {/* 추가 정보 (시간, 메모리 등) */}
                                            </div>
                                        )
                                    )
                                ) : (
                                    <div className="p-4 text-center text-gray-500">
                                        {result && result.message
                                            ? result.message
                                            : "실행 결과가 없습니다."}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            {/* 하단 버튼 영역 */}
            <div className="flex-none flex flex-row-reverse gap-3 items-center p-2 bg-gray-700 border-t border-gray-600">
                <Button
                    variant="contained"
                    color="success"
                    size="small"
                    sx={{ mr: 1 }}
                    onClick={handleSubmitCode}
                    disabled={loading}
                >
                    제출
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    size="small"
                    onClick={handleRunCode}
                    disabled={loading}
                >
                    실행
                </Button>
                <Button
                    variant="contained"
                    sx={{ backgroundColor: "#44576c", color: "#fff" }}
                    size="small"
                    onClick={handleResetCode} // 초기화 버튼 핸들러 연결
                >
                    초기화
                </Button>
            </div>
        </div>
    );
}

export default CodeEditorSection;
