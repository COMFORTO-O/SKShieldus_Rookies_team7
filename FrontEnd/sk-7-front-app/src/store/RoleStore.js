import { create } from "zustand";

const RoleStore = create((set) => ({
    role: "CHAT_ONLY",
    setRole: (val) => set({ role: val }),
}));

export default RoleStore;
