import React from "react";

const Button = ({ children, onClick, disabled = false, className = "" }) => {
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`
        px-4 py-2 rounded-xl font-semibold shadow-md transition
        ${disabled
          ? "bg-gray-300 text-gray-500 cursor-not-allowed shadow-none"
          : "bg-gray-700 hover:bg-gray-800 text-white shadow-lg"}
        ${className}
      `}
    >
      {children}
    </button>
  );
};

export default Button;