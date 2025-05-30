// 인증 관련
// 로그인 유무, 쿠키 상태

import { create } from "zustand";
import axios from "axios";

const useAuthStore = create((set) => ({
    isLoggedIn: false,
    accessToken: null,
    setLogin: (token) => {
        set({ isLoggedIn: true, accessToken: token });
    },
    setLogout: async () => {
        // 서버로 로그아웃 요청 보내서 Token 삭제
        // try {
        //     await axios.post("/api/auth/logout");
        // } catch (error) {
        //     console.error("Error during server logout:", error);
        // }
        set({ isLoggedIn: false, accessToken: null });
    },
}));

export default useAuthStore;
