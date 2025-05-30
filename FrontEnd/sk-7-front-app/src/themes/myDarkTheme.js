// src/themes/myDarkTheme.js
export const myDarkTheme = {
    base: "vs-dark",
    inherit: true,
    rules: [
        { token: "keyword", foreground: "C586C0" },
        { token: "identifier", foreground: "9CDCFE" },
        { token: "number", foreground: "B5CEA8" },
        { token: "string", foreground: "CE9178" },
        { token: "comment", foreground: "6A9955", fontStyle: "italic" },
    ],
    colors: {
        "editor.background": "#1E1E1E",
    },
};
