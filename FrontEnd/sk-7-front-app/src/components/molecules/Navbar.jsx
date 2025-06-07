import { Link } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";
import useModalStore from "../../store/useModalStore";
import logo from "../../../public/logo.png";

const Navbar = () => {
    const { isLoggedIn } = useAuthStore();
    const { infoModalOpen, openInfoModal, closeInfoModal } = useModalStore();

    return (
        <div className="bg-base-100 h-14 flex justify-between items-center border-b-2 px-4">
            {/* 로고 (홈 링크) */}
            <Link to="/" className="flex items-center">
                <img
                    src={logo}
                    alt="로고"
                    className="h-10 w-auto object-contain"
                />
            </Link>

            {/* 네비게이션 바 */}
            <nav className="flex gap-4 items-center text-black font-sourgummy">
                <Link to="/">홈</Link>
                {isLoggedIn ? (
                    <>
                        <Link to="/helpRoomList">방 목록</Link>
                        <button
                            className="rounded-md w-20 h-7 text-center bg-primary text-base-100 hover:bg-hoverButton-900"
                            onClick={
                                infoModalOpen ? closeInfoModal : openInfoModal
                            }
                        >
                            내 정보
                        </button>
                    </>
                ) : (
                    <>
                        <Link
                            to="/login"
                            className="rounded-md w-16 h-7 text-center bg-blue-500 text-white border-solid border-2 hover:bg-blue-700 transition"
                        >
                            로그인
                        </Link>
                    </>
                )}
            </nav>
        </div>
    );
};

export default Navbar;
