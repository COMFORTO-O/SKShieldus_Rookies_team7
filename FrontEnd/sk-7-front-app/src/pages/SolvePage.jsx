import ProblemSection from "../components/molecules/ProblemSection";
import CodeEditorSection from "../components/molecules/CodeEditorSection";
import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import getProblemDetail from "../api/getProblemDetail";

function SolvePage() {
    const { id } = useParams();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        setLoading(true);
        setError("");
        getProblemDetail(id)
            .then((res) => setData(res))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <span className="text-gray-500">
                    문제 정보를 불러오는 중...
                </span>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex items-center justify-center h-screen">
                <span className="text-red-500">{error}</span>
            </div>
        );
    }
    return (
        <div className="grid grid-rows-[auto_1fr] h-screen">
            <header className="bg-gray-700 text-white p-4 text-xl font-bold">
                문제 ID : {id}
            </header>
            <main className="grid grid-cols-2 h-full">
                <ProblemSection detail={data} />
                <CodeEditorSection />
            </main>
        </div>
    );
}

export default SolvePage;
