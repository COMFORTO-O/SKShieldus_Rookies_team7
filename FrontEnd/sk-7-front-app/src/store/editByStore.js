import { create } from "zustand";

const editByStore = create((set) => ({
    editingBy: null,
    setEditingBy: (val) => set({ editingBy: val }),
    doneEdit: () => set({ editingBy: null }),
}));
export default editByStore;
