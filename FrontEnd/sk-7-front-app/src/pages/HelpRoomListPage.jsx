import axios from "axios";
import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";
import RoomItem from "../components/molecules/RoomItem";

export default function HelpRoomListPage() {
    const { isLoggedIn, accessToken } = useAuthStore();
    const [roomList, setRoomList] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn && accessToken) {
            const fetchRooms = async () => {
                try {
                    const response = await axios.get(
                        "http://localhost:8080/api/rooms",
                        {
                            headers: {
                                /* Authorization: `Bearer ${accessToken}` */
                            },
                            withCredentials: true,
                        }
                    );

                    setRoomList(response.data || []);
                } catch (error) {
                    console.error("방 목록 불러오기 실패:", error);
                    setRoomList([]);
                }
            };
            fetchRooms();
        } else {
            setRoomList([]);
        }
    }, [isLoggedIn, accessToken]);

    // RoomItem 클릭 시 호출될 함수 - SolvePage로 이동
    const handleJoinRoom = useCallback(
        (roomClicked) => {
            if (!roomClicked || !roomClicked.problemId || !roomClicked.id) {
                console.error("Invalid room data for navigation:", roomClicked);
                alert("잘못된 방 정보입니다.");
                return;
            }

            navigate(`/join/${roomClicked.problemId}`, {
                // URL은 problemId를 사용
                state: { roomToJoinData: roomClicked },
            });
        },
        [navigate]
    );

    return (
        <div className="min-h-0 h-full bg-primary py-10 px-4">
            <div className="m-16">
                <div className="flex items-center justify-between mb-8">
                    <h1 className="text-3xl font-bold text-secondary tracking-tight drop-shadow">
                        참여 가능한 도움방 목록
                    </h1>
                    <button
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg shadow hover:bg-blue-700 transition font-semibold"
                        onClick={() => navigate("/")}
                    >
                        메인으로
                    </button>
                </div>
                <div className="bg-white rounded-2xl shadow-lg p-8">
                    {isLoggedIn && roomList.length > 0 ? (
                        <div
                            className="
                            grid
                            gap-6
                            min-h-0
                            grid-cols-1
                            sm:grid-cols-2
                            lg:grid-cols-3
                            xl:grid-cols-4
                        "
                        >
                            {roomList.map((room) => (
                                <div
                                    key={room.id}
                                    className="transition-transform hover:-translate-y-1 hover:shadow-xl"
                                >
                                    <RoomItem
                                        id={room.id}
                                        title={room.title}
                                        owner={room.owner}
                                        problemId={room.problemId}
                                        onJoin={() => handleJoinRoom(room)}
                                    />
                                </div>
                            ))}
                        </div>
                    ) : isLoggedIn && roomList.length === 0 ? (
                        <div className="flex flex-col items-center py-16">
                            <p className="text-gray-500 text-lg font-medium">
                                현재 참여 가능한 방이 없습니다.
                            </p>
                        </div>
                    ) : (
                        <div className="flex flex-col items-center py-16">
                            <p className="text-gray-500 text-lg font-medium">
                                로그인 후 방 목록을 볼 수 있습니다.
                                <button
                                    className="px-4 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 transition font-bold"
                                    onClick={() => navigate("/login")}
                                >
                                    로그인 하러가기
                                </button>
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
