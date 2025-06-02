import Editor from "@monaco-editor/react";

const MonacoEditor = ({
    value,
    onChange,
    language,
    height,
    // onValidation,
}) => {
    // function handleEditorValidation(markers = []) {
    //     const markerErrors = Array.from(
    //         new Set(
    //             markers.map((marker) => ({
    //                 startLineNumber: marker.startLineNumber,
    //                 endLineNumber: marker.endLineNumber,
    //             }))
    //         )
    //     );
    //     onValidation(markerErrors);
    // }

    return (
        <Editor
            height={height}
            language={language}
            value={value}
            onChange={onChange}
            theme="vs-dark"
            // onValidate={handleEditorValidation}
        />
    );
};

export default MonacoEditor;
