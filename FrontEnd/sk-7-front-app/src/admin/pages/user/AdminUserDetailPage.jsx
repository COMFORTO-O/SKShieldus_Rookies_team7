import { useEffect, useState } from "react";
import { deleteUser, getUserInfo, updateUser } from "../../api/userApi.js";
import Button from "../../../components/atoms/Button.jsx";
import Input from "../../../components/atoms/Input.jsx";
import AdminLayout from "../../layout/AdminLayout.jsx";
import { useParams } from "react-router-dom";
import dayjs from 'dayjs';
import { encryptPassword } from "../../../encrypt/encryptPassword.js";
const AdminUserDetailPage = () => {
    const { id } = useParams();
    const userId = Number(id);
    const [user, setUser] = useState({
        member: {
            id: 0,
            name: "",
            email: "",
            phone: "",
            role: "",
            deleted: false,
        },
        submissions: {}
    }); // 여러 유저를 가정
    const [form, setForm] = useState({
        name: "",
        email: "",
        phone: "",
        role: "",
        password: "",
        deleted: false,
    });

    const fetchUserInfo = async () => {
        try {
            const res = await getUserInfo ({id: userId, page:0, size:100 });
            // paging 처리, data.content.list
            setUser(res.data.data);
            setForm(res.data.data.member)
            console.log(res.data.data);
        } catch (err) {
            console.error("유저 정보 가져오기 실패:", err);
        }
    };

    const handleDelete = async () => {
        if (!window.confirm("계정을 삭제하시겠습니까?")) return;
        try {
            await deleteUser(userId);
            alert("계정이 삭제되었습니다.");
            await fetchUserInfo();
        } catch (err) {
            console.error("계정 삭제 실패:", err);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            let updateData = form;
            if(updateData.password != null && updateData.password.length > 0){
                updateData.password =  encryptPassword(updateData.password);
            }
            await updateUser(form);

            alert("업데이트되었습니다.");
            await fetchUserInfo();
        } catch (err) {
            console.error("계정 생성 실패:", err);
        }
    };

    useEffect(() => {
        fetchUserInfo();
    }, []);


    return (
        <AdminLayout>
            <main className="p-6 overflow-auto">
                <h2 className="text-2xl font-semibold mb-6">계정 관리</h2>
                {/* 계정 생성 폼 */}
                <form
                    onSubmit={handleUpdate}
                    className="bg-white p-6 rounded shadow mb-8 max-w-2xl"
                >
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
                            placeholder="권한"
                            type="role"
                            value={form.role}
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
                        />
                        <Input readOnly={true} value={form.deleted? "탈퇴" : "미탈퇴" }/>
                    </div>
                    <div className="mt-4">
                        <Button type="submit">계정 업데이트</Button>
                        <Button onClick={handleDelete}>계정 삭제</Button>
                    </div>
                </form>

                {/* 사용자 목록 */}
                <div className="overflow-x-auto max-h-[500px] overflow-y-auto rounded border border-gray-300">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-100 sticky top-0 z-10">
                        <tr>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">문제</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">성공여부</th>
                            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">시작일시</th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {user.submissions.content?.length > 0 ? (
                            user.submissions.content.map((submission) => (
                                <tr key={submission.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 text-sm text-gray-800">{submission.problemTitle}</td>
                                    <td className="px-6 py-4 text-sm text-gray-800">{submission.pass ? "성공": "실패"}</td>
                                    <td className="px-6 py-4 text-sm text-gray-800">{dayjs(submission.createdAt).format("YYYY-MM-DD HH:mm:ss")}</td>
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

export default AdminUserDetailPage;
