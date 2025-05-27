import { create } from "zustand";

const useCategoryStore = create((set) => ({
    sort: "recent",
    selectedLanguages: [],
    setSort: (sort) => set({ sort }),
    setSelectedLanguages: (langs) => set({ selectedLanguages: langs }),
}));

export default useCategoryStore;
