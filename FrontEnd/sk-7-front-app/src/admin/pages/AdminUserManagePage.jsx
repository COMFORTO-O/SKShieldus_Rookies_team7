import { useEffect, useState } from "react";
import { registerUser, deleteUser, getUserInfo } from "../api/userApi";
import Button from "../../components/atoms/button";
import Input from "../../components/atoms/Input";
import AdminLayout from "../layout/AdminLayout";

const AdminUserManagePage = () => {
    const [users, setUsers] = useState([]); // 여러 유저를 가정
    const [form, setForm] = useState({
        name: "",
        email: "",
        phone: "",
        password: "",
    });

    const fetchUser = async () => {
        try {
            const res = await getUserInfo();
            setUsers([res.data]);
        } catch (err) {
            console.error("유저 정보 가져오기 실패:", err);
        }
    };

    const handleDelete = async (email) => {
        if (!window.confirm("계정을 삭제하시겠습니까?")) return;
        try {
            await deleteUser();
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
                <div className="overflow-y-auto max-h-[500px]">
                    <h3 className="text-xl font-semibold mb-4">계정 목록</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        {users.length > 0 ? (
                            users.map((user, index) => (
                                <div
                                    key={index}
                                    className="bg-white p-6 rounded shadow border border-gray-300"
                                >
                                    <p>
                                        <strong>이름:</strong> {user.name}
                                    </p>
                                    <p>
                                        <strong>이메일:</strong> {user.email}
                                    </p>
                                    <p>
                                        <strong>전화번호:</strong> {user.phone}
                                    </p>
                                    <div className="mt-4">
                                        <Button
                                            variant="destructive"
                                            onClick={() =>
                                                handleDelete(user.email)
                                            }
                                        >
                                            계정 삭제
                                        </Button>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="text-gray-500">
                                사용자 정보가 없습니다.
                            </p>
                        )}
                    </div>
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminUserManagePage;
