import axios from "axios";

export default async function getTempCode(problemId) {
    try {

        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/member/problem/temp/${problemId}`,
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
        console.log(response.data);
        return response?.data;
    } catch (err) {
        console.error(err);
    }
}
