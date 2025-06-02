const ProfileTap = ({ activeTab, setActiveTab }) => {
    return (
        <div className="flex justify-center gap-4 mb-6 bg-gradient-to-r from-blue-100 via-blue-200 to-blue-300 h-[200px]">
            <div className="flex justify-between items-end w-[70%]">
                <div className="text-2xl font-bold text-blue-800">
                    👤 내 프로필
                </div>
                <div className="flex gap-2">
                    {[
                        { key: "account", label: "계정 관리" },
                        { key: "activity", label: "나의 활동" },
                    ].map((tab) => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key)}
                            className={`px-6 py-2 rounded-t-md text-sm font-semibold transition-all duration-200
                            ${
                                activeTab === tab.key
                                    ? "bg-white text-blue-600 font-bold border-b-4 border-blue-600 shadow-md"
                                    : "bg-blue-100 text-blue-500 hover:bg-blue-200"
                            }`}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default ProfileTap;
