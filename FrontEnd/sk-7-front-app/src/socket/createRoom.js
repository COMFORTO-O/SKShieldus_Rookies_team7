import axios from "axios";

// 방 생성 요청
// TODO : 초기 코드 같이 보내주기
const createRoom = async ({ problemId, language, code }) => {
    try {
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/rooms`,
            { problemId: problemId, language: language, code: code },
            { withCredentials: true }
        );
        console.log(response.data);

        console.log("방이 생성되었습니다.");

        return response.data;
    } catch (error) {
        console.error("방 생성 실패:", error);
    }
};

export default createRoom;
