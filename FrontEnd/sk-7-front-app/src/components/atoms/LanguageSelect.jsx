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
                    <option key={item.code} value={item.code}>{item.code}</option>
                ))}
            </select>
        </div>
    );
};

export default LanguageSelect;
