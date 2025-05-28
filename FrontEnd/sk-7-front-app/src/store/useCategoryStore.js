// 정렬 카테고리 전역 상태

import { create } from "zustand";

const useCategoryStore = create((set) => ({
    sort: "recent",
    // selectedLanguages: [],
    setSort: (sort) => set({ sort }),
    // setSelectedLanguages: (langs) => set({ selectedLanguages: langs }),
}));

export default useCategoryStore;
