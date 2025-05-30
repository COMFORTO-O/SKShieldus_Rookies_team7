const Label = ({ htmlFor, children, className = "" }) => {
    return (
        <label htmlFor={htmlFor} className={`block text-gray-700 ${className}`}>
            {children}
        </label>
    );
};

export default Label;
