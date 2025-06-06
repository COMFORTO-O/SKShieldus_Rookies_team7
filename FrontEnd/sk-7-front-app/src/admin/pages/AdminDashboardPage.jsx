import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // useNavigate 훅 임포트
import AdminLayout from "../layout/AdminLayout";
import { getStatistics } from "../api/statisticsApi.js"; // 사용자 정보 API 임포트

const AdminDashboardPage = () => {
    const navigate = useNavigate(); // useNavigate 훅 초기화
    const [loadingAuth, setLoadingAuth] = useState(true); // 권한 확인 로딩 상태
    const [statistics, setStatistics] = useState({
        memberCount: {
            totalCount: 0,
            createCount: 0,
            deleteCount: 0,
            date: null,
        },
        dailyMemberCount: [{
            createCount: 0,
            date: null,
        }],
    });
    useEffect(() => {
        const checkAdminAuth = async () => {

            let statisticsRes = await getStatistics();
            setStatistics(statisticsRes.data.data);
            setLoadingAuth(false);

        };

        checkAdminAuth();
    }, [navigate]);

    if (loadingAuth) {
        return (
            <AdminLayout>
                <main className="p-6 text-center bg-gray-100 min-h-screen flex items-center justify-center">
                    <p className="text-xl text-gray-700 font-semibold">관리자 권한을 확인 중입니다...</p>
                </main>
            </AdminLayout>
        );
    }

    return (
        <AdminLayout>
            <main className="p-6 md:p-8 lg:p-10 bg-gray-100 min-h-screen">
                <h2 className="text-3xl font-bold text-gray-800 mb-8">관리자 대시보드</h2>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

                    <div className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                        <h3 className="text-xl font-semibold mb-2">총 사용자 수</h3>
                        <p className="text-3xl font-bold text-blue-600">{statistics.memberCount.totalCount}</p>
                    </div>
                    <div className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                        <h3 className="text-xl font-semibold mb-2">현재 사용자 수</h3>
                        <p className="text-3xl font-bold text-green-600">{statistics.memberCount.createCount}</p>
                    </div>
                    <div className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                        <h3 className="text-xl font-semibold mb-2">탈퇴 사용자 수</h3>
                        <p className="text-3xl font-bold text-purple-600">{statistics.memberCount.deleteCount}</p>
                    </div>

                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminDashboardPage;
