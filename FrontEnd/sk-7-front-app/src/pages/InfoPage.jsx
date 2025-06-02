import { useState } from "react";

import useAuthStore from "../store/useAuthStore";
import ProfileTap from "../components/molecules/ProfileTap";
import ProfileAccount from "../components/molecules/ProfileAccount";
import ProfileActivity from "../components/molecules/ProfileActivity";

const InfoPage = () => {
    const { isLoggedIn, accessToken } = useAuthStore();
    const [loading, setLoading] = useState(false);

    // 계정 관리, 나의 활동  [ account, activity ]
    const [activeTab, setActiveTab] = useState("account");

    return (
        <div className="h-full w-full overflow-auto p-6 bg-gray-50">
            {/* 탭버튼 */}
            <ProfileTap activeTab={activeTab} setActiveTab={setActiveTab} />

            {/* 탭 내용 */}
            <div className="border-solid border-2 border-black w-full h-full">
                {activeTab === "account" && <ProfileAccount />}
                {activeTab === "activity" && <ProfileActivity />}
            </div>
        </div>
    );
};

export default InfoPage;
