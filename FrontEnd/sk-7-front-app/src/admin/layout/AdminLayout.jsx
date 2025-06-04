import { FaUsers, FaDollarSign, FaChartBar } from "react-icons/fa";

import Header from "../componenet/AdminHeader";
import Sidebar from "../componenet/AdminSidebar";

const AdminLayout = ({ children }) => {
    return (
        <div className="flex min-h-screen bg-gray-100 overflow-hidden">
            <Sidebar />
            <div className="flex-1 flex flex-col">
                <Header />
                <div className="flex-1 bg-white m-10 rounded shadow overflow-auto">
                    {children}
                </div>
            </div>
        </div>
    );
};

export default AdminLayout;
