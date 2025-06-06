import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProblemDetail } from '../../api/problemApi.js';
import AdminLayout from '../../layout/AdminLayout.jsx';
import Button from '../../../components/atoms/Button.jsx';
import dayjs from 'dayjs';

const AdminProblemDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [problem, setProblem] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProblemDetail = async () => {
            try {
                setLoading(true);
                const res = await getProblemDetail(id);
                // JSON 응답의 'data' 필드 안에 문제 상세 정보가 있으므로 res.data.data로 접근
                setProblem(res.data.data);
            } catch (err) {
                console.error("문제 상세 정보 가져오기 실패:", err);
                setError("문제 정보를 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchProblemDetail();
        }
    }, [id]);

    if (loading) {
        return (
            <AdminLayout>
                <main className="p-6 text-center">
                    <p>문제 정보를 불러오는 중입니다...</p>
                </main>
            </AdminLayout>
        );
    }

    if (error) {
        return (
            <AdminLayout>
                <main className="p-6 text-center text-red-600">
                    <p>{error}</p>
                    <Button onClick={() => navigate(-1)} className="mt-4">
                        이전 페이지로 돌아가기
                    </Button>
                </main>
            </AdminLayout>
        );
    }

    if (!problem) {
        return (
            <AdminLayout>
                <main className="p-6 text-center">
                    <p>문제를 찾을 수 없습니다.</p>
                    <Button onClick={() => navigate('/admin/problem')} className="mt-4">
                        문제 목록으로 돌아가기
                    </Button>
                </main>
            </AdminLayout>
        );
    }

    return (
        <AdminLayout>
            <main className="p-6 overflow-auto">
                <div className="bg-white shadow rounded-lg p-6">
                    <h2 className="text-2xl font-semibold mb-6">문제 상세 정보</h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-lg">
                        <div className="flex flex-col">
                            <span className="font-medium text-gray-600">제목:</span>
                            <span className="mt-1 p-2 border rounded-md bg-gray-50">{problem.title}</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="font-medium text-gray-600">레벨:</span>
                            <span className="mt-1 p-2 border rounded-md bg-gray-50">{problem.level}</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="font-medium text-gray-600">작성자:</span>
                            <span className="mt-1 p-2 border rounded-md bg-gray-50">{problem.memberName}</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="font-medium text-gray-600">카테고리:</span>
                            {/* category 객체 안에 code와 description이 있으므로, code를 사용 */}
                            <span className="mt-1 p-2 border rounded-md bg-gray-50">{problem.category?.code || 'N/A'}</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="font-medium text-gray-600">생성일시:</span>
                            <span className="mt-1 p-2 border rounded-md bg-gray-50">{dayjs(problem.createdAt).format("YYYY-MM-DD HH:mm:ss")}</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="font-medium text-gray-600">수정일시:</span>
                            <span className="mt-1 p-2 border rounded-md bg-gray-50">{problem.updatedAt ? dayjs(problem.updatedAt).format("YYYY-MM-DD HH:mm:ss") : 'N/A'}</span>
                        </div>
                    </div>

                    <div className="mt-6">
                        <h3 className="text-xl font-semibold mb-3">문제 내용:</h3>
                        {/* 'detail' 필드가 문제 내용이므로 이를 사용 */}
                        <div className="p-4 border rounded-md bg-gray-50 min-h-[150px] overflow-auto whitespace-pre-wrap">
                            {problem.detail}
                        </div>
                    </div>

                    {/* 테스트 케이스 목록 추가 */}
                    {problem.testCase && problem.testCase.length > 0 && (
                        <div className="mt-6">
                            <h3 className="text-xl font-semibold mb-3">테스트 케이스:</h3>
                            <div className="border rounded-md overflow-hidden">
                                <table className="min-w-full bg-white">
                                    <thead className="bg-gray-100">
                                    <tr>
                                        <th className="p-3 text-left font-medium text-gray-700">ID</th>
                                        <th className="p-3 text-left font-medium text-gray-700">입력 (Input)</th>
                                        <th className="p-3 text-left font-medium text-gray-700">출력 (Output)</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {problem.testCase.map((test, index) => (
                                        <tr key={test.id || index} className="border-t hover:bg-gray-50">
                                            <td className="p-3">{test.id}</td>
                                            <td className="p-3 font-mono text-sm break-all">{test.input}</td>
                                            <td className="p-3 font-mono text-sm break-all">{test.output}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}


                    <div className="mt-6 flex justify-end space-x-3">
                        <Button
                            onClick={() => navigate(`/admin/problem/${id}/edit`)}
                            variant="secondary"
                        >
                            수정
                        </Button>
                        <Button
                            onClick={() => navigate(`/admin/problem`)}
                        >
                            목록으로
                        </Button>
                    </div>
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminProblemDetailPage;