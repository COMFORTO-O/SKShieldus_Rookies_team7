import PropTypes from "prop-types";

const Button = ({
    children,
    type,
    onClick,
    disabled = false,
    className = "",
}) => {
    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={`
        px-4 py-2 rounded-lg shadow-md transition
        ${disabled ? "opacity-50 cursor-not-allowed" : ""}
        ${className}
      `}
        >
            {children}
        </button>
    );
};

Button.propTypes = {
    children: PropTypes.node.isRequired,
    type: PropTypes.oneOf(["button", "submit", "reset"]),
    onClick: PropTypes.func,
    disabled: PropTypes.bool,
    className: PropTypes.string,
};

export default Button;
