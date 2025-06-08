import axios from "axios";
import PropTypes from "prop-types";

// 방 생성 요청
const createRoom = async ({ problemId, language, code }) => {
    try {
        const response = await axios.post(
            "http://localhost:8080/api/rooms",
            { problemId: problemId, language: language, code: code },
            { withCredentials: true }
        );

        return response.data;
    } catch (error) {
        console.error("방 생성 실패:", error);
    }
};

// propTypes 정의
createRoom.propTypes = {
    problemId: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
        .isRequired,
    language: PropTypes.string.isRequired,
    code: PropTypes.string.isRequired,
};

export default createRoom;
