import { Link } from "react-router-dom";

const Navbar = () => {
    // 로그인 유무 ( 로컬 스토리지, 쿠키 )

    return (
        <div className="h-14 flex justify-between border-b-2">
            {/* 아이콘 */}
            <p>아이콘</p>
            {/* 네비게이션 바 */}
            <nav className="h-full flex gap-4 items-center bg-base-100 text-black pr-4">
                <Link to="/">메인</Link>
                <Link to="/info">내 정보</Link>
                <Link
                    to="/login"
                    className="rounded-md w-16 h-7 text-center bg-secondary text-black border-solid border-2 hover:bg-hoverButton-400"
                >
                    로그인
                </Link>
                <Link
                    to="/register"
                    className="rounded-md w-20 h-7 text-center bg-primary text-base-100 hover:bg-hoverButton-900"
                >
                    회원가입
                </Link>
            </nav>
        </div>
    );
};

export default Navbar;
