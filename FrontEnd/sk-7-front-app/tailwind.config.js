import colors from "tailwindcss/colors";

/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}", // src 폴더 내 모든 관련 파일
    ],
    theme: {
        extend: {
            // 공통 테마 설정
            colors: {
                primary: "var(--color-primary)",
                secondary: "var(--color-secondary)",
                accent: "#f56565",
                "base-100": "#ffffff",
                success: "#36d399",
                fail: "#fbbd23",
                error: "#f87272",
                hoverButton: {
                    400: "#94a3b8",
                    900: "#0f172a",
                },
                melrose: {
                    50: "#f3f3ff",
                    100: "#eae8ff",
                    200: "#d6d5ff",
                    300: "#c1bdff",
                    400: "#9488fd",
                    500: "#7158fa",
                    600: "#5f35f2",
                    700: "#5023de",
                    800: "#431dba",
                    900: "#381a98",
                    950: "#200e67",
                },
                vanila: "#FFF4E3",
                levelColor: {
                    5: "#fa0800",
                    4: "#f05e59",
                    3: "#ffb75e",
                    2: "#d4fa6b",
                    1: "#0afa4a",
                },
            },
            fontFamily: {
                sans: ["Inter", "system-ui", "sans-serif"],
                roboto: ["Roboto", "system-ui", "sans-serif"],
                dot42: ["42dot Sans", "sans-serif"],
                blackhan: ["Black Han Sans", "sans-serif"],
                sourgummy: ["Sour Gummy", "sans-serif"],
            },
        },
    },
    plugins: [],
};
