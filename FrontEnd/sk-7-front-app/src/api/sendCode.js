import axios from "axios";

// {
//   "status": 0,
//   "code": "string",
//   "message": "string",
//   "data": {
//     "passedCount": 0,
//     "totalCount": 0,
//     "testCaseResults": [  //3개
//       {
//         "input": "string",
//         "expectedOutput": "string",
//         "actualOutput": "string",
//         "error": "string",
//         "correct": true
//       }
//    ]
//  }

export async function sendUserCode(p_id, code, lang = "PYTHON") {
    console.log(`id=${p_id}\ncode=${code}\nlang=${lang}`);
    try {
        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/compile/test`,
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

        console.log(response?.data?.data);

        return response?.data?.data;
    } catch (err) {
        throw new Error(err?.message || "문제 채점 오류");
    }
}
