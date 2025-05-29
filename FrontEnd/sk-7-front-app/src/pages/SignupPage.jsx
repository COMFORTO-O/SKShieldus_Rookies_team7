import { useState } from "react";
import AuthLayout from "../components/auth/AuthLayout";
import AuthInput from "../components/atoms/AuthInput";
import Button from "../components/atoms/Button";
import { encryptPassword } from "../encrypt/encryptPassword";
import { RegisterTask } from "../api/RegisterTask";
import { useNavigate } from "react-router-dom";

// useAuthForm 훅 통합 정의
const useAuthForm = (initialFields) => {
    const [inputs, setInputs] = useState(initialFields);
    const [errors, setErrors] = useState({});

    const validateField = (key, value, updatedInputs) => {
        let error = "";

        switch (key) {
            case "id":
                if (!value || !/^[a-zA-Z0-9]{5,15}$/.test(value)) {
                    error = "5~15자 영문 또는 숫자만 입력 가능합니다.";
                }
                break;
            case "password":
                if (!value || !/^(?=.*[a-zA-Z])(?=.*\d).{5,}$/.test(value)) {
                    error = "5자 이상, 영문과 숫자를 포함해야 합니다.";
                }
                break;
            case "checkPassword":
                if (!value || value !== updatedInputs.password) {
                    error = "비밀번호가 일치하지 않습니다.";
                }
                break;
            case "name":
                if (!value || !/^[a-zA-Z가-힣]{2,}$/.test(value)) {
                    error = "이름은 한글 또는 영문으로 2자 이상 입력해 주세요.";
                }
                break;
            case "emailId":
            case "emailDomain": {
                const email =
                    key === "emailId"
                        ? `${value}@${updatedInputs.emailDomain}`
                        : `${updatedInputs.emailId}@${value}`;
                if (!updatedInputs.emailId || !updatedInputs.emailDomain) {
                    error = "이메일을 입력해 주세요.";
                } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    error = "이메일 형식이 올바르지 않습니다.";
                }
                break;
            }
            case "phone":
                if (!value || !/^010\d{7}$/.test(value)) {
                    error = "010으로 시작하는 11자리 숫자만 입력하세요.";
                }
                break;
            default:
                break;
        }

        setErrors((prev) => ({
            ...prev,
            [key === "emailDomain" ? "emailId" : key]: error,
        }));
    };

    // input value 업데이트 및 검증 함수
    const handleChange = (key) => (e) => {
        const value = e.target.value;
        setInputs((prev) => {
            const updated = {
                ...prev,
                [key]: value,
            };
            validateField(key, value, updated);
            return updated;
        });
    };

    return {
        inputs,
        errors,
        handleChange,
    };
};

function SignupPage() {
    const navigate = useNavigate();

    const { inputs, errors, handleChange } = useAuthForm({
        id: "",
        password: "",
        checkPassword: "",
        name: "",
        emailId: "",
        emailDomain: "",
        phone: "",
    });

    const handleRegister = async (e) => {
        e.preventDefault();

        const hasError = Object.values(errors).some((error) => error);
        if (hasError) {
            alert("입력된 정보를 다시 확인해주세요.");
            return;
        }

        const { password, name, phone, emailId, emailDomain } = inputs;
        const e_pwd = encryptPassword(password);

        try {
            const result = await RegisterTask({
                email: `${emailId}@${emailDomain}`,
                encryptedPassword: e_pwd,
                name,
                phone,
            });

            if (result && result.token) {
                navigate("/login", { replace: true });
            }
        } catch (err) {
            alert("회원가입 실패");
            console.error(err);
        }
        [inputs, errors, navigate];
    };

    return (
        <div className="grid grid-cols-1 2xl:grid-cols-[1fr_40%] min-h-screen">
            <div className="w-full bg-white flex items-center justify-center">
                <div className="w-full max-w-2xl">
                    <AuthLayout title="User Register">
                        <form className="w-full space-y-4">
                            <AuthInput
                                label="아이디"
                                placeholder="ID"
                                bottomMessage={
                                    errors.id
                                        ? errors.id
                                        : inputs.id === ""
                                        ? "아이디를 입력하세요."
                                        : ""
                                }
                                required
                                value={inputs.id}
                                error={errors.id}
                                onChange={handleChange("id")}
                            />
                            <AuthInput
                                label="비밀번호"
                                placeholder="PW"
                                type="password"
                                bottomMessage={
                                    errors.password
                                        ? errors.password
                                        : inputs.password === ""
                                        ? "비밀번호를 입력하세요."
                                        : ""
                                }
                                required
                                value={inputs.password}
                                error={errors.password}
                                onChange={handleChange("password")}
                            />
                            <AuthInput
                                label="비밀번호 확인"
                                placeholder="PW 재입력"
                                type="password"
                                bottomMessage={
                                    errors.checkPassword
                                        ? errors.checkPassword
                                        : inputs.checkPassword === ""
                                        ? "비밀번호를 다시 입력하세요."
                                        : ""
                                }
                                required
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
                                            : inputs.name === ""
                                            ? "이름을 입력하세요."
                                            : ""
                                    }
                                    required
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
                                                : inputs.emailId === ""
                                                ? "이메일을 입력하세요."
                                                : ""
                                        }
                                        required
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
                                        required
                                        value={inputs.emailDomain}
                                        onChange={handleChange("emailDomain")}
                                    />
                                </div>
                            </div>
                            <AuthInput
                                label="전화번호"
                                placeholder="휴대폰 번호 ('-' 제외 11자리 입력 )"
                                type="phone"
                                bottomMessage={
                                    errors.phone
                                        ? errors.phone
                                        : inputs.phone === ""
                                        ? "휴대폰 번호를 입력하세요."
                                        : ""
                                }
                                required
                                value={inputs.phone}
                                error={errors.phone}
                                onChange={handleChange("phone")}
                            />
                            <div>
                                <Button
                                    type="submit"
                                    onClick={handleRegister}
                                    className="h-14 my-14 w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400"
                                >
                                    회원가입
                                </Button>
                            </div>
                        </form>
                    </AuthLayout>
                </div>
            </div>
            <div className="hidden 2xl:flex flex-col min-h-screen">
                <div className="h-full bg-primary text-white font-bold text-6xl">
                    <div className="mt-[30%] ml-[10%]">
                        <div className="mb-2">안내 문구</div>
                        <div>홍보 타이틀</div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default SignupPage;
