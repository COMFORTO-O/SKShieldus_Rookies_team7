// 문제 리스트 상태 저장소

import { createStore } from "zustand";

const useProblemListStore = createStore((set) => ({
    problemList: [],
    setProblemList: (list) => set({ problemList: list }),
    addProblem: (problem) =>
        set((state) => ({
            problemList: [...state.problemList, problem],
        })),
    clearProblemList: () => set({ problemList: [] }),
}));

export default useProblemListStore;
