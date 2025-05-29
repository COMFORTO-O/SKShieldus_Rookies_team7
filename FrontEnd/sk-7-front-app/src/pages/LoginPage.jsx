import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import useAuthStore from "../store/useAuthStore";

import { useCallback, useState, useEffect } from "react";
import { encryptPassword } from "../encrypt/encryptPassword";
import { loginTask } from "../api/loginTask";
import { useNavigate } from "react-router-dom";
import Spinner from "../components/atoms/Spinner";

// 로그인 페이지

function LoginPage() {
    // 아이디, 비밀번호 상태 저장
    const [id, setId] = useState("");
    const [password, setPassword] = useState("");
    // 로그인 정보 상태
    const { isLoggedIn, setLogin } = useAuthStore();
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
                const result = await loginTask({
                    email: id,
                    encryptedPassword: e_pwd,
                });
                // result = response.data
                if (result && result.token) {
                    setLogin(result.token); // 상태 저장
                    // 페이지 이동
                    navigate("/", { replace: true });
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

            // // 테스트용 코드
            // if (id === "test" && password === "test") {
            //     alert("로그인 성공");
            //     navigate("/", { replace: true });
            //     setTimeout(() => {
            //         setLogin({ isLoggedIn: true, user: "test user" });
            //         console.log(isLoggedIn);
            //     }, 1000);
            // }
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
        <>
            {/* 전체 레이아웃 */}
            <div className="grid grid-cols-1 xl:grid-cols-[40%_1fr]">
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
                            <form
                                className="w-full space-y-8"
                                onSubmit={handleLogin}
                            >
                                {/* 에러 메시지 표시 */}
                                {error && (
                                    <div className="w-full text-center text-red-600 bg-red-50 border border-red-200 rounded p-2 mb-2">
                                        {error}
                                    </div>
                                )}
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

                                {/* 회원가입 버튼 */}
                                <Button
                                    className="w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400"
                                    onClick={() => {
                                        navigate("/register", {
                                            replace: true,
                                        });
                                    }}
                                >
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
