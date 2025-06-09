import { useEffect, useState } from "react";
import { deleteUser, getUserInfo, updateUser } from "../../api/userApi.js";
import Button from "../../../components/atoms/Button.jsx";
import Input from "../../../components/atoms/Input.jsx";
import AdminLayout from "../../layout/AdminLayout.jsx";
import { useParams, useNavigate } from "react-router-dom";
import dayjs from "dayjs";
import { encryptPassword } from "../../../encrypt/encryptPassword.js";

const AdminUserDetailPage = () => {
    const { id: paramId } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [form, setForm] = useState({
        name: "",
        email: "",
        phone: "",
        role: "",
        password: "",
        deleted: false,
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchUserInfo = async (currentId) => {
        if (isNaN(currentId || currentId <= 0)) {
            setError("유효하지 않은 사용자 ID입니다.");
            setLoading(false);
            setUser(null);
            return;
        }

        try {
            setLoading(true);

            const res = await getUserInfo({
                id: currentId,
                page: 0,
                size: 100,
            });
            console.log(res);
            setUser(res.data.data);
            setForm((prevForm) => ({
                ...prevForm,
                name: res.data.data.member.name,
                email: res.data.data.member.email,
                phone: res.data.data.member.phone,
                role: res.data.data.member.role,
                deleted: res.data.data.member.deleted,
                password: "",
            }));
            setError(null);
        } catch (err) {
            console.error("유저 정보 가져오기 실패:", err);
            setError("사용자 정보를 불러오는 데 실패했습니다.");
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        const currentUserId = Number(paramId);
        if (
            !window.confirm(
                "정말로 이 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
            )
        )
            return;
        try {
            await deleteUser(currentUserId);
            alert("계정이 성공적으로 삭제되었습니다.");
            navigate("/admin/user");
        } catch (err) {
            console.error("계정 삭제 실패:", err);
            alert(
                "계정 삭제에 실패했습니다: " +
                    (err.response?.data?.message || err.message)
            );
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            let updateData = { ...form };

            if (updateData.password && updateData.password.length > 0) {
                updateData.password = encryptPassword(updateData.password);
            } else {
                delete updateData.password;
            }

            await updateUser(updateData);
            alert("계정 정보가 성공적으로 업데이트되었습니다.");
            await fetchUserInfo();
        } catch (err) {
            console.error(
                "계정 업데이트 실패:",
                err.response?.data || err.message
            );
            alert(
                "계정 업데이트에 실패했습니다: " +
                    (err.response?.data?.message || err.message)
            );
        }
    };

    useEffect(() => {
        const numericId = Number(paramId);
        if (numericId) {
            fetchUserInfo(numericId);
        }
    }, [paramId]);

    if (loading) {
        return (
            <AdminLayout>
                <main className="p-6 text-center">
                    <p className="text-gray-700 text-lg">
                        사용자 정보를 불러오는 중입니다...
                    </p>
                </main>
            </AdminLayout>
        );
    }

    if (error) {
        return (
            <AdminLayout>
                <main className="p-6 text-center">
                    <p className="text-red-600 text-lg mb-4">{error}</p>
                    <Button
                        onClick={() => navigate("/admin/user")}
                        variant="secondary"
                    >
                        사용자 목록으로 돌아가기
                    </Button>
                </main>
            </AdminLayout>
        );
    }

    if (!user) {
        return (
            <AdminLayout>
                <main className="p-6 text-center">
                    <p className="text-gray-700 text-lg mb-4">
                        사용자를 찾을 수 없습니다.
                    </p>
                    <Button
                        onClick={() => navigate("/admin/user")}
                        variant="secondary"
                    >
                        사용자 목록으로 돌아가기
                    </Button>
                </main>
            </AdminLayout>
        );
    }

    return (
        <AdminLayout>
            <main className="p-6 md:p-8 lg:p-10 bg-gray-100 min-h-screen">
                <h2 className="text-3xl font-bold text-gray-800 mb-8">
                    사용자 상세 정보 및 관리
                </h2>

                {/* 사용자 정보 및 수정 폼 섹션 */}
                <section className="bg-white p-6 rounded-xl shadow-lg mb-10 border border-gray-200">
                    <h3 className="text-2xl font-semibold text-gray-700 mb-6 border-b pb-3">
                        {user.member.name} 님의 정보
                    </h3>
                    <form
                        onSubmit={handleUpdate}
                        className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4"
                    >
                        <div className="md:col-span-2">
                            <label className="block text-gray-700 text-sm font-bold mb-2">
                                회원 ID:
                            </label>
                            <Input
                                readOnly={true}
                                value={user.member.id}
                                className="w-full p-3 bg-gray-100 border border-gray-300 rounded-lg"
                            />
                        </div>
                        <div>
                            <label
                                htmlFor="name"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                이름:
                            </label>
                            <Input
                                id="name"
                                name="name"
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                                placeholder="이름"
                                value={form.name}
                                onChange={(e) =>
                                    setForm({ ...form, name: e.target.value })
                                }
                                required
                            />
                        </div>
                        <div>
                            <label
                                htmlFor="email"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                이메일:
                            </label>
                            <Input
                                id="email"
                                name="email"
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                                placeholder="이메일"
                                type="email"
                                value={form.email}
                                onChange={(e) =>
                                    setForm({ ...form, email: e.target.value })
                                }
                                required
                                readOnly
                            />
                        </div>
                        <div>
                            <label
                                htmlFor="phone"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                전화번호:
                            </label>
                            <Input
                                id="phone"
                                name="phone"
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                                placeholder="전화번호"
                                value={form.phone}
                                onChange={(e) =>
                                    setForm({ ...form, phone: e.target.value })
                                }
                                required
                            />
                        </div>
                        <div>
                            <label
                                htmlFor="role"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                권한:
                            </label>
                            <select
                                id="role"
                                name="role"
                                className="w-full p-3 border border-gray-300 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                                value={form.role}
                                onChange={(e) =>
                                    setForm({ ...form, role: e.target.value })
                                }
                                required
                            >
                                <option value="USER">일반 사용자</option>
                                <option value="ADMIN">관리자</option>
                            </select>
                        </div>
                        <div className="md:col-span-2">
                            <label
                                htmlFor="password"
                                className="block text-gray-700 text-sm font-bold mb-2"
                            >
                                새 비밀번호 (변경 시에만 입력):
                            </label>
                            <Input
                                id="password"
                                name="password"
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                                placeholder="새 비밀번호 (비밀번호 변경 시에만 입력)"
                                type="password"
                                value={form.password}
                                onChange={(e) =>
                                    setForm({
                                        ...form,
                                        password: e.target.value,
                                    })
                                }
                            />
                            <p className="text-sm text-gray-500 mt-1">
                                비밀번호를 입력하지 않으면 변경되지 않습니다.
                            </p>
                        </div>
                        <div className="md:col-span-2">
                            <label className="block text-gray-700 text-sm font-bold mb-2">
                                계정 상태:
                            </label>
                            <Input
                                readOnly={true}
                                value={form.deleted ? "탈퇴 계정" : "활성 계정"}
                                className="w-full p-3 bg-gray-100 border border-gray-300 rounded-lg"
                            />
                        </div>
                        <div className="md:col-span-2 flex justify-end space-x-3 mt-6">
                            <Button
                                type="submit"
                                className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-200 ease-in-out transform hover:scale-105"
                            >
                                정보 업데이트
                            </Button>
                            {!user.member.deleted && (
                                <Button
                                    type="button"
                                    onClick={handleDelete}
                                    variant="destructive"
                                    className="py-3 px-6 rounded-lg shadow-md transition duration-200 ease-in-out transform hover:scale-105"
                                >
                                    계정 삭제
                                </Button>
                            )}
                            <Button
                                type="button"
                                onClick={() => navigate("/admin/user")}
                                variant="secondary"
                                className="py-3 px-6 rounded-lg shadow-md transition duration-200 ease-in-out transform hover:scale-105"
                            >
                                목록으로
                            </Button>
                        </div>
                    </form>
                </section>

                {/* 제출 기록 섹션 */}
                <section className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                    <h3 className="text-2xl font-semibold text-gray-700 mb-6 border-b pb-3">
                        제출 기록
                    </h3>
                    <div className="overflow-x-auto max-h-[500px] overflow-y-auto rounded-lg border border-gray-300">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-100 sticky top-0 z-10 shadow-sm">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        문제 제목
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        성공 여부
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                        제출 일시
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {user.submissions.content &&
                                user.submissions.content.length > 0 ? (
                                    user.submissions.content.map(
                                        (submission) => (
                                            <tr
                                                key={submission.id}
                                                className="hover:bg-gray-50 transition duration-150 ease-in-out"
                                            >
                                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                    {submission.problemTitle}
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                                    <span
                                                        className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                                            submission.pass
                                                                ? "bg-green-100 text-green-800"
                                                                : "bg-red-100 text-red-800"
                                                        }`}
                                                    >
                                                        {submission.pass
                                                            ? "성공"
                                                            : "실패"}
                                                    </span>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                                    {dayjs(
                                                        submission.createdAt
                                                    ).format(
                                                        "YYYY-MM-DD HH:mm:ss"
                                                    )}
                                                </td>
                                            </tr>
                                        )
                                    )
                                ) : (
                                    <tr>
                                        <td
                                            colSpan={3}
                                            className="px-6 py-4 text-center text-gray-500"
                                        >
                                            제출 기록이 없습니다.
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

export default AdminUserDetailPage;
