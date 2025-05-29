import { useState, useCallback } from "react";
import AuthInput from "../atoms/AuthInput";
import Button from "../atoms/Button";
import { encryptPassword } from "../../encrypt/encryptPassword";
import { loginTask } from "../../api/loginTask";
import useAuthStore from "../../store/useAuthStore";
import { useNavigate } from "react-router-dom";

function LoginForm() {
    const [id, setId] = useState("");
    const [password, setPassword] = useState("");
    const { setLogin } = useAuthStore();
    const navigate = useNavigate();

    const handleIdChange = useCallback((e) => {
        setId(e.target.value);
    }, []);

    const handlePasswordChange = useCallback((e) => {
        setPassword(e.target.value);
    }, []);

    const handleLogin = useCallback(
        async (e) => {
            e.preventDefault();
            const e_pwd = encryptPassword(password);
            try {
                const result = await loginTask({
                    email: id,
                    encryptedPassword: e_pwd,
                });
                if (result && result.token) {
                    setLogin(result.user);
                    navigate("/");
                }
            } catch (err) {
                alert("로그인 실패");
                console.error(err);
            }
        },
        [id, password, setLogin, navigate]
    );

    return (
        <form className="w-full space-y-8">
            <AuthInput
                label="이메일"
                placeholder="Email"
                bottomMessage="이메일을 입력하세요."
                onChange={handleIdChange}
            />
            <AuthInput
                label="비밀번호"
                placeholder="PW"
                bottomMessage="비밀번호를 입력하세요."
                type="password"
                onChange={handlePasswordChange}
            />
            <Button
                className="w-full bg-primary text-white font-bold py-2 rounded hover:bg-gray-800"
                type="submit"
                onClick={handleLogin}
            >
                로그인
            </Button>
            <Button className="w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400">
                회원가입
            </Button>
        </form>
    );
}

export default LoginForm;
