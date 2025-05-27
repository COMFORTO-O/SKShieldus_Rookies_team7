import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import Label from "../components/atoms/Label";
import Input from "../components/atoms/Input";

function SignupPage() {
    return (
        <>
            {/* 전체 레이아웃 */}
            <div className="grid grid-cols-1 2xl:grid-cols-[1fr_40%] h-screen overflow-y-auto">
                {/* 왼쪽 컨테이너 */}
                <div className="w-full bg-white flex items-center justify-center">
                    {/* 섹션 */}
                    <div className="w-full max-w-2xl">
                        <AuthLayout title="User Register">
                            <form className="w-full space-y-4">
                                <AuthInput
                                    label="아이디"
                                    placeholder="ID"
                                    bottomText="아이디를 입력하세요."
                                    type="text"
                                />
                                <AuthInput
                                    label="비밀번호"
                                    placeholder="PW"
                                    bottomText="비밀번호를 입력하세요."
                                    type="password"
                                />
                                <AuthInput
                                    label="비밀번호 확인"
                                    placeholder="PW 재입력"
                                    bottomText="비밀번호를 다시 입력하세요."
                                    type="password"
                                />

                                <div className="pt-10">
                                    <AuthInput
                                        label="이름"
                                        placeholder="Name"
                                        bottomText="비밀번호를 다시 입력하세요."
                                    />
                                </div>

                                <div className="flex items-start space-x-2">
                                    <div className="flex-1">
                                        <AuthInput
                                            id="email1"
                                            label="이메일"
                                            placeholder="Email"
                                            bottomText="이메일을 입력하세요."
                                        />
                                    </div>

                                    <div className="flex items-center pt-4 font-bold">
                                        @
                                    </div>

                                    <div className="flex-1">
                                        <AuthInput
                                            id="email2"
                                            label="도메인"
                                            placeholder="domain.com"
                                            bottomText=""
                                        />
                                    </div>
                                </div>

                                <AuthInput
                                    label="전화번호"
                                    placeholder="휴대폰 번호 ('-' 제외 11자리 입력 )"
                                    bottomText="비밀번호를 다시 입력하세요."
                                />

                                {/* 회원가입 버튼 */}
                                <div>
                                    <Button className="h-14 my-14 w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400">
                                        회원가입
                                    </Button>
                                </div>
                            </form>
                        </AuthLayout>
                    </div>
                </div>

                {/* 오른쪽 컨테이너 */}
                <div className="hidden 2xl:flex flex-col h-screen">
                    {/* 섹션 */}
                    <div className="h-full bg-primary text-white font-bold text-6xl">
                        {/* 텍스트 배치 */}
                        <div className="mt-[30%] ml-[10%]">
                            <div className="mb-2">안내 문구</div>
                            <div>홍보 타이틀</div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SignupPage;
