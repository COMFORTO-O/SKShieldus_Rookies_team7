import { create } from "zustand";

const useCodeStore = create((set) => ({
    code: "// 코드를 입력하세요.",
    setCode: (val) => set({ code: val }),
    resetCode: () => set({ code: "// 코드를 입력하세요." }),
}));

export default useCodeStore;
