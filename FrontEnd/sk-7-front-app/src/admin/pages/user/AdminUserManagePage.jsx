import { useEffect, useState } from "react";
import { deleteUser, getUserList, registerUser } from "../../api/userApi.js";
import Button from "../../../components/atoms/Button.jsx";
import Input from "../../../components/atoms/Input.jsx";
import AdminLayout from "../../layout/AdminLayout.jsx";
import { useNavigate } from "react-router-dom";

const AdminUserManagePage = () => {
    const [users, setUsers] = useState([]); // 초기값을 빈 배열로 설정하는 것이 좋습니다.
    const [form, setForm] = useState({
        name: "",
        email: "",
        phone: "",
        role: "", // 역할 필드 추가
        password: "",
    });

    const navigate = useNavigate();

    const fetchUser = async () => {
        try {
            const res = await getUserList();
            setUsers(res.data.data.content || []); // 데이터가 없을 경우 빈 배열
        } catch (err) {
            console.error("유저 정보 가져오기 실패:", err);
            setUsers([]); // 에러 발생 시 사용자 목록 초기화
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("계정을 삭제하시겠습니까?")) return;
        try {
            await deleteUser(id);
            alert("계정이 삭제되었습니다.");
            fetchUser();
        } catch (err) {
            console.error("계정 삭제 실패:", err);
            alert("계정 삭제에 실패했습니다.");
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            // role 필드가 폼에 없으므로, 기본값을 설정하거나 폼에 추가해야 합니다.
            // 여기서는 임시로 'USER' 역할을 부여하는 예시입니다. 실제로는 UI에서 선택하게 할 수 있습니다.
            const userToRegister = { ...form, role: form.role || "USER" }; // role이 폼에 없으면 'USER' 기본값
            await registerUser(userToRegister);
            alert("계정이 성공적으로 생성되었습니다.");
            setForm({ name: "", email: "", phone: "", role: "", password: "" }); // role 필드도 초기화
            fetchUser();
        } catch (err) {
            console.error("계정 생성 실패:", err);
            alert(
                "계정 생성에 실패했습니다: " +
                    (err.response?.data?.message || err.message)
            );
        }
    };

    const handleNavigateUserDetail = (id) => {
        navigate(`/admin/user/${id}`);
    };

    useEffect(() => {
        fetchUser();
    }, []);

    return (
        <AdminLayout>
            <main className="p-6 md:p-8 lg:p-10 bg-gray-100 min-h-screen">
                {" "}
                {/* 배경색 및 패딩 조정 */}
                <h2 className="text-3xl font-bold text-gray-800 mb-8">
                    관리자 계정 관리
                </h2>{" "}
                {/* 제목 크기 및 색상 */}
                {/* 계정 생성 폼 카드 */}
                <section className="bg-white p-6 rounded-xl shadow-lg mb-10 border border-gray-200">
                    {" "}
                    {/* 그림자 및 둥근 모서리, 테두리 */}
                    <h3 className="text-2xl font-semibold text-gray-700 mb-6 border-b pb-3">
                        새 계정 생성
                    </h3>{" "}
                    {/* 구분선 */}
                    <form
                        onSubmit={handleRegister}
                        className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4"
                    >
                        {" "}
                        {/* 2열 그리드, 간격 조정 */}
                        <Input
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                            placeholder="이름"
                            value={form.name}
                            onChange={(e) =>
                                setForm({ ...form, name: e.target.value })
                            }
                            required
                        />
                        <Input
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                            placeholder="이메일"
                            type="email"
                            value={form.email}
                            onChange={(e) =>
                                setForm({ ...form, email: e.target.value })
                            }
                            required
                        />
                        <Input
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                            placeholder="전화번호 (예: 010-1234-5678)"
                            value={form.phone}
                            onChange={(e) =>
                                setForm({ ...form, phone: e.target.value })
                            }
                            required
                        />
                        {/* 역할(role) 선택 드롭다운 추가 */}
                        <select
                            className="w-full p-3 border border-gray-300 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                            value={form.role}
                            onChange={(e) =>
                                setForm({ ...form, role: e.target.value })
                            }
                            required
                        >
                            <option value="" disabled>
                                역할 선택
                            </option>
                            <option value="USER">일반 사용자</option>
                            <option value="ADMIN">관리자</option>
                            {/* 추가 역할이 있다면 여기에 추가 */}
                        </select>
                        <div className="md:col-span-2">
                            {" "}
                            {/* 비밀번호 필드는 전체 너비 사용 */}
                            <Input
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                                placeholder="비밀번호"
                                type="password"
                                value={form.password}
                                onChange={(e) =>
                                    setForm({
                                        ...form,
                                        password: e.target.value,
                                    })
                                }
                                required
                            />
                        </div>
                        <div className="md:col-span-2 flex justify-end mt-4">
                            {" "}
                            {/* 버튼을 오른쪽으로 정렬 */}
                            <Button
                                type="submit"
                                className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-200 ease-in-out transform hover:scale-105"
                            >
                                새 계정 생성
                            </Button>
                        </div>
                    </form>
                </section>
                {/* 사용자 목록 섹션 */}
                <section className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                    {" "}
                    {/* 그림자 및 둥근 모서리, 테두리 */}
                    <h3 className="text-2xl font-semibold text-gray-700 mb-6 border-b pb-3">
                        사용자 목록
                    </h3>
                    <div className="overflow-x-auto max-h-[600px] overflow-y-auto rounded-lg border border-gray-300">
                        {" "}
                        {/* 테이블 전체에 둥근 모서리 및 테두리 */}
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-100 sticky top-0 z-10 shadow-sm">
                                {" "}
                                {/* 헤더 그림자 추가 */}
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        이름
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        권한
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        이메일
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        전화번호
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        상태
                                    </th>
                                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        작업
                                    </th>{" "}
                                    {/* 중앙 정렬 */}
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {users.length > 0 ? (
                                    users.map((user) => (
                                        <tr
                                            key={user.id}
                                            className="hover:bg-gray-50 transition duration-150 ease-in-out"
                                        >
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                {user.name}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                                <span
                                                    className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                                        user.role === "ADMIN"
                                                            ? "bg-purple-100 text-purple-800"
                                                            : "bg-blue-100 text-blue-800"
                                                    }`}
                                                >
                                                    {user.role}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                                {user.email}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                                {user.phone}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                                <span
                                                    className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                                        user.deleted
                                                            ? "bg-red-100 text-red-800"
                                                            : "bg-green-100 text-green-800"
                                                    }`}
                                                >
                                                    {user.deleted
                                                        ? "탈퇴"
                                                        : "활성"}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                                                {" "}
                                                {/* 중앙 정렬 */}
                                                <div className="flex justify-center space-x-2">
                                                    {" "}
                                                    {/* 버튼 간격 */}
                                                    <Button
                                                        variant="primary" // primary 버튼으로 변경
                                                        onClick={() =>
                                                            handleNavigateUserDetail(
                                                                user.id
                                                            )
                                                        }
                                                        className="py-2 px-4 text-xs font-medium rounded-md shadow-sm hover:shadow-md transition duration-150 ease-in-out"
                                                    >
                                                        상세 보기
                                                    </Button>
                                                    {!user.deleted && ( // 탈퇴하지 않은 사용자만 삭제 버튼 표시
                                                        <Button
                                                            variant="destructive"
                                                            onClick={() =>
                                                                handleDelete(
                                                                    user.id
                                                                )
                                                            }
                                                            className="py-2 px-4 text-xs font-medium rounded-md shadow-sm hover:shadow-md transition duration-150 ease-in-out"
                                                        >
                                                            계정 삭제
                                                        </Button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td
                                            colSpan={6}
                                            className="px-6 py-4 text-center text-gray-500"
                                        >
                                            {" "}
                                            {/* colSpan 조정 */}
                                            등록된 사용자 정보가 없습니다.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </AdminLayout>
    );
};

export default AdminUserManagePage;
