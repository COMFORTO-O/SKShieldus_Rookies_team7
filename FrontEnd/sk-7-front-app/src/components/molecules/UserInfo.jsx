import { Avatar, Button } from "@mui/material";
import useAuthStore from "../../store/useAuthStore";
import { useNavigate } from "react-router-dom";
import getUserInfo from "../../api/getUserInfo";
import { useEffect, useState } from "react";

const UserInfo = () => {
    const {
        isLoggedIn,
        accessToken,
        userName,
        userEmail, // 이메일 정보는 그대로 유지
        setName,
        setEmail,
        setLogout,
    } = useAuthStore();

    const [solvedCount, setSolvedCount] = useState(0);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    //맞은 갯수
    const [passCount, setPassCount] = useState(0);
    useEffect(() => {
        if (isLoggedIn) {
            const fetchUserInfo = async () => {
                try {
                    const data = await getUserInfo();
                    setName(data.member.name);
                    setSolvedCount(data.submissions.totalElements);
                    setEmail(data.member.email);
                    const passed = data.submissions.content.filter(
                        (item) => item.pass === true
                    ).length;
                    setPassCount(passed);
                } catch (e) {
                    setError(
                        e?.message || "사용자 정보를 불러오지 못했습니다."
                    );
                    setName(null);
                    setLogout();
                }
            };
            fetchUserInfo();
        }
    }, [isLoggedIn, setLogout, setEmail, setName]);

    return (
        <>
            {isLoggedIn && accessToken ? (
                // 로그인 상태
                // h-full 제거, flex-shrink-0 추가하여 내용만큼만 공간 차지하도록 함
                <div className="p-4 flex flex-col items-center bg-white rounded-xl justify-start text-sm flex-shrink-0">
                    {/* 사용자 아바타 */}
                    <div className="flex-shrink-0 mb-3 mt-2">
                        <Avatar
                            alt={userName || "User Avatar"}
                            src={"/image.png"}
                            sx={{
                                width: 70,
                                height: 70,
                                border: "2px solid",
                                borderColor: "blue.500",
                                boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                            }}
                        />
                    </div>

                    {/* 사용자 정보 텍스트 */}
                    <div className="flex flex-col items-center text-center flex-grow-0 mb-3">
                        <ul className="space-y-1">
                            <li className="font-bold text-xl text-blue-700 leading-snug">
                                {userName || "사용자 이름"}
                            </li>
                            <li className="text-gray-600 text-xs">
                                <span className="font-semibold">이메일:</span>{" "}
                                {userEmail}
                            </li>
                            <li className="text-gray-600 text-xs">
                                <span className="font-semibold">
                                    시도한 문제 수:
                                </span>{" "}
                                <span className="font-extrabold text-blue-600 text-base">
                                    {solvedCount}
                                </span>{" "}
                                문제
                            </li>
                            <li className="text-gray-600 text-xs">
                                <span className="font-semibold">
                                    맞힌 문제 수:
                                </span>{" "}
                                <span className="font-extrabold text-green-600 text-base">
                                    {passCount}
                                </span>{" "}
                                문제
                            </li>
                            {error && (
                                <li className="text-red-500 text-xs mt-1">
                                    {error}
                                </li>
                            )}
                        </ul>
                    </div>
                    {/* 랭킹 정보 */}
                    <div className="w-full mt-3">
                        <div className="bg-blue-50 rounded-md p-2 text-center text-blue-700 font-bold text-sm shadow-sm">
                            현재 랭킹:{" "}
                            <span className="text-blue-800 text-base">
                                상위 10%
                            </span>
                        </div>
                    </div>
                </div>
            ) : (
                // 로그아웃 상태 (로그인 유도)
                // h-full 제거, flex-shrink-0 추가하여 내용만큼만 공간 차지하도록 함
                <div className="p-4 flex flex-col items-center justify-center bg-white rounded-xl shadow-md flex-shrink-0">
                    <h2 className="text-xl sm:text-2xl font-extrabold text-gray-800 mb-4 leading-tight">
                        코딩 테스트 연습을 <br /> 지금 바로 시작하세요!
                    </h2>
                    <Button
                        variant="contained"
                        onClick={() => {
                            navigate("/login", {
                                preventScrollReset: false,
                            });
                        }}
                        sx={{
                            backgroundColor: "#2563EB",
                            "&:hover": {
                                backgroundColor: "#1E40AF",
                            },
                            borderRadius: "0.5rem",
                            boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
                            padding: "0.6rem 1.8rem",
                            fontSize: "1rem",
                            fontWeight: "bold",
                            textTransform: "none",
                            transition: "all 0.3s ease-out",
                        }}
                        className="mt-3 transform hover:scale-105"
                    >
                        로그인
                    </Button>
                </div>
            )}
        </>
    );
};

export default UserInfo;
