import { FaHome, FaUsers, FaCog } from "react-icons/fa";
import { Link } from "react-router-dom";

const AdminSidebar = () => {
    return (
        <div className="w-64 bg-white shadow-lg p-4">
            <h1 className="text-xl font-bold mb-6">Admin</h1>
            <nav className="flex flex-col gap-4">
                <Link
                    to="/dashboard"
                    className="flex items-center gap-2 text-gray-700 hover:text-blue-500"
                >
                    <FaHome size={18} /> Dashboard
                </Link>
                <Link
                    to="/users"
                    className="flex items-center gap-2 text-gray-700 hover:text-blue-500"
                >
                    <FaUsers size={18} /> Users
                </Link>
                <Link
                    to="/settings"
                    className="flex items-center gap-2 text-gray-700 hover:text-blue-500"
                >
                    <FaCog size={18} /> Settings
                </Link>
            </nav>
        </div>
    );
};

export default AdminSidebar;
