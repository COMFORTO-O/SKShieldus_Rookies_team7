import { useState } from "react";

const LanguageSelect = () => {
    const [selectedLanguage, setSelectedLanguage] = useState("");

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
                <option value="Java">Java</option>
                <option value="Python">Python</option>
                <option value="C++">C++</option>
                <option value="JavaScript">JavaScript</option>
            </select>
        </div>
    );
};

export default LanguageSelect;
