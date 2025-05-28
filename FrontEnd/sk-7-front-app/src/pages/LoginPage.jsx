import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import useAuthStore from "../store/useAuthStore";

import { useCallback, useState } from "react";
import { encryptPassword } from "../encrypt/encryptPassword";
import { loginTask } from "../api/loginTask";
import { useNavigate } from "react-router-dom";

// 로그인 페이지

function LoginPage() {
    // 아이디, 비밀번호 상태 저장
    const [id, setId] = useState("");
    const [password, setPassword] = useState("");
    // 로그인 정보 상태
    const { isLoggedIn, setLogin } = useAuthStore();

    // 페이지 이동 네비게이터
    const navigate = useNavigate();

    // 아이디 onChange
    const handleIdChange = useCallback((e) => {
        setId(e.target.value);
    }, []);

    // 비밀번호 onChange
    const handlePasswordChange = useCallback((e) => {
        setPassword(e.target.value);
    }, []);

    // 로그인 폼 클릭 핸들러
    const handleLogin = useCallback(
        async (e) => {
            e.preventDefault();

            // 비밀번호 암호화 ( RSA 공개키 암호화 )
            const e_pwd = encryptPassword(password);

            // 로그인 시도
            try {
                const result = await loginTask({
                    email: id,
                    encryptedPassword: e_pwd,
                });
                // 예시: result.token, result.user 등
                if (result && result.token) {
                    setLogin(result.user); // zustand 상태 저장
                    // Cookies.set("token", result.token, { expires: 1 }); // 쿠키 1일 저장
                    // 페이지 이동
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
        <>
            {/* 전체 레이아웃 */}
            <div className="grid grid-cols-1 xl:grid-cols-[40%_1fr] overflow-y-auto">
                {/* 왼쪽 컨테이너 */}
                <div className="hidden xl:flex flex-col">
                    {/* 섹션 */}
                    <div className="h-full bg-primary text-white font-bold text-6xl">
                        {/* 텍스트 배치 */}
                        <div className="mt-[30%] ml-[10%]">
                            <div className="mb-2">안내 문구</div>
                            <div>홍보 타이틀</div>
                        </div>
                    </div>
                </div>

                {/* 오른쪽 컨테이너 */}
                <div className="w-full bg-white flex items-center justify-center">
                    {/* 섹션 */}
                    <div className="w-full max-w-xl">
                        <AuthLayout title="Login">
                            <form className="w-full space-y-8">
                                <AuthInput
                                    label="이메일"
                                    placeholder="Email"
                                    bottomText="이메일을 입력하세요."
                                    onChange={handleIdChange}
                                />
                                <AuthInput
                                    label="비밀번호"
                                    placeholder="PW"
                                    bottomText="비밀번호를 입력하세요."
                                    onChange={handlePasswordChange}
                                    type="password"
                                />

                                {/* 로그인 버튼 */}
                                <Button
                                    className="w-full bg-primary text-white font-bold py-2 rounded hover:bg-gray-800"
                                    type={"submit"}
                                    onClick={handleLogin}
                                >
                                    로그인
                                </Button>

                                {/* 회원가입 버튼 */}
                                <Button className="w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400">
                                    회원가입
                                </Button>
                            </form>
                        </AuthLayout>
                    </div>
                </div>
            </div>
        </>
    );
}

export default LoginPage;
