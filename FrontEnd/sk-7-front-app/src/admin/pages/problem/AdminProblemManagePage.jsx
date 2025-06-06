import { useEffect, useState } from "react";
import { getProblems, deleteProblem } from "../../api/problemApi.js";
import Button from "../../../components/atoms/Button.jsx";
import AdminLayout from "../../layout/AdminLayout.jsx";
import dayjs from "dayjs";
import ProblemSearchBar from "./ProblemSearchBar.jsx";
import { useNavigate } from "react-router-dom"; // CategoryBar 경로 확인

const AdminProblemManagePage = () => {
    const [problems, setProblems] = useState([]);
    const [titleSearch, setTitleSearch] = useState(""); // CategoryBar의 title prop과 연결될 상태
    const navigate = useNavigate();
    // 검색 조건을 저장하는 상태 (CategoryBar와 연동)
    const [searchParams, setSearchParams] = useState({
        title: "",
        sort: "recent", // CategoryBar의 기본 정렬 값과 일치
        language: "",   // CategoryBar의 기본 언어 선택 값과 일치
    });

    const fetchProblems = async () => {
        try {
            const res = await getProblems({
                page: 0,
                size: 20,
                title: searchParams.title,
                sort: searchParams.sort,
                language: searchParams.language,
            });
            setProblems(res.data.data.content || []);
        } catch (err) {
            console.error("문제 목록 가져오기 실패:", err);
        }
    };

    // CategoryBar의 검색 버튼 클릭 시 호출될 함수
    const handleCategoryBarSearch = ({ title, sort, language }) => {
        setSearchParams({ title, sort, language });
        // 검색 파라미터가 변경되면 문제를 다시 불러옵니다.
        fetchProblems();
    };

    // CategoryBar의 초기화 버튼 클릭 시 호출될 함수
    const handleCategoryBarReset = () => {
        setTitleSearch(""); // 검색 입력 필드 초기화
        setSearchParams({
            title: "",
            sort: "recent",
            language: "",
        });
        // 검색 파라미터가 초기화되면 문제를 다시 불러옵니다.
        fetchProblems();
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
    const handleNavigateProblem= (id) => {
        navigate(`/admin/problem/${id}`)
    }
    useEffect(() => {
        fetchProblems();
    }, [searchParams]);
    return (
        <AdminLayout>
            <main className="p-6 overflow-auto">
                <div className="grid grid-cols-1 gap-6">
                    <div className="p-6">
                        <h2 className="text-2xl font-semibold mb-6">
                            문제 목록
                        </h2>
                        <div className="mb-6"> {/* CategoryBar를 위한 여백 추가 */}
                            <div className="searchBar">
                                <ProblemSearchBar
                                    title={titleSearch}
                                    setTitle={setTitleSearch}
                                    onSearch={handleCategoryBarSearch}
                                    onReset={handleCategoryBarReset}
                                />
                            </div>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="min-w-full bg-white shadow rounded-lg">
                                <thead className="bg-gray-100">
                                <tr>
                                    <th className="p-3 text-left">id</th>
                                    <th className="p-3 text-left">제목</th>
                                    <th className="p-3 text-left">레벨</th>
                                    <th className="p-3 text-left">정답률</th>
                                    <th className="p-3 text-left">생성일시</th>
                                    <th className="p-3 text-left">카테고리</th>
                                    <th className="p-3 text-center">작업</th>
                                </tr>
                                </thead>
                                <tbody>
                                {problems.map((problem) => (
                                    <tr
                                        key={problem.id}
                                        className="border-t hover:bg-gray-50"
                                    >
                                        <td className="p-3">
                                            {problem.id}
                                        </td>
                                        <td className="p-3">
                                            {problem.title}
                                        </td>
                                        <td className="p-3">
                                            {problem.level}
                                        </td>
                                        <td className="p-3">
                                            {(problem.passRate * 100).toFixed(2)}% {/* 소수점 2자리까지 표시 */}
                                        </td>
                                        <td className="p-3">
                                            {dayjs(problem.createdAt).format("YYYY-MM-DD HH:mm:ss")}
                                        </td>
                                        <td className="p-3">
                                            {problem.category?.code}
                                        </td>
                                        <td className="p-3 text-center">
                                            <Button
                                                className="mr-2"
                                                onClick={() =>handleNavigateProblem(problem.id)
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
                                            colSpan="7"
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