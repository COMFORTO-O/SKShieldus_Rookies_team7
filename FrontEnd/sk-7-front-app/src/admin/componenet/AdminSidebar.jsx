import { FaHome, FaUsers, FaFileCode, FaTags } from "react-icons/fa";
import { Link, useLocation } from "react-router-dom";

const AdminSidebar = () => {
    const location = useLocation();

    const navItems = [
        { to: "/admin", icon: <FaHome size={18} />, label: "Dashboard" },
        { to: "/admin/user", icon: <FaUsers size={18} />, label: "User" },
        { to: "/admin/problem", icon: <FaFileCode size={18} />, label: "Problem" },
        { to: "/admin/category", icon: <FaTags size={18} />, label: "Category" },
    ];

    return (
        <div className="w-64 bg-gray-800 text-white shadow-xl p-4 flex flex-col">

            <div className="py-6 px-4 mb-6 border-b border-gray-700">
                <h1 className="text-2xl font-extrabold text-blue-400">Admin Panel</h1>
            </div>

            <nav className="flex flex-col gap-3 flex-grow">
                {navItems.map((item) => (
                    <Link
                        key={item.to}
                        to={item.to}

                        className={`
                            flex items-center gap-3 p-3 rounded-lg
                            transition-all duration-200 ease-in-out
                            ${location.pathname === item.to
                            ? "bg-blue-600 text-white shadow-md font-semibold"
                            : "text-gray-300 hover:bg-gray-700 hover:text-white"
                        }
                        `}
                    >
                        {item.icon} <span className="ml-1">{item.label}</span>
                    </Link>
                ))}
            </nav>

            {/* 하단 공간 (필요시 추가 콘텐츠용) */}
            <div className="mt-auto pt-6 text-sm text-gray-500 border-t border-gray-700">
                <p>&copy; 2025 Admin. All rights reserved.</p>
            </div>
        </div>
    );
};

export default AdminSidebar;