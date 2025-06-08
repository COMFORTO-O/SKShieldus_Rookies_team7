import PropTypes from "prop-types";

const Input = ({
    id,
    type = "text",
    name,
    placeholder = "",
    value,
    onChange,
    className = "",
    required = false,
    error = "",
    readOnly = false,
}) => {
    return (
        <div className="mb-4">
            <input
                id={id}
                type={type}
                name={name}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                required={required}
                readOnly={readOnly}
                className={`
          w-full px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2
          ${
              error
                  ? "border-red-500 focus:ring-red-300"
                  : "border-gray-300 focus:ring-blue-300"
          }
          ${className}
        `}
            />
            {error && <p className="mt-1 text-sm text-red-500">{error}</p>}
        </div>
    );
};

Input.propTypes = {
    id: PropTypes.string,
    type: PropTypes.string,
    name: PropTypes.string,
    placeholder: PropTypes.string,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    onChange: PropTypes.func.isRequired,
    className: PropTypes.string,
    required: PropTypes.bool,
    error: PropTypes.string,
    readOnly: PropTypes.bool,
};

export default Input;
