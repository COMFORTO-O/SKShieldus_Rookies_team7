import { useState } from "react";

import useAuthStore from "../store/useAuthStore";
import ProfileAccount from "../components/molecules/ProfileAccount";
import ProfileActivity from "../components/molecules/ProfileActivity";

const InfoPage = () => {
    const { isLoggedIn, accessToken } = useAuthStore();
    const [loading, setLoading] = useState(false);

    return (
        <div className="flex justify-center h-full w-full overflow-auto px-[20%] py-[5%] bg-slate-100 ">
            <div className="w-[1000px] h-[1000px] rounded-2xl overflow-hidden bg-white">
                {/* 헤더 */}
                <div className="p-7 font-bold text-2xl">프로필 </div>

                {/* 콘텐츠 */}
                <div className="w-full h-full flex flex-col justify-center">
                    <div className="w-full h-full">
                        <ProfileAccount />
                    </div>
                    <div className="w-full h-full">
                        <ProfileActivity />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default InfoPage;
