import { useState, useCallback } from "react";

const useAuthForm = (initialFields) => {
    const [inputs, setInputs] = useState(initialFields);
    const [errors, setErrors] = useState({});

    const validate = useCallback(() => {
        const newErrors = {};

        if (!inputs.id || !/^[a-zA-Z0-9]{5,15}$/.test(inputs.id)) {
            newErrors.id = "5~15자 영문 또는 숫자만 입력 가능합니다.";
        }

        if (
            !inputs.password ||
            !/^(?=.*[a-zA-Z])(?=.*\d).{5,}$/.test(inputs.password)
        ) {
            newErrors.password = "5자 이상, 영문과 숫자를 포함해야 합니다.";
        }

        if (!inputs.checkPassword || inputs.password !== inputs.checkPassword) {
            newErrors.checkPassword = "비밀번호가 일치하지 않습니다.";
        }

        if (!inputs.name || !/^[a-zA-Z가-힣]{2,}$/.test(inputs.name)) {
            newErrors.name =
                "이름은 한글 또는 영문으로 2자 이상 입력해 주세요.";
        }

        if (!inputs.emailId || !inputs.emailDomain) {
            newErrors.emailId = "이메일을 입력해 주세요.";
        } else if (
            !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
                `${inputs.emailId}@${inputs.emailDomain}`
            )
        ) {
            newErrors.emailId = "이메일 형식이 올바르지 않습니다.";
        }

        if (!inputs.phone || !/^010\d{7}$/.test(inputs.phone)) {
            newErrors.phone = "010으로 시작하는 11자리 숫자만 입력하세요.";
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    }, [inputs]);

    // 입력값 변경 핸들러
    const handleChange = useCallback(
        (key) => (e) => {
            const value = e.target.value;
            setInputs((prev) => ({
                ...prev,
                [key]: value,
            }));
        },
        []
    );

    return {
        inputs,
        errors,
        handleChange,
        validate,
    };
};

export default useAuthForm;
