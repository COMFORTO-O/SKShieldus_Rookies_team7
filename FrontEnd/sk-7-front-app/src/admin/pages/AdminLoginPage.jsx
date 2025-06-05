import { useState } from "react";
import { useNavigate } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";
import Input from "../../components/atoms/Input";
import Button from "../../components/atoms/Button";
import { loginAdminTask } from "../../admin/api/loginAdminTask";

import { encryptPassword } from "../../encrypt/encryptPassword";

const AdminLoginPage = () => {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            let encryptedPassword = encryptPassword(password);
            const success = await loginAdminTask({
                email: email,
                encryptedPassword: encryptedPassword
            });
            if (success) {
                navigate("/admin"); // 로그인 성공 시 관리자 대시보드로 이동
            } else {
                setErrorMsg("이메일 또는 비밀번호가 올바르지 않습니다.");
            }
        } catch (error) {
            console.log(error)
            setErrorMsg("로그인 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="w-full max-w-md p-8 bg-white rounded shadow-md">
                <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">
                    관리자 로그인
                </h2>

                {errorMsg && (
                    <div className="mb-4 text-red-600 text-center font-medium">
                        {errorMsg}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <Input
                        type="text"
                        placeholder="ID"
                        className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        autoFocus
                    />
                    <Input
                        type="password"
                        placeholder="PW"
                        className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />

                    <Button
                        type="submit"
                        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded py-2 transition"
                    >
                        로그인
                    </Button>
                </form>
            </div>
        </div>
    );
};

export default AdminLoginPage;
