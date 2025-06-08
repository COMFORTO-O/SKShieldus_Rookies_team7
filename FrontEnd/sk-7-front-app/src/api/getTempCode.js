// /problem/temp/{problemId}

// get

// Yes

// problemId:Long

export default async function getTempCode(problemId) {
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/problem/temp/${problemId}`,
            {
                problemId: problemId,
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
        console.error(err);
    }
}
