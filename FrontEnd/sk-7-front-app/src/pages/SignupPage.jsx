import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import useAuthForm from "../hooks/useAuthForm";

function SignupPage() {
    // 아이디, 비밀번호, 비밀번호 확인, 이름, 이메일, 전화번호 상태 관리 및 검증
    const { inputs, errors, handleChange, validate } = useAuthForm({
        id: "",
        password: "",
        checkPassword: "",
        name: "",
        emailId: "",
        emailDomain: "",
        tel: "",
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        const isValid = validate();

        if (isValid) {
            console.log("폼 제출 준비 완료", inputs);
        } else {
            console.log("유효성 검사 실패", errors);
        }
    };

    return (
        <>
            {/* 전체 레이아웃 */}
            <div className="grid grid-cols-1 2xl:grid-cols-[1fr_40%] min-h-screen">
                {/* 왼쪽 컨테이너 */}
                <div className="w-full bg-white flex items-center justify-center">
                    {/* 섹션 */}
                    <div className="w-full max-w-2xl">
                        <AuthLayout title="User Register">
                            <form className="w-full space-y-4">
                                <AuthInput
                                    label="아이디"
                                    placeholder="ID"
                                    bottomMessage={
                                        errors.id
                                            ? errors.id
                                            : inputs.id == ""
                                            ? "아이디를 입력하세요."
                                            : ""
                                    }
                                    required={true}
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
                                    required={true}
                                    value={inputs.password}
                                    error={errors.password}
                                    onChange={handleChange("password")}
                                />
                                <AuthInput
                                    label="비밀번호 확인"
                                    placeholder="PW 재입력"
                                    bottomMessage={
                                        errors.checkPassword
                                            ? errors.checkPassword
                                            : inputs.checkPassword == ""
                                            ? "비밀번호를 다시 입력하세요."
                                            : ""
                                    }
                                    type="password"
                                    required={true}
                                    value={inputs.checkPassword}
                                    error={errors.checkPassword}
                                    onChange={handleChange("checkPassword")}
                                />

                                <div className="pt-10">
                                    <AuthInput
                                        label="이름"
                                        placeholder="Name"
                                        bottomMessage={
                                            errors.name
                                                ? errors.name
                                                : inputs.name == ""
                                                ? "이름을 입력하세요."
                                                : ""
                                        }
                                        required={true}
                                        value={inputs.name}
                                        error={errors.name}
                                        onChange={handleChange("name")}
                                    />
                                </div>

                                <div className="flex items-start space-x-2">
                                    <div className="flex-1">
                                        <AuthInput
                                            label="이메일"
                                            placeholder="Email"
                                            bottomMessage={
                                                errors.emailId
                                                    ? errors.emailId
                                                    : inputs.emailId == ""
                                                    ? "이메일을 입력하세요."
                                                    : ""
                                            }
                                            required={true}
                                            value={inputs.emailId}
                                            error={errors.emailId}
                                            onChange={handleChange("emailId")}
                                        />
                                    </div>

                                    <div className="flex items-center pt-4 font-bold">
                                        @
                                    </div>

                                    <div className="flex-1">
                                        <AuthInput
                                            label="도메인"
                                            placeholder="domain.com"
                                            required={true}
                                            value={inputs.emailDomain}
                                            onChange={handleChange(
                                                "emailDomain"
                                            )}
                                        />
                                    </div>
                                </div>

                                <AuthInput
                                    label="전화번호"
                                    placeholder="휴대폰 번호 ('-' 제외 11자리 입력 )"
                                    bottomMessage={
                                        errors.tel
                                            ? errors.tel
                                            : inputs.tel == ""
                                            ? "휴대폰 번호을 입력하세요."
                                            : ""
                                    }
                                    type="tel"
                                    required={true}
                                    value={inputs.tel}
                                    error={errors.tel}
                                    onChange={handleChange("tel")}
                                />

                                {/* 회원가입 버튼 */}
                                <div>
                                    <Button
                                        type="submit"
                                        onClick={handleSubmit}
                                        className="h-14 my-14 w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400"
                                    >
                                        회원가입
                                    </Button>
                                </div>
                            </form>
                        </AuthLayout>
                    </div>
                </div>

                {/* 오른쪽 컨테이너 */}
                <div className="hidden 2xl:flex flex-col min-h-screen">
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
