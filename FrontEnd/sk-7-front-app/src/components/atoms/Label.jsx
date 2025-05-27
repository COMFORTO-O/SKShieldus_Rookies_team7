const Label = ({ htmlFor, children, className = "", required = false }) => {
  return (
    <label
      htmlFor={htmlFor}
      className={`block text-gray-700 ${className}`}
    >
      {children}
      {required && <span className="text-red-500 ml-1">*</span>}
    </label>
  );
};

export default Label;
