import Label from "./Label";

const AuthInput = ({
    id,
    label,
    type = "text",
    placeholder = "",
    bottomText = "",
    value,
    required = false,
    error = "",
    onChange,
    className = "",
    ...rest
}) => {
    // input 값 초기화 함수
    const inputClear = () => {
        onChange({ target: { value: "" } });
    };

    return (
        <div className="relative w-full">
            {/* 라벨 */}
            {label && (
                <Label
                    htmlFor={id}
                    className="absolute -top-2 left-3 px-1 bg-white text-xs text-gray-500"
                >
                    {label}
                    {required && <span className="text-red-500 ml-1">*</span>}
                </Label>
            )}

            {/* 인풋 박스 */}
            <div className="flex items-center border border-black rounded px-4 py-3 focus-within:ring-2 focus-within:ring-primary">
                <input
                    id={id}
                    type={type}
                    placeholder={placeholder}
                    value={value}
                    onChange={onChange}
                    required={required}
                    {...rest}
                    className={`w-full outline-none bg-transparent ${className}`}
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
            <div
                className={`ml-2 mt-1 text-xs ${
                    error ? " text-red-600" : " text-gray-400"
                }`}
            >
                {bottomText}
            </div>
        </div>
    );
};

export default AuthInput;
