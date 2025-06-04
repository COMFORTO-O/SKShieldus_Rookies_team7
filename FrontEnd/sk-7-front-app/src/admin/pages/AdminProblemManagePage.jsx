import { useEffect, useState } from "react";
import { getProblems, deleteProblem } from "../api/problemApi";
import Button from "../../components/atoms/button";
import AdminLayout from "../layout/AdminLayout";

const AdminProblemManagePage = () => {
    const [problems, setProblems] = useState([]);

    const fetchProblems = async () => {
        try {
            const res = await getProblems({ page: 0, size: 20 });
            setProblems(res.data.content || []);
        } catch (err) {
            console.error("문제 목록 가져오기 실패:", err);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm(`${id}번 문제를 삭제할까요?`)) return;
        try {
            await deleteProblem(id);
            alert("삭제 완료");
            fetchProblems();
        } catch (err) {
            console.error("문제 삭제 실패:", err);
        }
    };

    useEffect(() => {
        fetchProblems();
    }, []);

    return (
        <AdminLayout>
            <main className="p-6 overflow-auto">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div className="p-6">
                        <h2 className="text-2xl font-semibold mb-6">
                            문제 목록
                        </h2>
                        <div className="overflow-x-auto">
                            <table className="min-w-full bg-white shadow rounded-lg">
                                <thead className="bg-gray-100">
                                    <tr>
                                        <th className="p-3 text-left">제목</th>
                                        <th className="p-3 text-left">레벨</th>
                                        <th className="p-3 text-left">
                                            카테고리
                                        </th>
                                        <th className="p-3 text-center">
                                            작업
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {problems.map((problem) => (
                                        <tr
                                            key={problem.id}
                                            className="border-t hover:bg-gray-50"
                                        >
                                            <td className="p-3">
                                                {problem.title}
                                            </td>
                                            <td className="p-3">
                                                {problem.level}
                                            </td>
                                            <td className="p-3">
                                                {problem.category?.code}
                                            </td>
                                            <td className="p-3 text-center">
                                                <Button
                                                    className="mr-2"
                                                    onClick={() =>
                                                        alert(
                                                            "상세보기 기능 예정"
                                                        )
                                                    }
                                                >
                                                    보기
                                                </Button>
                                                <Button
                                                    variant="destructive"
                                                    onClick={() =>
                                                        handleDelete(problem.id)
                                                    }
                                                >
                                                    삭제
                                                </Button>
                                            </td>
                                        </tr>
                                    ))}
                                    {problems.length === 0 && (
                                        <tr>
                                            <td
                                                colSpan="4"
                                                className="p-3 text-center text-gray-500"
                                            >
                                                등록된 문제가 없습니다.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminProblemManagePage;
