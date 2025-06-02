import { create } from "zustand";

const useModalStore = create((set) => ({
    modalOpen: false,
    openModal: () => set({ modalOpen: true }),
    closeModal: () => set({ modalOpen: false }),
    toggleModal: () => set((state) => ({ modalOpen: !state.modalOpen })),
}));

export default useModalStore;
