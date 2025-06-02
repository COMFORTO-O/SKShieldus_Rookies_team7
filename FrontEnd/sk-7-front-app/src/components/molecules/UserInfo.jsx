import { Avatar, Button } from "@mui/material";
import useAuthStore from "../../store/useAuthStore";
import { useNavigate } from "react-router-dom";
import getUserInfo from "../../api/getUserInfo";
import { useEffect, useState } from "react";

const UserInfoContainerStyle =
    "mx-14 h-36 flex flex-row border-solid border-2 rounded-lg xl:mx-0 xl:flex-col xl:h-full ";

// {
//   "status": 0,
//   "code": "string",
//   "message": "string",
//   "data": {
//     "name": "string",
//     "email": "string",
//     "solvedProblems": [
//       {
//         "problemTitle": "string",
//         "completeDate": "2025-06-02T00:19:53.290Z"
//       }
//     ]
//   }
// 임시 데이터
const memberInfo = {
    message: "success",
    data: {
        name: "홍길동",
        email: "tester@test.com",
        solvedProblems: [
            {
                problemTitle: "string",
                completeDate: "2025-06-02T00:19:53.290Z",
            },
        ],
    },
};

const UserInfo = () => {
    const { isLoggedIn } = useAuthStore();
    const [userInfo, setUserInfo] = useState(null);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn) {
            // 유저 정보 가져오기
            const fetchUserInfo = async () => {
                try {
                    const response = await getUserInfo();
                    setUserInfo(response?.data || null);
                } catch (e) {
                    setError(
                        e?.message || "사용자 정보를 불러오지 못했습니다."
                    );
                    setUserInfo(null);
                }
            };
            fetchUserInfo();
        }
    }, [isLoggedIn]);

    return (
        <>
            {isLoggedIn ? (
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
                                    {memberInfo?.data?.name || "이름"}
                                </li>
                                <li>점수:</li>
                                <li>관리자 or 사용자</li>
                            </ul>
                        </div>
                        <div className="flex-1 flex items-center justify-center">
                            기타 정보
                        </div>
                    </div>
                </div>
            ) : (
                <div className={UserInfoContainerStyle}>
                    <div
                        className="flex flex-col gap-3 w-full items-center justify-center
                    xl:h-full"
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
