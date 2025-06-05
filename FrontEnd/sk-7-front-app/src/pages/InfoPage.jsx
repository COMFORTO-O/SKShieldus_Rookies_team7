import { useEffect, useState } from "react";
import useAuthStore from "../store/useAuthStore";
import getMyPageInfo from "../api/getMyPageInfo";
import ProfileAccount from "../components/molecules/ProfileAccount";
import ProfileActivity from "../components/molecules/ProfileActivity";

const InfoPage = () => {
    const { accessToken } = useAuthStore();
    const [loading, setLoading] = useState(true);
    const [myPageData, setMyPageData] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const data = await getMyPageInfo();
                setMyPageData(data);
            } catch (err) {
                console.error("마이페이지 로딩 실패", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [accessToken]);

    if (loading || !myPageData) return <div>로딩 중...</div>;

return (
    <div className="flex justify-center min-h-screen w-full px-[20%] py-[5%] bg-slate-100">
        <div className="w-[1000px] min-h-screen rounded-2xl bg-white">
            <div className="p-7 font-bold text-2xl">프로필</div>
            <div className="w-full flex flex-col justify-center">
                <ProfileAccount name={myPageData.name} email={myPageData.email} />
                <ProfileActivity submissions={myPageData.submissions.content} />
            </div>
        </div>
    </div>
);
};

export default InfoPage;
