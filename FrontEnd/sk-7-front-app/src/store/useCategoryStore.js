// 정렬 카테고리 전역 상태

import { create } from "zustand";

const getStorage = (key, defaultValue) => {
    const stored = localStorage.getItem(key);
    return stored !== null ? JSON.parse(stored) : defaultValue;
};

// 정렬 기준, 오름/내림차순, 내가 푼 문제 제외
const useCategoryStore = create((set) => ({
    sort: getStorage("sort", "recent"),
    status: getStorage("status", "unsolved"),
    category: getStorage("category", []),
    level: getStorage("level", null),
    setSort: (val) => {
        localStorage.setItem("sort", JSON.stringify(val));
        set({ sort: val });
    },
    setStatus: (val) => {
        localStorage.setItem("status", JSON.stringify(val));
        set({ status: val });
    },
    setCategory: (val) => {
        localStorage.setItem("category", JSON.stringify(val));
        set({ category: val });
    },
    setLevel: (val) => {
        localStorage.setItem("level", JSON.stringify(val));
        set({ level: val });
    },
}));

export default useCategoryStore;
