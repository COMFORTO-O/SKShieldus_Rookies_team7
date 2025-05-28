import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import useAuthForm from "../hooks/useAuthForm";

import { Link } from "react-router-dom";
import { useCallback } from "react";

function LoginPage() {
    // 아이디, 비밀번호 상태 관리
    const { inputs, errors, handleChange } = useAuthForm({
        id: "",
        password: "",
    });

    // 로그인 폼 클릭 핸들러
    const handleLogin = useCallback(() => {
        // 비밀번호 암호화 ( RSA 공개키 암호화 )
    }, []);

    return (
        <>
            {/* 전체 레이아웃 */}
            <div className="grid grid-cols-1 xl:grid-cols-[40%_1fr] min-h-screen">
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
                <div className="w-full bg-white flex items-center justify-center min-h-screen">
                    {/* 섹션 */}
                    <div className="w-full max-w-xl">
                        <AuthLayout title="Login">
                            <form className="w-full space-y-8">
                                <AuthInput
                                    label="아이디"
                                    placeholder="ID"
                                    bottomMessage={
                                        errors.id
                                            ? errors.id
                                            : inputs.id == ""
                                            ? "비밀번호를 입력하세요."
                                            : ""
                                    }
                                    required={false}
                                    value={inputs.id}
                                    error={errors.id}
                                    onChange={handleChange("id")}
                                />
                                <AuthInput
                                    label="비밀번호"
                                    placeholder="PW"
                                    bottomMessage={
                                        errors.password
                                            ? errors.password
                                            : inputs.password == ""
                                            ? "비밀번호를 입력하세요."
                                            : ""
                                    }
                                    type="password"
                                    required={false}
                                    value={inputs.password}
                                    error={errors.password}
                                    onChange={handleChange("password")}
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
                                <Link to="/register" className="block">
                                    <Button className="w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400">
                                        회원가입
                                    </Button>
                                </Link>
                            </form>
                        </AuthLayout>
                    </div>
                </div>
            </div>
        </>
    );
}

export default LoginPage;
