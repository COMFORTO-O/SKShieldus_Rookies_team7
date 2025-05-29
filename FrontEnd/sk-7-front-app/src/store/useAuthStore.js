// 인증 관련
// 로그인 유무, 쿠키 상태

import { create } from "zustand";
import axios from "axios";

async function refreshAccessToken() {
    try {
        // 서버의 refreshToken 엔드포인트로 요청 (쿠키는 자동으로 전송됨)
        const response = await axios.post("");
        if (!response.ok) {
            throw new Error("Refresh token 요청 실패");
        }
        const data = await response.json();
        return data.accessToken; // 새로운 accessToken 반환
    } catch (error) {
        console.error("Error refreshing token:", error);
        return null;
    }
}

const useAuthStore = create((set, get) => ({
    isLoggedIn: false,
    accessToken: null,
    setLogin: (token) => {
        set({ isLoggedIn: true, accessToken: token });
    },
    setLogout: async () => {
        // 서버로 로그아웃 요청 보내서 RefreshToken 무효화
        try {
            await axios.post("/api/auth/logout");
        } catch (error) {
            console.error("Error during server logout:", error);
        }
        set({ isLoggedIn: false, accessToken: null });
    },
    ensureAuth: async () => {
        const { accessToken, isLoggedIn } = get();

        if (accessToken && isLoggedIn) {
            // accessToken 기한 검사

            // 만료되었다면 refresh 시도

            return true;
        }

        // accessToken이 없거나 만료된 경우 (또는 앱 초기 로드 시)
        const newAccessToken = await refreshAccessToken();
        if (newAccessToken) {
            set({
                isLoggedIn: true,
                accessToken: newAccessToken,
            });
            return true;
        } else {
            // Refresh 실패 시 로그아웃 처리
            set({ isLoggedIn: false, accessToken: null, user: null });
            return false;
        }
    },
}));

export default useAuthStore;
