import axios from "axios";

// {
//   "status": 0,
//   "code": "string",
//   "message": "string",
//   "data": {
//     "passedCount": 0,
//     "totalCount": 0,
//     "testCaseResults": [
//       {
//         "input": "string",
//         "expectedOutput": "string",
//         "actualOutput": "string",
//         "error": "string",
//         "correct": true
//       }
//     ],
//     "score": 0
//   }

export async function submitCode(p_id, code, lang = "PYTHON") {
    try {
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/compile/score`,
            {
                problemId: p_id,
                code: code,
                language: lang,
            },
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem(
                        "accessToken"
                    )}`,
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }
        );

        return response?.data?.data;
    } catch (err) {
        throw new Error(err?.message || "문제 제출 오류");
    }
}
