import { Avatar } from "@mui/material";
import ProfileAccount from "../components/molecules/ProfileAccount";
import ProfileActivity from "../components/molecules/ProfileActivity";
import useAuthStore from "../store/useAuthStore";
import { useState, useEffect } from "react";
import getUserInfo from "../api/getUserInfo";

const InfoPage = () => {
    const { isLoggedIn } = useAuthStore();
    const [name, setName] = useState("");
    const [solvedCount, setSolvedCount] = useState(0);
    const [email, setEmail] = useState(null);

    const [current, setCurrent] = useState("account");

    const [error, setError] = useState("");

    useEffect(() => {
        if (isLoggedIn) {
            // 유저 정보 가져오기
            const fetchUserInfo = async () => {
                try {
                    const data = await getUserInfo();
                    setName(data.name);
                    setSolvedCount(data.solvedProblems.length);
                    setEmail(data.email);
                } catch (e) {
                    setError(
                        e?.message || "사용자 정보를 불러오지 못했습니다."
                    );
                    setName(null);
                }
            };
            fetchUserInfo();
        }
    }, [isLoggedIn]);

    if (!error) {
        return <div className="w-full ">{error}</div>;
    }

    return (
        <div className="w-screen">
            <div className="w-full flex mt-10">
                {/* 네비게이터 바 (계정 관리, 나의 활동) */}
                <div className="flex flex-col rounded-md border-2 ml-5 mr-10 w-[250px] inline-block ">
                    <div>계정 관리</div>
                    <div>나의 활동</div>
                </div>
                {/* 내용 */}
                <div className="flex-1 rounded-md border-2 mr-10">
                    {current === "account" ? (
                        <div>
                            <h1 className="text-2xl font-semibold text-black">
                                계정 관리
                            </h1>
                            <h2 className="text-lg mt-5 font-semibold text-primary">
                                기본 정보
                            </h2>
                            <div className="w-full h-[400px]">이름</div>
                        </div>
                    ) : (
                        <h1>나의 활동</h1>
                    )}
                </div>
            </div>
        </div>
    );
};

export default InfoPage;
