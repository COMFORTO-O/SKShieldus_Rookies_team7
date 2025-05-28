import Label from "./Label";

const AuthInput = ({
    id,
    label,
    type = "text",
    placeholder = "",
    bottomText = "",
    value,
    onChange,
    className = "",
    required = false,
    error = "",
}) => {
    // input 값 초기화 함수
    const inputClear = () => {
        onChange({ target: { value: "" } });
    };

    return (
        <div className="relative w-full">
            {/* 라벨 */}
            <Label
                htmlFor={id}
                className="absolute -top-2 left-3 px-1 bg-white text-xs text-gray-500"
            >
                {label}
            </Label>

            <div className="flex items-center border border-black rounded px-4 py-3 focus-within:ring-2 focus-within:ring-primary">
                <input
                    id={id}
                    type={type}
                    placeholder={placeholder}
                    value={value}
                    onChange={onChange}
                    required={required}
                    className={`w-full outline-none bg-transparent
                                ${
                                    error
                                        ? "border-red-500 focus:ring-red-300"
                                        : "border-gray-300 focus:ring-blue-300"
                                }
                                ${className}`}
                />
                <button
                    type="button"
                    className="text-2xl text-gray-400 hover:text-black ml-2"
                    onClick={inputClear}
                >
                    ⊗
                </button>
            </div>

            {/* 하단 메시지 */}
            <div className="ml-2 mt-1 text-xs text-gray-400">{bottomText}</div>
        </div>
    );
};

export default AuthInput;
