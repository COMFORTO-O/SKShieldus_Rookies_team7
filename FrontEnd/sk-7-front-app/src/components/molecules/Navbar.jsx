import { Link } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";
import useModalStore from "../../store/useModalStore";
import logo from "../../../public/logo.png";

const Navbar = () => {
    const { isLoggedIn } = useAuthStore();
    const { infoModalOpen, openInfoModal, closeInfoModal } = useModalStore();

    return (
        <div className="bg-white shadow-sm h-16 flex justify-between items-center px-6 border-b border-gray-200">
            {/* 로고 (홈 링크) */}
            <Link to="/" className="flex items-center py-2">
                <img
                    src={logo}
                    alt="로고"
                    className="h-10 w-auto object-contain"
                />
            </Link>

            {/* 네비게이션 바 */}
            <nav className="flex items-center space-x-6 text-gray-700 font-semibold">
                <Link to="/" className="hover:text-primary-focus transition-colors duration-200">홈</Link>
                {isLoggedIn ? (
                    <>
                        <Link to="/helpRoomList" className="hover:text-primary-focus transition-colors duration-200">방 목록</Link>
                        <button
                            className="px-4 py-2 rounded-md bg-primary text-white hover:bg-primary-dark transition-colors duration-200 text-sm"
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
                            className="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700 transition-colors duration-200 text-sm"
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