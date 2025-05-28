// 인증 관련
// 로그인 유무, 쿠키 상태

import { create } from "zustand";

const useAuthStore = create((set) => ({
    isLoggedIn: false,
    user: null,
    setLogin: (user) => set({ isLoggedIn: true, user }),
    setLogout: () => set({ isLoggedIn: false, user: null }),
}));

export default useAuthStore;
