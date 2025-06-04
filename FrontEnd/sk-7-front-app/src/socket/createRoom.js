import axios from "axios";
import useAuthStore from "../store/useAuthStore";

/**
 * 방 생성, 입장, 삭제, 목록 조회 기능
 */

// 방 생성

export async function createRoom(params) {
    try {
        const accessToken = useAuthStore.getState().accessToken;
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/room/create`,
            params,
            {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true,
            }
        );
        return response.data?.data;
    } catch (error) {
        console.error("방 생성 실패:", error);
        throw new Error("방 생성에 실패했습니다.");
    }
}

// 방 입장
export async function enterRoom(roomId) {
    try {
        const accessToken = useAuthStore.getState().accessToken;
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/room/enter/${roomId}`,
            {},
            {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: accessToken ? `Bearer ${accessToken}` : "",
                },
                withCredentials: true,
            }
        );
        return response.data?.data;
    } catch (error) {
        console.error("방 입장 실패:", error);
        throw new Error("방 입장에 실패했습니다.");
    }
}

// 방 삭제
export async function deleteRoom(roomId) {
    try {
        const accessToken = useAuthStore.getState().accessToken;
        const response = await axios.delete(
            `${import.meta.env.VITE_API_URL}/api/room/delete/${roomId}`,
            {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: accessToken ? `Bearer ${accessToken}` : "",
                },
                withCredentials: true,
            }
        );
        return response.data?.data;
    } catch (error) {
        console.error("방 삭제 실패:", error);
        throw new Error("방 삭제에 실패했습니다.");
    }
}

// 방 목록 조회
export async function getRoomList() {
    try {
        const accessToken = useAuthStore.getState().accessToken;
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/room/list`,
            {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: accessToken ? `Bearer ${accessToken}` : "",
                },
                withCredentials: true,
            }
        );
        return response.data?.data;
    } catch (error) {
        console.error("방 목록 조회 실패:", error);
        throw new Error("방 목록 조회에 실패했습니다.");
    }
}