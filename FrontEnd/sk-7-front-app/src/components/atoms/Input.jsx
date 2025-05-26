const Input = ({
  id,
  label,
  type = "text",
  placeholder = "",
  value,
  onChange,
  className = "",
  required = false,
  error = "",
}) => {
  return (
    <div className="mb-4">
      {label && (
        <label htmlFor={id} className="block mb-1 text-sm font-medium text-gray-700">
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <input
        id={id}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required={required}
        className={`
          w-full px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2
          ${error ? "border-red-500 focus:ring-red-300" : "border-gray-300 focus:ring-blue-300"}
          ${className}
        `}
      />
      {error && <p className="mt-1 text-sm text-red-500">{error}</p>}
    </div>
  );
};

export default Input;
