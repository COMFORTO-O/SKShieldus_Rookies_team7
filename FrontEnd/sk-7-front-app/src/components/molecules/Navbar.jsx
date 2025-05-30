import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../../store/useAuthStore";

const Navbar = () => {
    // 로그인 유무 ( 로컬 스토리지, 쿠키 )
    const { isLoggedIn } = useAuthStore();

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
                        <Link
                            to="/info"
                            className="rounded-md w-20 h-7 text-center bg-primary text-base-100 hover:bg-hoverButton-900"
                            onClick={() => {
                                navigate("/info");
                            }}
                        >
                            내 정보
                        </Link>
                        <button
                            className="rounded-md w-20 h-7 text-center bg-red-400 text-white ml-2 hover:bg-red-600 transition"
                            onClick={() => {
                                // 로그아웃 처리 (예: 토큰 삭제, 상태 변경)
                                localStorage.removeItem("accessToken");
                                window.location.reload();
                            }}
                        >
                            로그아웃
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
