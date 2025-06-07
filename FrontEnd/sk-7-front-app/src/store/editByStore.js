import { create } from "zustand";

const editByStore = create((set) => ({
    editingBy: "",
    setEdit: (val) => set({ editingBy: val }),
    doneEdit: () => set({ editingBy: "" }),
}));
export default editByStore;
