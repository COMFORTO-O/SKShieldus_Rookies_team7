import PropTypes from "prop-types";

const Label = ({ htmlFor, children, className = "" }) => {
    return (
        <label htmlFor={htmlFor} className={`block text-gray-700 ${className}`}>
            {children}
        </label>
    );
};

Label.propTypes = {
    htmlFor: PropTypes.string,
    children: PropTypes.node.isRequired,
    className: PropTypes.string,
};

export default Label;
