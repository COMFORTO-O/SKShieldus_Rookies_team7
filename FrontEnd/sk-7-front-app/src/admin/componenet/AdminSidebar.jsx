import { FaHome, FaUsers, FaFileCode } from "react-icons/fa";
import { Link } from "react-router-dom";

const AdminSidebar = () => {
    return (
        <div className="w-64 bg-white shadow-lg p-4">
            <h1 className="text-xl font-bold mb-6">Admin</h1>
            <nav className="flex flex-col gap-4">
                <Link
                    to="/admin"
                    className="flex items-center gap-2 text-gray-700 hover:text-blue-500"
                >
                    <FaHome size={18} /> Dashboard
                </Link>
                <Link
                    to="/adminuser"
                    className="flex items-center gap-2 text-gray-700 hover:text-blue-500"
                >
                    <FaUsers size={18} /> user
                </Link>
                <Link
                    to="/adminproblem"
                    className="flex items-center gap-2 text-gray-700 hover:text-blue-500"
                >
                    <FaFileCode size={18} /> problem
                </Link>
            </nav>
        </div>
    );
};

export default AdminSidebar;
