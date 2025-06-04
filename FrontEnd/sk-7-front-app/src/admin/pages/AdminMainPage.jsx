import AdminHeader from "../componenet/AdminHeader";
import AdminSidebar from "../componenet/AdminSidebar";
import AdminCard from "../componenet/AdminCard";
import { FaUsers, FaDollarSign, FaChartBar } from "react-icons/fa";

const AdminMainPage = () => {
    return (
        <div className="flex h-screen bg-gray-100">
            <AdminSidebar />
            <div className="flex-1 flex flex-col">
                <AdminHeader />
                <main className="p-6 overflow-auto">
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        <AdminCard
                            title="Total Users"
                            value="1,234"
                            icon={<FaUsers size={32} />}
                        />

                        <AdminCard
                            title="Traffic"
                            value="98.7%"
                            icon={<FaChartBar size={32} />}
                        />
                    </div>
                </main>
            </div>
        </div>
    );
};

export default AdminMainPage;
