import { useEffect, useState } from "react";
import { deleteUser, getUserList, registerUser } from "../../api/userApi.js";
import Button from "../../../components/atoms/Button.jsx";
import Input from "../../../components/atoms/Input.jsx";
import AdminLayout from "../../layout/AdminLayout.jsx";
import { useNavigate } from "react-router-dom";

const AdminUserManagePage = () => {
    const [users, setUsers] = useState([
        {
            id: "",
            name: "",
            email: "",
            phone: "",
            role: "",
            deleted: false
        }
    ]); // 여러 유저를 가정
    const [form, setForm] = useState({
        name: "",
        email: "",
        phone: "",
        role: "",
        password: ""
    });

    // navigate
    const navigate = useNavigate();

    const fetchUser = async () => {
        try {
            const res = await getUserList();
            // paging 처리, data.content.list
            setUsers(res.data.data.content);
            console.log(res.data.data.content);
        } catch (err) {
            console.error("유저 정보 가져오기 실패:", err);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("계정을 삭제하시겠습니까?")) return;
        try {
            console.log(id);
            await deleteUser(id);
            alert("계정이 삭제되었습니다.");
            fetchUser();
        } catch (err) {
            console.error("계정 삭제 실패:", err);
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await registerUser(form);
            alert("계정이 생성되었습니다.");
            setForm({ name: "", email: "", phone: "", password: "" });
            fetchUser();
        } catch (err) {
            console.error("계정 생성 실패:", err);
        }
    };

    const handleNavigateUserDetail= (id) => {
        navigate(`/admin/user/${id}`)
    }

    useEffect(() => {
        fetchUser();
    }, []);


    return (
        <AdminLayout>
            <main className="p-6 overflow-auto">
                <h2 className="text-2xl font-semibold mb-6">계정 관리</h2>

                {/* 계정 생성 폼 */}
                <form
                    onSubmit={handleRegister}
                    className="bg-white p-6 rounded shadow mb-8 max-w-2xl"
                >
                    <h3 className="text-xl font-semibold mb-4">계정 생성</h3>
                    <div className=" sm:grid-cols-2 gap-4">
                        <Input
                            className="border p-2 rounded"
                            placeholder="이름"
                            value={form.name}
                            onChange={(e) =>
                                setForm({ ...form, name: e.target.value })
                            }
                            required
                        />
                        <Input
                            className="border p-2 rounded"
                            placeholder="이메일"
                            type="email"
                            value={form.email}
                            onChange={(e) =>
                                setForm({ ...form, email: e.target.value })
                            }
                            required
                        />
                        <Input
                            className="border p-2 rounded"
                            placeholder="전화번호"
                            value={form.phone}
                            onChange={(e) =>
                                setForm({ ...form, phone: e.target.value })
                            }
                            required
                        />
                        <Input
                            className="border p-2 rounded"
                            placeholder="비밀번호"
                            type="password"
                            value={form.password}
                            onChange={(e) =>
                                setForm({ ...form, password: e.target.value })
                            }
                            required
                        />
                    </div>
                    <div className="mt-4">
                        <Button type="submit">계정 생성</Button>
                    </div>
                </form>

                {/* 사용자 목록 */}
                <div className="overflow-x-auto max-h-[500px] overflow-y-auto rounded border border-gray-300">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-100 sticky top-0 z-10">
                        <tr>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">이름</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">권한</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">이메일</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">전화번호</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">삭제여부</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">관리</th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {users.length > 0 ? (
                            users.map((user) => (
                                <tr key={user.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 text-sm text-gray-800">{user.name}</td>
                                    <td className="px-6 py-4 text-sm text-gray-800">{user.role}</td>
                                    <td className="px-6 py-4 text-sm text-gray-800">{user.email}</td>
                                    <td className="px-6 py-4 text-sm text-gray-800">{user.phone}</td>
                                    <td className="px-6 py-4 text-sm text-gray-800">{user.deleted ? "탈퇴" : "미탈퇴"}</td>
                                    <td className="px-6 py-4">
                                        <Button
                                            variant="destructive"
                                            onClick={() => handleNavigateUserDetail(user.id)}
                                        >
                                            상세 보기
                                        </Button>
                                        {user.deleted ?   (<></>) :
                                            (<Button
                                                variant="destructive"
                                                onClick={() => handleDelete(user.id)}
                                            >
                                                계정 삭제
                                            </Button>)}
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={4} className="px-6 py-4 text-center text-gray-500">
                                    사용자 정보가 없습니다.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminUserManagePage;
