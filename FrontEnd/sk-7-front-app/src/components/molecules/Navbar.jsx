import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";
import useModalStore from "../../store/useModalStore";

const Navbar = () => {
    // 로그인 유무 ( 로컬 스토리지, 쿠키 )
    const { isLoggedIn } = useAuthStore();

    // Modal 상태 가져오기
    const { modalOpen, openModal, closeModal, toggleModal } = useModalStore();

    const navigate = useNavigate();

    return (
        <div className="bg-base-100 h-14 flex justify-between border-b-2">
            {/* 아이콘 */}
            <p>아이콘</p>
            {/* 네비게이션 바 */}
            <nav className="h-full flex gap-4 items-center text-black pr-4 font-sourgummy">
                <Link to="/">홈</Link>
                {isLoggedIn ? (
                    <>
                        <button
                            to="/info"
                            className="rounded-md w-20 h-7 text-center bg-primary text-base-100 hover:bg-hoverButton-900"
                            onClick={toggleModal}
                        >
                            내 정보
                        </button>
                    </>
                ) : (
                    <>
                        <Link
                            to="/login"
                            className="rounded-md w-16 h-7 text-center bg-secondary text-black border-solid border-2 hover:bg-primary hover:text-white transition"
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
