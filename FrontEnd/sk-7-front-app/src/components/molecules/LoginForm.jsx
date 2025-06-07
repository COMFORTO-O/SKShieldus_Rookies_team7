import Button from "../atoms/Button";
import useAuthStore from "../../store/useAuthStore";
import Spinner from "../atoms/Spinner";
import AuthInput from "../atoms/AuthInput";

import { useCallback, useState, useEffect } from "react";
import { encryptPassword } from "../../encrypt/encryptPassword";
import { loginTask } from "../../api/loginTask";
import { useNavigate } from "react-router-dom";
import { getCookie } from "../../api/getCookie";

// 로그인 페이지
export default function LoginForm() {
    // 아이디, 비밀번호 상태 저장
    const [id, setId] = useState("");
    const [password, setPassword] = useState("");
    // 로그인 정보 상태
    const { isLoggedIn, setLogin, setEmail } = useAuthStore();
    // 에러 메세지 상태
    const [error, setError] = useState("");
    // 스피너
    const [loading, setLoading] = useState(false);

    // 페이지 이동 네비게이터
    const navigate = useNavigate();

    // 아이디 onChange
    const handleIdChange = useCallback((e) => {
        setError("");
        setId(e.target.value);
    }, []);

    // 비밀번호 onChange
    const handlePasswordChange = useCallback((e) => {
        setError("");
        setPassword(e.target.value);
    }, []);

    // 로그인 폼 클릭 핸들러
    const handleLogin = useCallback(
        async (e) => {
            e.preventDefault();
            setError("");

            // 아이디, 비밀번호 빈 값 체크
            if (!id.trim() || !password.trim()) {
                setError("이메일과 비밀번호를 모두 입력하세요.");
                return;
            }

            setLoading(true);

            // 비밀번호 암호화 ( RSA 공개키 암호화 )
            const e_pwd = encryptPassword(password);

            // 로그인 시도
            try {
                // { success: , data: }
                const result = await loginTask({
                    email: id,
                    encryptedPassword: e_pwd,
                });
                // result = { success: , data:response.data }
                if (result.success) {
                    // 로그인 성공
                    const token = getCookie("Authorization");

                    if (token) {
                        alert("로그인 성공");
                        // 토큰 정보 저장
                        localStorage.setItem("accessToken", token);
                        localStorage.setItem("Email", id);
                        setLogin(token);
                        setEmail(id);
                        // 페이지 이동
                        navigate("/", { replace: true });
                    } else {
                        setError("로그인은 성공했으나 토큰이 없음.");
                    }
                } else {
                    setError(
                        result?.message ||
                            "아이디 또는 비밀번호가 일치하지 않습니다."
                    );
                }
            } catch (err) {
                setError(
                    err?.response?.data?.message ||
                        err?.message ||
                        "로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                );
                console.error(err);
            } finally {
                setLoading(false);
            }
        },
        [id, password, setLogin, navigate]
    );

    // 로그인 상태면 메인으로 이동 및 새로고침
    useEffect(() => {
        if (isLoggedIn) {
            navigate("/", { replace: true });
        }
    }, [isLoggedIn, navigate]);

    return (
        <div>
            <form className="w-full space-y-8" onSubmit={handleLogin}>
                {/* 에러 메시지 표시 */}
                {error && (
                    <div className="w-full text-center text-red-600 bg-red-50 border border-red-200 rounded p-2 mb-2">
                        {error}
                    </div>
                )}
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

                {/* 로그인 버튼 */}
                <Button
                    className="w-full bg-primary text-white font-bold py-2 rounded hover:bg-gray-800"
                    type={"submit"}
                >
                    {loading ? (
                        <div className="flex items-center justify-center">
                            <Spinner />
                            로그인 중...
                        </div>
                    ) : (
                        "로그인"
                    )}
                </Button>
            </form>
            {/* 회원가입 버튼 */}
            <Button
                className="w-full bg-gray-300 text-black font-bold py-2 rounded mt-8 hover:bg-gray-400"
                onClick={(e) => {
                    e.preventDefault();
                    navigate("/register");
                }}
            >
                회원가입
            </Button>
        </div>
    );
}
