import { Avatar, Button } from "@mui/material";
import useAuthStore from "../../store/useAuthStore";
import { useNavigate } from "react-router-dom";
import getUserInfo from "../../api/getUserInfo";
import { useEffect, useState } from "react";

const UserInfoContainerStyle =
    "mx-14 h-36 flex flex-row border-solid border-2 rounded-lg xl:mx-0 xl:flex-col xl:h-full ";

const UserInfo = () => {
    const {
        isLoggedIn,
        accessToken,
        userName,
        userEmail,
        setName,
        setEmail,
        setLogout,
    } = useAuthStore();

    // TODO : const [memberRank, setMemberRank] = useState(0);
    // setMemberRank(data.memberRank)
    const [solvedCount, setSolvedCount] = useState(0);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn) {
            // 유저 정보 가져오기
            const fetchUserInfo = async () => {
                try {
                    const data = await getUserInfo();

                    setName(data.member.name);
                    setSolvedCount(data.submissions.length);
                    setEmail(data.member.email);
                } catch (e) {
                    setError(
                        e?.message || "사용자 정보를 불러오지 못했습니다."
                    );
                    setName(null);
                    setLogout(); // 유저 정보가 없으면 로그아웃 처리
                }
            };
            fetchUserInfo();
        }
    }, [isLoggedIn, setLogout, setEmail, setName]);

    return (
        <>
            {isLoggedIn && accessToken ? (
                <div className={UserInfoContainerStyle}>
                    {/* 사용자 아이콘 */}
                    <div className="ml-8 flex items-center h-full xl:ml-0">
                        <Avatar
                            alt="Upload new avatar"
                            src={"../../../public/image.png"}
                            sx={{ width: "100%", height: "100%" }}
                            className="h-full w-auto"
                        />
                    </div>
                    <div className="flex flex-row ml-8 w-full xl:ml-0 xl:flex-col">
                        <div className="flex-1 flex flex-col justify-center">
                            <ul className="flex flex-col gap-3">
                                <li className="font-sourgummy font-semibold text-lg text-blue-700 xl:text-center">
                                    {userName || "이름"}
                                </li>

                                <li>이메일 : {userEmail} (User)</li>
                                <li>해결한 문제 수 : {solvedCount}</li>
                            </ul>
                        </div>
                    </div>
                </div>
            ) : (
                <div className={UserInfoContainerStyle}>
                    <div
                        className="flex flex-col gap-3 w-full xl:h-[400px] items-center justify-center
                    "
                    >
                        <h2
                            className="text-xl text-primary font-semibold font-sourgummy
                        xl:text-base"
                        >
                            코딩테스트 연습을 시작하세요!
                        </h2>
                        <Button
                            variant="contained"
                            onClick={() => {
                                navigate("/login", {
                                    preventScrollReset: false,
                                });
                            }}
                            className="w-24 h-10"
                        >
                            로그인
                        </Button>
                    </div>
                </div>
            )}
        </>
    );
};

export default UserInfo;
