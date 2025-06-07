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
        userEmail,
        setName,
        setEmail,
        setLogout,
    } = useAuthStore();

    // solvedCount 초기값을 0으로 유지
    const [solvedCount, setSolvedCount] = useState(0);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn) {
            const fetchUserInfo = async () => {
                try {
                    const data = await getUserInfo();
                    setName(data.member.name);
                    // submissions 객체의 totalElements 사용
                    setSolvedCount(data.submissions.totalElements);
                    setEmail(data.member.email);
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
                <div className="p-6 flex flex-col items-center bg-white rounded-xl h-full justify-between">
                    {/* 사용자 아바타 */}
                    <div className="flex-shrink-0 mb-6 mt-4">
                        <Avatar
                            alt={userName || "User Avatar"}
                            src={"/image.png"}
                            sx={{
                                width: 100,
                                height: 100,
                                border: '4px solid',
                                borderColor: 'blue.500',
                                boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
                            }}
                        />
                    </div>

                    {/* 사용자 정보 텍스트 */}
                    <div className="flex flex-col items-center text-center flex-grow mb-6">
                        <ul className="space-y-3">
                            <li className="font-bold text-3xl text-blue-700 leading-snug">
                                {userName || "사용자 이름"}
                            </li>
                            <li className="text-gray-600 text-md">
                                <span className="font-semibold">이메일:</span> {userEmail}
                            </li>
                            <li className="text-gray-600 text-md">
                                <span className="font-semibold">제출한 문제 수:</span>{" "} {/* 텍스트 수정 */}
                                <span className="font-extrabold text-blue-600 text-lg">
                                    {solvedCount}
                                </span>{" "}
                                문제
                            </li>
                            {error && (
                                <li className="text-red-500 text-sm mt-3">
                                    {error}
                                </li>
                            )}
                        </ul>
                    </div>
                    {/* 랭킹 정보 (예시, 필요 시 추가) */}
                    <div className="w-full">
                        <div className="bg-blue-50 rounded-md p-3 text-center text-blue-700 font-bold text-lg shadow-sm">
                            현재 랭킹: <span className="text-blue-800 text-xl">상위 10%</span> {/* 예시 데이터 */}
                        </div>
                    </div>
                </div>
            ) : (
                // 로그아웃 상태 (로그인 유도)
                <div className="p-6 flex flex-col items-center justify-center h-full text-center bg-white rounded-xl shadow-md">
                    <h2 className="text-2xl sm:text-3xl font-extrabold text-gray-800 mb-6 leading-tight">
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
                            backgroundColor: '#2563EB',
                            '&:hover': {
                                backgroundColor: '#1E40AF',
                            },
                            borderRadius: '0.75rem',
                            boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)',
                            padding: '0.9rem 2.5rem',
                            fontSize: '1.25rem',
                            fontWeight: 'bold',
                            textTransform: 'none',
                            transition: 'all 0.3s ease-out',
                        }}
                        className="mt-6 transform hover:scale-105"
                    >
                        로그인
                    </Button>
                </div>
            )}
        </>
    );
};

export default UserInfo;