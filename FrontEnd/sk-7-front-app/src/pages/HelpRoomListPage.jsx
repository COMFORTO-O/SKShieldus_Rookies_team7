import axios from "axios";
import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";
import RoomItem from "../components/molecules/RoomItem";
import UserInfo from "../components/molecules/UserInfo";

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
                    console.log("Fetched rooms:", response.data);
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
            console.log(
                `Navigating to JoinRoomPage for problemId: ${roomClicked.problemId}, to join roomId: ${roomClicked.id}`
            );
            console.log(
                `방 데이터 : \nID: ${roomClicked.id}\nProblemID: ${roomClicked.problemId}`
            );
            navigate(`/join/${roomClicked.problemId}`, {
                // URL은 problemId를 사용
                state: { roomToJoinData: roomClicked },
            });
        },
        [navigate]
    );

    return (
        <>
            <div>
                <div>
                    <h1 className="text-2xl font-bold mb-6 text-gray-700">
                        참여 가능한 도움방 목록
                    </h1>
                    {isLoggedIn && roomList.length > 0 ? (
                        <div
                            className="
                                bg-base-100
                                grid
                                gap-4
                                min-h-0
                                grid-cols-1
                                sm:grid-cols-2
                                lg:grid-cols-3
                                xl:grid-cols-4
                            "
                        >
                            {roomList.map((room) => (
                                <RoomItem
                                    key={room.id}
                                    id={room.id}
                                    title={room.title}
                                    owner={room.owner}
                                    problemId={room.problemId}
                                    onJoin={() => handleJoinRoom(room)}
                                />
                            ))}
                        </div>
                    ) : isLoggedIn && roomList.length === 0 ? (
                        <p className="text-gray-500">
                            현재 참여 가능한 방이 없습니다.
                        </p>
                    ) : (
                        <p className="text-gray-500">
                            로그인 후 방 목록을 볼 수 있습니다.
                        </p>
                    )}
                </div>
            </div>
        </>
    );
}
