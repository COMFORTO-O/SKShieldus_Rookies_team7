import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getProblemDetail, updateProblem } from "../../api/problemApi.js"; // `updateProblem` API 함수가 이 DTO를 처리하도록 백엔드에 맞춰야 합니다.
import AdminLayout from "../../layout/AdminLayout.jsx";
import Button from "../../../components/atoms/Button.jsx";
// axiosInstance는 `updateProblem` 내부에서 사용될 것이므로 여기서는 직접 임포트하지 않습니다.

const AdminProblemEditPage = () => {
    const { id } = useParams(); // URL에서 문제 ID 가져오기
    const navigate = useNavigate();

    const [problem, setProblem] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // DTO 구조에 맞춰 폼 데이터 상태 정의
    const [formData, setFormData] = useState({
        problemId: null, // 초기에는 null, 불러온 문제 ID로 설정
        title: "",
        detail: "",
        category: "", // categoryCode 대신 category 필드 사용
        level: "",
        testCase: [], // List<TestCaseDto>
    });

    useEffect(() => {
        const fetchProblemData = async () => {
            try {
                setLoading(true);
                const res = await getProblemDetail(id);
                const problemData = res.data.data;
                setProblem(problemData); // 원본 문제 데이터 저장

                // DTO에 맞춰 formData 설정
                setFormData({
                    problemId: problemData.id,
                    title: problemData.title,
                    detail: problemData.detail,
                    category: problemData.category?.code || "", // category.code를 category 필드로
                    level: problemData.level,
                    // testCaseId를 포함하고 isTestCase 필드를 추가 (기본값 true로 설정)
                    testCase:
                        problemData.testCase.map((tc) => ({
                            testCaseId: tc.id,
                            input: tc.input,
                            output: tc.output,
                            isTestCase: true, // 기존 테스트 케이스는 true로 설정
                        })) || [],
                });
            } catch (err) {
                console.error("문제 정보 가져오기 실패:", err);
                setError("문제 정보를 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchProblemData();
        }
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleTestCaseChange = (index, e) => {
        const { name, value, type, checked } = e.target;
        const newTestCases = [...formData.testCase];
        newTestCases[index] = {
            ...newTestCases[index],
            [name]: type === "checkbox" ? checked : value, // 체크박스 처리
        };
        setFormData((prev) => ({ ...prev, testCase: newTestCases }));
    };

    const addTestCase = () => {
        setFormData((prev) => ({
            ...prev,
            testCase: [
                ...prev.testCase,
                { testCaseId: null, input: "", output: "", isTestCase: true }, // 새 테스트 케이스는 ID null, isTestCase 기본값 true
            ],
        }));
    };

    const removeTestCase = (index) => {
        setFormData((prev) => ({
            ...prev,
            testCase: prev.testCase.filter((_, i) => i !== index),
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // DTO 구조에 맞춰 최종 데이터 구성
            const dataToSubmit = {
                problemId: formData.problemId,
                title: formData.title,
                detail: formData.detail,
                category: formData.category,
                level: parseInt(formData.level), // Integer 타입이므로 parseInt
                testCase: formData.testCase.map((tc) => ({
                    testCaseId: tc.testCaseId, // 기존 ID는 그대로, 새로 추가된 것은 null
                    input: tc.input,
                    output: tc.output,
                    isTestCase: tc.isTestCase,
                })),
            };

            await updateProblem(id, dataToSubmit); // `id`는 URL에서, `dataToSubmit`은 DTO 형식으로 전달
            alert("문제가 성공적으로 수정되었습니다.");
            navigate(`/admin/problem/${id}`); // 상세 페이지로 리다이렉트
        } catch (err) {
            console.error("문제 수정 실패:", err.response?.data || err.message);
            alert(
                "문제 수정에 실패했습니다: " +
                    (err.response?.data?.message || err.message)
            );
        }
    };

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
                    <Button
                        onClick={() => navigate("/admin/problem")}
                        className="mt-4"
                    >
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
                    <h2 className="text-2xl font-semibold mb-6">
                        문제 수정 ({problem.id})
                    </h2>

                    <form onSubmit={handleSubmit}>
                        {/* 문제 정보 입력 필드 */}
                        <div className="mb-4">
                            <label
                                htmlFor="title"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                제목:
                            </label>
                            <input
                                type="text"
                                id="title"
                                name="title"
                                value={formData.title}
                                onChange={handleChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label
                                htmlFor="detail"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                문제 내용:
                            </label>
                            <textarea
                                id="detail"
                                name="detail"
                                value={formData.detail}
                                onChange={handleChange}
                                rows="8"
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            ></textarea>
                        </div>
                        <div className="mb-4">
                            <label
                                htmlFor="level"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                난이도 (1-10):
                            </label>
                            <input
                                type="number"
                                id="level"
                                name="level"
                                value={formData.level}
                                onChange={handleChange}
                                min="1"
                                max="10"
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <div className="mb-6">
                            <label
                                htmlFor="category"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                카테고리:
                            </label>
                            {/* 실제 카테고리 목록을 드롭다운으로 제공하는 것이 더 좋습니다. */}
                            <input
                                type="text"
                                id="category"
                                name="category"
                                value={formData.category}
                                onChange={handleChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>

                        {/* 테스트 케이스 수정 섹션 */}
                        <h3 className="text-xl font-semibold mb-4">
                            테스트 케이스
                        </h3>
                        {formData.testCase.map((test, index) => (
                            <div
                                key={test.testCaseId || `new-${index}`}
                                className="flex flex-wrap items-center mb-4 p-4 border rounded-lg bg-gray-50"
                            >
                                <div className="w-full md:w-1/2 md:pr-2 mb-2 md:mb-0">
                                    <label
                                        htmlFor={`input-${index}`}
                                        className="block text-gray-700 text-sm font-bold mb-1"
                                    >
                                        입력 {index + 1}:
                                    </label>
                                    <textarea
                                        id={`input-${index}`}
                                        name="input"
                                        value={test.input}
                                        onChange={(e) =>
                                            handleTestCaseChange(index, e)
                                        }
                                        rows="2"
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                        required
                                    ></textarea>
                                </div>
                                <div className="w-full md:w-1/2 md:pl-2 mb-2 md:mb-0">
                                    <label
                                        htmlFor={`output-${index}`}
                                        className="block text-gray-700 text-sm font-bold mb-1"
                                    >
                                        출력 {index + 1}:
                                    </label>
                                    <textarea
                                        id={`output-${index}`}
                                        name="output"
                                        value={test.output}
                                        onChange={(e) =>
                                            handleTestCaseChange(index, e)
                                        }
                                        rows="2"
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                        required
                                    ></textarea>
                                </div>
                                <div className="w-full mt-2">
                                    <label className="inline-flex items-center">
                                        <input
                                            type="checkbox"
                                            name="isTestCase"
                                            checked={test.isTestCase}
                                            onChange={(e) =>
                                                handleTestCaseChange(index, e)
                                            }
                                            className="form-checkbox h-5 w-5 text-blue-600"
                                        />
                                        <span className="ml-2 text-gray-700 text-sm">
                                            테스트 케이스 사용
                                        </span>
                                    </label>
                                </div>
                                <div className="w-full text-right mt-2">
                                    <Button
                                        type="button"
                                        onClick={() => removeTestCase(index)}
                                        variant="destructive"
                                        className="py-1 px-3 text-sm"
                                    >
                                        삭제
                                    </Button>
                                </div>
                            </div>
                        ))}
                        <Button
                            type="button"
                            onClick={addTestCase}
                            className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline mb-6"
                        >
                            테스트 케이스 추가
                        </Button>

                        {/* 버튼들 */}
                        <div className="flex justify-end space-x-3 mt-6">
                            <Button
                                type="submit"
                                className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                            >
                                수정 완료
                            </Button>
                            <Button
                                type="button"
                                onClick={() => navigate(`/admin/problem/${id}`)}
                                variant="secondary"
                            >
                                취소
                            </Button>
                        </div>
                    </form>
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminProblemEditPage;
