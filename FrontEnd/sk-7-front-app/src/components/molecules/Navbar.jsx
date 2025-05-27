import { Link } from "react-router-dom";

const Navbar = () => {
    return (
        <nav className="h-16 flex justify-end gap-4 items-cente bg-base-100 text-black border-b-2 items-center">
            <Link to="/">메인</Link>
            <Link to="/login">로그인</Link>
        </nav>
    );
};

export default Navbar;
