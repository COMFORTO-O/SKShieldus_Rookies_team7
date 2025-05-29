import AuthInput from "../atoms/AuthInput";
import Button from "../atoms/Button";
import useAuthForm from "../../hooks/useAuthForm";

import { encryptPassword } from "../../encrypt/encryptPassword";
import { RegisterTask } from "../../api/RegisterTask";
import { useNavigate } from "react-router-dom";

function SignupForm() {
    const navigate = useNavigate();

    // 검증 훅 불러오기
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
        if (hasError) return alert("정보를 다시 확인해주세요.");

        const { password, name, phone, emailId, emailDomain } = inputs;
        const e_pwd = encryptPassword(password);

        try {
            const result = await RegisterTask({
                email: `${emailId}@${emailDomain}`,
                encryptedPassword: e_pwd,
                name,
                phone,
            });
            if (result?.token) navigate("/login");
        } catch (err) {
            alert("회원가입 실패");
            console.error(err);
        }
    };

    return (
        <form className="w-full space-y-4" onSubmit={handleRegister}>
            <AuthInput
                label="아이디"
                placeholder="ID"
                value={inputs.id}
                onChange={handleChange("id")}
                error={errors.id}
                bottomMessage={
                    errors.id ||
                    (inputs.id === "" ? "아이디를 입력하세요." : "")
                }
                required
            />
            <AuthInput
                label="비밀번호"
                placeholder="PW"
                type="password"
                value={inputs.password}
                onChange={handleChange("password")}
                error={errors.password}
                bottomMessage={
                    errors.password ||
                    (inputs.password === "" ? "비밀번호를 입력하세요." : "")
                }
                required
            />
            <AuthInput
                label="비밀번호 확인"
                placeholder="PW 재입력"
                type="password"
                value={inputs.checkPassword}
                onChange={handleChange("checkPassword")}
                error={errors.checkPassword}
                bottomMessage={
                    errors.checkPassword ||
                    (inputs.checkPassword === ""
                        ? "비밀번호를 다시 입력하세요."
                        : "")
                }
                required
            />
            <div className="pt-10">
                <AuthInput
                    label="이름"
                    placeholder="Name"
                    value={inputs.name}
                    onChange={handleChange("name")}
                    error={errors.name}
                    bottomMessage={
                        errors.name ||
                        (inputs.name === "" ? "이름을 입력하세요." : "")
                    }
                    required
                />
            </div>
            <div className="flex items-start space-x-2">
                <div className="flex-1">
                    <AuthInput
                        label="이메일"
                        placeholder="Email"
                        value={inputs.emailId}
                        onChange={handleChange("emailId")}
                        error={errors.emailId}
                        bottomMessage={
                            errors.emailId ||
                            (inputs.emailId === ""
                                ? "이메일을 입력하세요."
                                : "")
                        }
                        required
                    />
                </div>
                <div className="flex items-center pt-4 font-bold">@</div>
                <div className="flex-1">
                    <AuthInput
                        label="도메인"
                        placeholder="domain.com"
                        value={inputs.emailDomain}
                        onChange={handleChange("emailDomain")}
                        required
                    />
                </div>
            </div>
            <AuthInput
                label="전화번호"
                placeholder="휴대폰 번호 ('-' 제외 11자리 입력 )"
                type="phone"
                value={inputs.phone}
                onChange={handleChange("phone")}
                error={errors.phone}
                bottomMessage={
                    errors.phone ||
                    (inputs.phone === "" ? "휴대폰 번호를 입력하세요." : "")
                }
                required
            />

            <Button
                type="submit"
                className="h-14 my-14 w-full bg-gray-300 text-black font-bold py-2 rounded hover:bg-gray-400"
            >
                회원가입
            </Button>
        </form>
    );
}

export default SignupForm;
