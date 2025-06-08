import React, {
    useRef,
    useEffect,
    useState,
    useCallback,
    forwardRef,
    useImperativeHandle,
} from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import useAuthStore from "../../store/useAuthStore";
import { useNavigate } from "react-router-dom";
import useCodeStore from "../../store/useCodeStore";
import RoleStore from "../../store/RoleStore";
import editByStore from "../../store/editByStore";

const ChatJoinComponent = forwardRef(({ roomId }, ref) => {
    // 코드 상태 (Global)
    const { setCode } = useCodeStore();

    // client 참조
    const stompClientRef = useRef(null);
    // 연결 on off
    const [isConnected, setIsConnected] = useState(false); // 초기값 false
    // 연결 상태
    // 초기 connectionStatus는 initialRoomIdToJoin 유무에 따라 설정
    const [connectionStatus, setConnectionStatus] = useState("방 입장");
    // 받은 메세지
    const [receivedMessages, setReceivedMessages] = useState([]);
    // 현재 연결된 방 ID
    const [currentRoomId, setCurrentRoomId] = useState("");

    // 네비게이터
    const navigate = useNavigate();

    // 구독 상태 Ref
    const chatSubscriptionRef = useRef(null);
    const codeUpdateSubscriptionRef = useRef(null);
    const initialSettingsSubscriptions = useRef({
        // subscribeInitialSetting 내부 구독 관리용
        initCode: null, // 초기 코드 받아오는 구독
        chatHistory: null, // 채팅 내역
        roomDeleted: null, // 방 닫힘
        members: null, // 참여자 목록
        kick: null, // 추방
    });

    // 메세지 인풋
    const [messageInput, setMessageInput] = useState("");
    // 채팅 내역 수정을 위한 참조
    const chatAreaRef = useRef(null);

    // 현재 코드 수정 중인 참여자
    const { editingBy, setEditingBy } = editByStore();
    // 수정 타이머 참조
    const editingTimerRef = useRef(null);

    // 참여자 ( id, role )
    const [memberMap, setMemberMap] = useState({});
    // OWNER, CODE_EDIT, CHAT_ONLY 등 (Global)
    const { role: myRole, setRole } = RoleStore();
    // 사용자 Email
    const [currentUserEmail, setCurrentUserEmail] = useState("");
    // 소유자 Email
    const [ownerEmail, setOwnerEmail] = useState("");

    // 로그인 상태, 사용자 이름, 이메일 전역 변수에서 가져오기
    const { isLoggedIn, userEmail } = useAuthStore();

    //====================================================== 여기까지 상태

    // 외부에서 호출할 수 있도록 메서드 노출
    useImperativeHandle(ref, () => ({
        sendCodeUpdateFromParent: (newCode) => {
            // 코드 업데이트를 서버로 전송 (Client 가 존재하고 현재 연결이 된 상태인 경우)
            if (stompClientRef.current?.active && currentRoomId) {
                if (
                    myRole === "CHAT_AND_EDIT" ||
                    myRole === "OWNER" ||
                    currentUserEmail === ownerEmail
                ) {
                    // 코드 업데이트 구독
                    stompClientRef.current.publish({
                        destination: `/app/code.update.${currentRoomId}`,
                        body: JSON.stringify({ code: newCode }),
                    });
                } else {
                    console.warn("코드 수정 권한이 없습니다.");
                }
            }
        },
    }));

    // <---- 구독 해제 유틸리티 ---->
    const unsubscribe = useCallback((subscriptionRef) => {
        // 구독이 존재하면 해당 구독 해제
        if (subscriptionRef.current) {
            try {
                subscriptionRef.current.unsubscribe();
            } catch (e) {
                console.warn("구독 해제 중 오류:", e, subscriptionRef.current);
            }
            subscriptionRef.current = null;
        }
    }, []);
    const unsubscribeAllRoomSpecific = useCallback(() => {
        console.log("모든 방 특정 구독 해제 시도...");
        unsubscribe(chatSubscriptionRef);
        unsubscribe(codeUpdateSubscriptionRef);

        // subscribeInitialSetting에서 생성된 Topic 구독 명시적 해제
        if (initialSettingsSubscriptions.current.roomDeleted)
            initialSettingsSubscriptions.current.roomDeleted.unsubscribe();
        initialSettingsSubscriptions.current.roomDeleted = null;
        if (initialSettingsSubscriptions.current.members)
            initialSettingsSubscriptions.current.members.unsubscribe();
        initialSettingsSubscriptions.current.members = null;

        console.log("방 특정 구독 해제 완료.");
    }, [unsubscribe]);

    // <---- 채팅 메시지 구독 ---->
    const subscribeToChatMessages = useCallback(
        // roomId 를 얻은 후에
        (roomId) => {
            if (!stompClientRef.current?.active || !roomId) {
                console.warn(
                    "채팅 구독 불가 : 클라이언트 비활성 또는 방 ID 없음."
                );
                unsubscribe(chatSubscriptionRef);
                return;
            }
            unsubscribe(chatSubscriptionRef); // 이전 구독이 있다면 확실히 해제

            const chatDestination = `/topic/chatroom.${roomId}`;
            console.log(`${chatDestination} 채팅 구독 시도.`);
            chatSubscriptionRef.current = stompClientRef.current.subscribe(
                chatDestination,
                (message) => {
                    try {
                        const receivedMessage = JSON.parse(message.body);

                        receivedMessage.isMine =
                            receivedMessage.sender === currentUserEmail;
                        console.log("채팅 메시지 수신:", receivedMessage);
                        setReceivedMessages((prevMessages) => [
                            ...prevMessages,
                            receivedMessage,
                        ]);
                    } catch (error) {
                        console.error(
                            "채팅 메시지 파싱 오류:",
                            error,
                            message.body
                        );
                    }
                }
            );
            console.log(`[${roomId}] 채팅 메시지 구독 완료.`);
        },
        [currentUserEmail, unsubscribe]
    );

    // <---- 코드 업데이트 구독 ---->
    const subscribeToCodeUpdates = useCallback(
        (roomId) => {
            if (!stompClientRef.current?.active || !roomId) {
                console.warn(
                    "코드 업데이트 구독 불가: 클라이언트 비활성 또는 방 ID 없음."
                );
                unsubscribe(codeUpdateSubscriptionRef);
                return;
            }
            unsubscribe(codeUpdateSubscriptionRef); // 이전 구독이 있다면 확실히 해제

            const codeDestination = `/topic/code.${roomId}`;
            console.log(`${codeDestination} 코드 업데이트 구독 시도.`);
            codeUpdateSubscriptionRef.current =
                stompClientRef.current.subscribe(codeDestination, (message) => {
                    try {
                        const payload = JSON.parse(message.body);
                        const newCode = payload.code;
                        const sender = payload.sender; // 이메일 형태를 기대

                        if (newCode !== undefined) setCode(newCode);

                        if (sender !== currentUserEmail) {
                            // 내가 보낸 업데이트는 알림 X
                            setEditingBy(sender); // 이름 또는 이메일 앞부분

                            if (editingTimerRef.current)
                                clearTimeout(editingTimerRef.current);
                            editingTimerRef.current = setTimeout(
                                () => setEditingBy(null),
                                1000
                            );
                        }
                    } catch (error) {
                        console.error(
                            "코드 업데이트 메시지 파싱 오류:",
                            error,
                            message.body
                        );
                    }
                });
            console.log(`[${roomId}] 코드 업데이트 구독 완료.`);
        },
        [currentUserEmail, setCode, unsubscribe, setEditingBy]
    );

    // <---- 연결 초기 설정 구독 ---->
    const subscribeInitialSetting = useCallback(
        (roomId) => {
            if (!stompClientRef.current?.active || !roomId) {
                console.warn(
                    "초기 설정 구독 불가: 클라이언트 비활성 또는 방 ID 없음."
                );
                return;
            }
            console.log(`[${roomId}] 초기 설정 구독 시도.`);

            // 안전하게 구독 해제하고 null로 만드는 헬퍼 함수
            const safeUnsubscribe = (subscriptionRefProperty) => {
                if (
                    initialSettingsSubscriptions.current[
                        subscriptionRefProperty
                    ]
                ) {
                    try {
                        initialSettingsSubscriptions.current[
                            subscriptionRefProperty
                        ].unsubscribe();
                    } catch (e) {
                        console.warn(
                            `${subscriptionRefProperty} 구독 해제 중 오류`,
                            e,
                            initialSettingsSubscriptions.current[
                                subscriptionRefProperty
                            ]
                        );
                    }
                    initialSettingsSubscriptions.current[
                        subscriptionRefProperty
                    ] = null;
                }
            };

            // 이전 구독들 해제 (사용자 큐 포함)
            safeUnsubscribe("initCode");
            safeUnsubscribe("chatHistory");
            safeUnsubscribe("kick");
            safeUnsubscribe("roomDeleted");
            safeUnsubscribe("members");

            // User-specific queue 구독 (STOMP 클라이언트 활성화 시 한 번만 해도 될 수 있음)
            initialSettingsSubscriptions.current.initCode =
                stompClientRef.current.subscribe(
                    "/user/queue/initCode",
                    (message) => {
                        try {
                            const latest = JSON.parse(message.body)?.code;
                            if (latest !== undefined) setCode(latest);
                            console.log("초기 코드 수신:", latest);
                        } catch (e) {
                            console.error("초기 코드 파싱 오류:", e);
                        }
                    }
                );

            initialSettingsSubscriptions.current.chatHistory =
                stompClientRef.current.subscribe(
                    "/user/queue/chatHistory",
                    (message) => {
                        try {
                            const history = JSON.parse(message.body);
                            setReceivedMessages(
                                Array.isArray(history) ? history : []
                            );
                            console.log("채팅 기록 수신:", history);
                        } catch (e) {
                            console.error("채팅 기록 파싱 오류:", e);
                        }
                    }
                );

            initialSettingsSubscriptions.current.kick =
                stompClientRef.current.subscribe(
                    "/user/queue/kick",
                    (message) => {
                        try {
                            const {
                                message: alertMsg,
                                roomId: kickedFromRoomId,
                            } = JSON.parse(message.body);

                            console.log(
                                `kickedRoomId: ${kickedFromRoomId}\ncurrent: ${roomId}`
                            );
                            if (kickedFromRoomId === roomId) {
                                // 현재 방에서 강퇴된 경우
                                alert(`강퇴 알림: ${alertMsg}`);
                                setCurrentRoomId("");
                                setConnectionStatus("강퇴됨");
                                navigate("/helpRoomList"); // 페이지 이동
                            }
                        } catch (e) {
                            console.error("강퇴 메시지 파싱 오류:", e);
                        }
                    }
                );

            // 방 특정 토픽 구독
            initialSettingsSubscriptions.current.roomDeleted =
                stompClientRef.current.subscribe(
                    `/topic/roomDeleted.${roomId}`,
                    (message) => {
                        alert(`방 삭제 알림: ${message.body}`);
                        setCurrentRoomId("");
                        setConnectionStatus("방 삭제됨 (새로운 방 요청 가능)");
                        if (myRole !== "OWNER") {
                            navigate("/helpRoomList");
                        }
                    }
                );

            initialSettingsSubscriptions.current.members =
                stompClientRef.current.subscribe(
                    `/topic/members.${roomId}`,
                    (message) => {
                        try {
                            const { members, owner } = JSON.parse(message.body);
                            console.log(
                                "멤버 목록 수신:",
                                members,
                                "방장:",
                                owner
                            );
                            setMemberMap(members || {});
                            setOwnerEmail(owner || "");
                            if (
                                currentUserEmail &&
                                members &&
                                members[currentUserEmail]
                            ) {
                                setRole(members[currentUserEmail]);
                            } else if (currentUserEmail === owner) {
                                setRole("OWNER"); // 방장 역할명은 백엔드와 일치 필요
                            } else {
                                setRole("CHAT_ONLY"); // 멤버 목록에 없거나 매칭 안되면 기본값
                            }
                        } catch (e) {
                            console.error("멤버 정보 파싱 오류:", e);
                        }
                    }
                );

            console.log(`[${roomId}] 초기 설정 구독 완료.`);

            // 초기 데이터 요청
            stompClientRef.current.publish({
                destination: `/app/code.latest.${roomId}`,
                body: "",
            });
            stompClientRef.current.publish({
                destination: `/app/chat.history.${roomId}`,
                body: "",
            });
            stompClientRef.current.publish({
                destination: `/app/room.enter.${roomId}`,
                body: "멤버 입장",
            }); // 입장 알림 (멤버 목록 즉시 갱신 트리거)

            setConnectionStatus("도움방 참여 완료"); // 모든 구독 및 요청 후 상태 변경
        },
        [
            setCode,
            currentUserEmail,
            navigate,
            setRole,
            myRole,
            setReceivedMessages,
            setConnectionStatus,
            setCurrentRoomId,
            setMemberMap,
            setOwnerEmail,
        ]
    ); // currentRoomId 의존성 추가 (kick 메시지 처리)

    // <---- 도움방 연결 해제 ---->
    const disconnectFromHelpRoom = useCallback(() => {
        console.log("도움방 연결 해제 시도...");
        if (stompClientRef.current?.active) {
            if (currentRoomId) {
                // 현재 방이 있을 때만 퇴장 메시지 전송
                stompClientRef.current.publish({
                    destination: `/app/room.leave.${currentRoomId}`,
                    body: "",
                });
            }
            stompClientRef.current.deactivate(); // onDisconnect 콜백이 트리거되어 나머지 정리
            console.log("STOMP 연결 해제 요청됨 (deactivate 호출)");
        } else {
            // 이미 비활성 상태이거나 stompClientRef가 null인 경우 수동 초기화
            console.log(
                "STOMP 클라이언트 비활성 또는 없음. 수동 상태 초기화 실행."
            );
            setIsConnected(false);
            setCurrentRoomId("");
            setReceivedMessages([]);
            unsubscribeAllRoomSpecific(); // 모든 구독 참조 정리
            stompClientRef.current = null; // 명시적 null 처리
            setConnectionStatus("방 입장");
            setMemberMap({});
            setOwnerEmail("");
            setRole("CHAT_ONLY");
        }
    }, [currentRoomId, unsubscribeAllRoomSpecific, setRole]);

    // ========================================================== 여기까지 구독 관련 기능

    // <---- 도움방 연결 ---->
    const connectToHelpRoom = useCallback(async () => {
        if (!isLoggedIn || !userEmail) {
            alert("로그인이 필요합니다. 로그인 후 다시 시도해주세요.");
            setConnectionStatus("로그인 필요");
            return;
        }

        setCurrentUserEmail(userEmail);

        // 이미 연결 시도 중이거나, 방 생성 중이면 중복 실행 방지
        if (
            connectionStatus === "연결 시도 중..." ||
            connectionStatus === "도움방 생성 중..."
        ) {
            console.log("이미 연결/생성 작업 진행 중입니다.");
            return;
        }

        // 새로운 STOMP 연결 생성
        setConnectionStatus("연결 시도 중...");
        const socket = new SockJS(`${import.meta.env.VITE_API_URL}/ws`);
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                /* Authorization: `Bearer ${localStorage.getItem("accessToken")}`, */
            },
            debug: (str) => console.log("STOMP Debug: ", str),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: async (frame) => {
                console.log("STOMP 서버에 연결되었습니다:", frame);
                stompClientRef.current = client;
                setIsConnected(true);

                // 연결 성공 후 구독
                setCurrentRoomId(roomId);
            },
            onStompError: (frame) => {
                console.error(
                    "STOMP 프로토콜 오류:",
                    frame.headers["message"],
                    frame.body
                );
                setIsConnected(false);
                setCurrentRoomId("");
                setConnectionStatus("연결 실패 (다시 요청)");
                stompClientRef.current = null;
            },
            onWebSocketError: (error) => {
                console.error("WebSocket 오류:", error);
                setIsConnected(false);
                setCurrentRoomId("");
                setConnectionStatus("WebSocket 오류 (다시 요청)");
                stompClientRef.current = null;
            },
            onDisconnect: () => {
                console.log(
                    "STOMP 서버와의 연결이 끊어졌습니다. (onDisconnect 콜백)"
                );
                setIsConnected(false);
                setCurrentRoomId("");
                setReceivedMessages([]);
                unsubscribeAllRoomSpecific(); // 모든 구독 참조 정리
                stompClientRef.current = null;
                setConnectionStatus("방 입장");
                setMemberMap({});
                setOwnerEmail("");
                setRole("CHAT_ONLY");
                setEditingBy(null);
            },
        });
        client.activate();
    }, [
        roomId,
        isLoggedIn,
        userEmail,
        connectionStatus,
        unsubscribeAllRoomSpecific,
        setRole,
        setEditingBy,
    ]);

    useEffect(() => {
        // 주 구독 관리 로직: currentRoomId, isConnected, stompClientRef.current.active 변경 시 실행
        const clientIsActive = stompClientRef.current?.active;
        console.log(
            `구독 관리 useEffect 실행: currentRoomId=${currentRoomId}, isConnected=${isConnected}, clientActive=${clientIsActive}`
        );

        if (currentRoomId && isConnected && clientIsActive) {
            console.log(
                `[EFFECT] RoomID ${currentRoomId}, 연결 활성. 모든 구독 설정 시도.`
            );
            // 구독 함수들은 useCallback으로 메모이징되어 의존성 배열에 포함 가능
            subscribeToChatMessages(currentRoomId);
            subscribeToCodeUpdates(currentRoomId);
            subscribeInitialSetting(currentRoomId); // 이 함수 내부에서 setConnectionStatus("도움방 참여 완료") 호출
            // 초기 구독 완료 후 방 입장 알리기
            stompClientRef.current.publish({
                destination: `/app/room.enter.${currentRoomId}`,
                body: "",
            });
        } else {
            // 방에 연결되지 않았거나, STOMP 연결이 끊어졌거나, 클라이언트가 비활성 상태인 경우
            console.log(
                `[useEffect] 구독 해제 조건 충족 또는 구독 설정 조건 미충족.`
            );
            // unsubscribeAllRoomSpecific(); // 여기서 호출하면, 방 생성 중 잠깐 currentRoomId가 없을 때도 해제될 수 있음
            // onDisconnect 또는 disconnectFromHelpRoom에서 주로 처리.
            // 단, STOMP는 연결되었으나 방에서 나간 경우(currentRoomId가 ""로 바뀜)는 여기서 해제 필요.
            if (!currentRoomId && isConnected && clientIsActive) {
                // 방에서 나갔지만 STOMP 연결은 유지된 경우
                console.log(
                    "[useEffect] 방에서 나감 (currentRoomId 없음), 방 특정 구독 해제."
                );
                unsubscribeAllRoomSpecific();
                setReceivedMessages([]);
                setMemberMap({});
                setOwnerEmail("");
                // setMyRole("CHAT_ONLY"); // 역할은 유지하거나 초기화 가능
                if (
                    connectionStatus !== "도움방 생성 중..." &&
                    connectionStatus !== "연결 시도 중..." &&
                    !connectionStatus.includes("강퇴됨") &&
                    !connectionStatus.includes("방 삭제됨")
                ) {
                    setConnectionStatus("도움방 참여 대기중"); // 또는 "도움방 해제"
                }
            }
        }
    }, [
        currentRoomId,
        isConnected,
        stompClientRef.current?.active, // stompClientRef.current.active 상태 변화 감지 중요!
        subscribeToChatMessages,
        subscribeToCodeUpdates,
        subscribeInitialSetting,
        unsubscribeAllRoomSpecific, // unsubscribeAllRoomSpecific도 useCallback으로 감싸져 있으므로 의존성 추가
        connectionStatus, // connectionStatus 변경에 따른 UI 메시지 업데이트 위해 포함
    ]);

    useEffect(() => {
        // 채팅창 자동 스크롤
        if (chatAreaRef.current) {
            chatAreaRef.current.scrollTop = chatAreaRef.current.scrollHeight;
        }
    }, [receivedMessages]);

    // <---- 버튼 및 액션 핸들러 ---->
    const handleHelpRequestClick = () => {
        if (!isLoggedIn) {
            alert("로그인이 필요합니다.");
            return;
        }
        if (currentRoomId && isConnected) {
            console.log("도움방 나가기 버튼 클릭");
            disconnectFromHelpRoom();
        } else if (!isConnected || (isConnected && !currentRoomId)) {
            // "도움방 생성 중..." 또는 "연결 시도 중..." 상태가 아닐 때만 connectToHelpRoom 호출
            if (
                connectionStatus !== "도움방 생성 중..." &&
                connectionStatus !== "연결 시도 중..."
            ) {
                console.log("방 참여 버튼 클릭");
                connectToHelpRoom();
            } else {
                console.log(
                    "이미 연결/생성 작업 진행 중입니다. 버튼 클릭 무시."
                );
            }
        } else {
            console.log("handleHelpRequestClick: 알 수 없는 상태", {
                currentRoomId,
                isConnected,
                connectionStatus,
            });
        }
    };

    // 메세지 전송
    const handleSendMessage = (e) => {
        e.preventDefault();
        if (
            messageInput.trim() &&
            stompClientRef.current?.active &&
            currentRoomId
        ) {
            try {
                stompClientRef.current.publish({
                    destination: `/app/chat.sendMessage.${currentRoomId}`,
                    body: JSON.stringify({
                        content: messageInput,
                        type: "CHAT",
                    }), // sender는 백엔드가 토큰으로 식별
                });
                setMessageInput("");
            } catch (error) {
                console.error("STOMP 메시지 전송 오류:", error);
            }
        }
    };

    // UI 렌더링 (이전 답변의 JSX 구조와 유사하게 구성)
    return (
        <div className="p-4 bg-gray-800 text-white rounded-lg shadow-xl w-full mx-auto my-5">
            {/* 제목 및 도움방 요청/나가기 버튼 */}
            <div className="flex flex-wrap gap-2 mb-3 items-center justify-between">
                <div className="font-bold text-xl">실시간 도움방</div>
                <button
                    onClick={handleHelpRequestClick}
                    className={`w-auto text-center px-3 py-1.5 rounded text-sm font-medium transition-colors
                        ${
                            currentRoomId && isConnected
                                ? "bg-red-600 hover:bg-red-500"
                                : connectionStatus === "도움방 생성 중..." ||
                                  connectionStatus === "연결 시도 중..."
                                ? "bg-yellow-500 hover:bg-yellow-400 cursor-not-allowed animate-pulse"
                                : "bg-blue-600 hover:bg-blue-500"
                        } text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-blue-400`}
                    disabled={
                        !isLoggedIn || // 로그인 안됐으면 비활성화
                        connectionStatus === "도움방 생성 중..." ||
                        connectionStatus === "연결 시도 중..."
                    }
                >
                    {!isLoggedIn
                        ? "로그인 필요"
                        : currentRoomId && isConnected
                        ? "도움방 나가기"
                        : connectionStatus}
                </button>
            </div>
            {currentRoomId && isConnected && (
                <span className="block text-xs text-gray-400 mb-2 text-right">
                    (방 ID: {currentRoomId})
                </span>
            )}

            {/* 코드 수정자 알림 */}
            {editingBy && (
                <p className="text-xs text-yellow-400 mb-2 animate-pulse text-center">
                    {editingBy === (userEmail || currentUserEmail.split("@")[0])
                        ? "(내)가"
                        : `${editingBy}님이`}{" "}
                    코드를 수정 중입니다...
                </p>
            )}

            {/* 채팅창 */}
            <div
                ref={chatAreaRef}
                className="bg-gray-900 overflow-y-auto p-3 rounded border border-gray-700 space-y-2 h-52 min-h-[13rem] flex flex-col mb-3 shadow-inner"
            >
                {/* 연결/참여 상태 메시지 */}
                {(!isConnected || !currentRoomId) &&
                    !(
                        connectionStatus === "도움방 생성 중..." ||
                        connectionStatus === "연결 시도 중..."
                    ) && (
                        <p className="text-xs text-gray-500 text-center p-1 self-center m-auto">
                            {!isLoggedIn
                                ? "로그인 후 도움방에 참여할 수 있습니다."
                                : connectionStatus.includes("강퇴됨")
                                ? "방에서 강퇴되었습니다. 새로운 방을 요청할 수 있습니다."
                                : connectionStatus.includes("방 삭제됨")
                                ? "방이 삭제되었습니다. 새로운 방을 요청할 수 있습니다."
                                : "도움방에 연결되지 않았습니다. '도움방 요청'을 눌러주세요."}
                        </p>
                    )}
                {(connectionStatus === "도움방 생성 중..." ||
                    connectionStatus === "연결 시도 중...") && (
                    <p className="text-xs text-gray-400 text-center p-1 self-center m-auto animate-pulse">
                        {connectionStatus}...
                    </p>
                )}

                {/* 채팅 메시지 목록 */}
                {receivedMessages.map((msg, index) => (
                    <div
                        key={msg.id || index} // 메시지에 고유 ID가 있다면 사용 (백엔드에서 제공)
                        className={`text-sm p-2 rounded max-w-[85%] break-words shadow ${
                            msg.sender === currentUserEmail // 백엔드에서 sender email을 보내준다고 가정
                                ? "bg-blue-700 self-end ml-auto" // 내 메시지
                                : "bg-gray-700 self-start mr-auto" // 다른 사람 메시지
                        }`}
                    >
                        {msg.sender &&
                            msg.sender !== currentUserEmail && ( // 다른 사람 메시지에만 발신자 표시
                                <span className="font-semibold text-purple-400 block text-xs mb-0.5">
                                    {msg.senderName || msg.sender.split("@")[0]}{" "}
                                    {/* 이름 또는 이메일 앞부분 */}
                                </span>
                            )}
                        <span className="text-gray-100 text-base whitespace-pre-wrap">
                            {msg.content}
                        </span>
                        {msg.timestamp && ( // 타임스탬프 표시
                            <span className="text-xs text-gray-500 block text-right mt-1">
                                {new Date(msg.timestamp).toLocaleTimeString(
                                    [],
                                    { hour: "2-digit", minute: "2-digit" }
                                )}
                            </span>
                        )}
                    </div>
                ))}
                {/* 채팅 참여 완료 후 메시지 없을 때 안내 */}
                {currentRoomId &&
                    isConnected &&
                    receivedMessages.length === 0 &&
                    connectionStatus === "도움방 참여 완료" && (
                        <p className="text-xs text-gray-400 text-center p-1 self-center m-auto">
                            채팅방에 참여했습니다. 첫 메시지를 보내보세요!
                        </p>
                    )}
            </div>

            {/* 메시지 입력 폼 */}
            <form
                onSubmit={handleSendMessage}
                className="mt-2 flex gap-2 items-center"
            >
                <input
                    type="text"
                    placeholder={
                        currentRoomId && isConnected
                            ? "메시지를 입력하세요..."
                            : "도움방에 먼저 참여하세요."
                    }
                    value={messageInput}
                    onChange={(e) => setMessageInput(e.target.value)}
                    className="flex-1 p-2 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-1 focus:ring-blue-500 shadow-sm"
                    disabled={!currentRoomId || !isConnected}
                />
                <button
                    type="submit"
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded text-white disabled:bg-gray-600 disabled:cursor-not-allowed transition-colors shadow-sm"
                    disabled={
                        !currentRoomId || !isConnected || !messageInput.trim()
                    }
                >
                    전송
                </button>
            </form>

            {/* 참여자 목록 */}
            {isConnected && currentRoomId && (
                <div className="mt-4 pt-3 border-t border-gray-700">
                    <h3 className="font-semibold text-md mb-2">
                        참여자 ({Object.keys(memberMap).length}명)
                        {myRole && (
                            <span className="text-xs text-gray-400 ml-2">
                                (나의 역할: {myRole})
                            </span>
                        )}
                    </h3>
                    {Object.keys(memberMap).length > 0 ? (
                        <ul className="space-y-1 text-sm text-gray-300 max-h-40 overflow-y-auto pr-1 custom-scrollbar">
                            {" "}
                            {/* custom-scrollbar는 별도 정의 필요 */}
                            {Object.entries(memberMap).map(([email, role]) => (
                                <li
                                    key={email}
                                    className="flex justify-between items-center p-1.5 bg-gray-750 rounded hover:bg-gray-700 transition-colors text-xs"
                                >
                                    <span
                                        className="truncate max-w-[45%]"
                                        title={email}
                                    >
                                        {email.split("@")[0]}{" "}
                                        {/* 이메일 앞부분만 표시 */}
                                        {email === ownerEmail && (
                                            <span className="text-yellow-400 text-xs ml-1">
                                                (방장)
                                            </span>
                                        )}
                                        {email === currentUserEmail && (
                                            <span className="text-green-400 text-xs ml-1">
                                                (나)
                                            </span>
                                        )}
                                    </span>
                                    <div className="flex items-center gap-1">
                                        <span className="px-1.5 py-0.5 bg-gray-600 rounded-full">
                                            {role}
                                        </span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-xs text-gray-500">
                            참여자 정보를 불러오는 중이거나 참여자가 없습니다.
                        </p>
                    )}
                </div>
            )}
        </div>
    );
});

export default ChatJoinComponent;
