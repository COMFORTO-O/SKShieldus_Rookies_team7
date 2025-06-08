import PropTypes from "prop-types";

const LanguageSelect = ({
    languages,
    selectedLanguage,
    setSelectedLanguage,
}) => {
    const handleChange = (e) => {
        setSelectedLanguage(e.target.value);
    };

    return (
        <div>
            <select
                value={selectedLanguage}
                onChange={handleChange}
                className="border border-gray-300 rounded-md w-full p-2"
            >
                {languages.map((item) => (
                    <option value={item.code}>{item.code}</option>
                ))}
            </select>
        </div>
    );
};

LanguageSelect.propTypes = {
    languages: PropTypes.arrayOf(
        PropTypes.shape({
            code: PropTypes.string.isRequired,
        })
    ).isRequired,
    selectedLanguage: PropTypes.string.isRequired,
    setSelectedLanguage: PropTypes.func.isRequired,
};

export default LanguageSelect;
