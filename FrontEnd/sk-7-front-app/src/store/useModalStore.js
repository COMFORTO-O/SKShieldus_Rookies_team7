import { create } from "zustand";

const useModalStore = create((set) => ({
    infoModalOpen: false,
    openInfoModal: () => set({ infoModalOpen: true }),
    closeInfoModal: () => set({ infoModalOpen: false }),
    toggleInfoModal: () =>
        set((state) => ({ infoModalOpen: !state.infoModalOpen })),
    noticeModalOpen: false,
    openNoticeModal: () => set({ noticeModalOpen: true }),
    closeNoticeModal: () => set({ noticeModalOpen: false }),
    toggleNoticeModal: () =>
        set((state) => ({ noticeModalOpen: !state.noticeModalOpen })),
}));

export default useModalStore;
