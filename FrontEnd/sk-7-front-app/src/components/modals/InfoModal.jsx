import { useNavigate } from "react-router-dom";
import useModalStore from "../../store/useModalStore";
import { Avatar } from "@mui/material";
import { useEffect, useRef, useState } from "react";
import useAuthStore from "../../store/useAuthStore";
import getUserInfo from "../../api/getUserInfo";

// 유저 정보 Modal
const InfoModal = () => {
    const {
        isLoggedIn,
        userName,
        userEmail, // 이메일 정보는 그대로 유지
        setName,
        setEmail,
        setLogout,
    } = useAuthStore();

    const [error, setError] = useState("");
    const { closeInfoModal } = useModalStore();

    const navigate = useNavigate();
    const modalRef = useRef(null);

    useEffect(() => {
        if (isLoggedIn) {
            const fetchUserInfo = async () => {
                try {
                    const data = await getUserInfo();
                    setName(data.member.name);
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

    // 바깥 클릭 시 모달 닫기
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (modalRef.current && !modalRef.current.contains(e.target)) {
                closeInfoModal();
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () =>
            document.removeEventListener("mousedown", handleClickOutside);
    }, [closeInfoModal]);

    const handleInfoClick = (e) => {
        e.preventDefault();
        closeInfoModal();

        navigate("/info");
    };
    return (
        <div
            className="fixed mt-2 mr-5 right-0 z-50 flex bg-secondary h-[300px] w-[300px] rounded-lg border-solid border-2"
            style={{
                animation: "modalDropFade 0.4s cubic-bezier(0.4,0,0.2,1)",
            }}
            ref={modalRef}
        >
            <div className="flex flex-col w-full h-full bg-white ">
                <p className="ml-5 mt-5 font-sourgummy font-bold">내 정보</p>
                <Avatar
                    alt="Upload new avatar"
                    src={"../../../public/image.png"}
                    sx={{ width: "30%", height: "30%" }}
                    className="ml-5 h-full w-auto"
                />
                <div className="flex-1 flex flex-col gap-2">
                    {error ? (
                        <div className="text-red-500 text-sm">{error}</div>
                    ) : (
                        <>
                            <h1>이름: {userName}</h1>
                            <h1>이메일: {userEmail}</h1>
                        </>
                    )}
                </div>
                <button
                    className="bg-white flex justify-center items-center h-12 border-t-2 font-sourgummy"
                    onClick={handleInfoClick}
                >
                    마이 페이지
                </button>
                <button
                    className="h-12 text-center text-red-600 bg-white border-t-2"
                    onClick={() => {
                        // 로그아웃 처리 (예: 토큰 삭제, 상태 변경)
                        localStorage.removeItem("accessToken");
                        navigate("/", { replace: true });
                        window.location.reload();
                    }}
                >
                    로그아웃
                </button>
            </div>
        </div>
    );
};

export default InfoModal;
