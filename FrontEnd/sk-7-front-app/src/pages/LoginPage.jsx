import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import Label from "../components/atoms/Label";
import Input from "../components/atoms/Input";

function LoginPage() {
    return (
        <>
            {/* 전체 레이아웃 */}
            <div className="grid grid-cols-[40%_1fr] h-screen">
                {/* 왼쪽 컨테이너 */}
                <div className="flex flex-col h-screen">
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
                    <div className="w-full">
                        <AuthLayout title="Login">
                            <form className="w-full space-y-8">
                                <AuthInput
                                    label="아이디"
                                    placeholder="ID"
                                    bottomText="아이디를 입력허세요."
                                />
                                <AuthInput
                                    label="비밀번호"
                                    placeholder="PW"
                                    bottomText="비밀번호를 입력허세요."
                                />

                                {/* 로그인 버튼 */}
                                <Button className="w-full bg-primary text-white font-bold py-2 rounded hover:bg-gray-800">
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
