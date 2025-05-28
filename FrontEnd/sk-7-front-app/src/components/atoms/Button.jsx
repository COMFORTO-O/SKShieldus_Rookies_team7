import React from "react";

const Button = ({ children, onClick, disabled = false, className = "" }) => {
    return (
        <button
            onClick={onClick}
            disabled={disabled}
            className={`
        px-4 py-2 rounded-lg font-semibold shadow-md transition
        ${className}
      `}
        >
            {children}
        </button>
    );
};

export default Button;
