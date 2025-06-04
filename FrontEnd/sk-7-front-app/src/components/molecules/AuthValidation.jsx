import { useState } from "react";

const AuthValidation = (initialFields) => {
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
                if (!value || !/^010\d{8}$/.test(value)) {
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

export default AuthValidation;
