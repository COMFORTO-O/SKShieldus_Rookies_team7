import { FaUsers, FaDollarSign, FaChartBar } from "react-icons/fa";

import AdminLayout from "../layout/AdminLayout";
import AdminCard from "../componenet/AdminCard";

const AdminDashboardPage = () => {
    return (
        <AdminLayout>
            <main className="p-6 overflow-auto">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    {/* <AdminCard
                            title="Total Users"
                            value="1,234"
                            icon={<FaUsers size={32} />}
                        />

                        <AdminCard
                            title="Traffic"
                            value="98.7%"
                            icon={<FaChartBar size={32} />}
                        /> */}
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminDashboardPage;
