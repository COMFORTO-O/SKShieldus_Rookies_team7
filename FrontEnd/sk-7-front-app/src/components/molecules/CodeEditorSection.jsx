import { useState, useEffect } from "react";

import LanguageSelect from "../atoms/LanguageSelect";
import Button from "../atoms/Button";

import Editor, { useMonaco } from "@monaco-editor/react";

function CodeEditorSection() {
    const [selectedLanguage, setSelectedLanguage] = useState("");
    const [code, setCode] = useState("// 코드를 입력하세요.");

    const monaco = useMonaco();
    // 내가 사용할 모나코 인스턴스를 생성한다.

    useEffect(() => {
        if (!monaco) return;
        // 모나코 인스턴스가 null이면 early return을 해준다.

        monaco.editor.setTheme("tomorrow");
        // 내가 사용하는 모나코 에디터에 테마를 적용해준다.
    }, [monaco]);

    return (
        <div className="p-3 overflow-auto bg-gray-700 border-y border-gray-600">
            <div className="h-[8%] flex flex-1 items-center">
                <section className="flex-1"></section>
                <section className="flex-1">
                    <LanguageSelect
                        selectedLanguage={selectedLanguage}
                        setSelectedLanguage={setSelectedLanguage}
                    />
                </section>
            </div>

            <section className="h-[82%]">
                <Editor
                    height="100%"
                    language={selectedLanguage}
                    value={code}
                    onChange={(value) => setCode(value)}
                    theme="tomorrow"
                />
            </section>

            <div className="h-[10%] flex flex-1">
                <section className="flex-1"></section>
                <section className="flex justify-end items-center flex-1 space-x-4">
                    <Button onClick={() => setCode("")} className="bg-gray-200">
                        초기화
                    </Button>
                    <Button
                        className="bg-gray-200"
                        onClick={() => console.log("실행된 코드:", code)}
                    >
                        코드 실행
                    </Button>
                    <Button
                        className="bg-blue-600 text-white"
                        onClick={() => alert("제출 완료!")}
                    >
                        제출 후 채점
                    </Button>
                </section>
            </div>
        </div>
    );
}

export default CodeEditorSection;
