import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AdminLayout from "../layout/AdminLayout";
import { getStatistics } from "../api/statisticsApi.js";

// Recharts 관련 컴포넌트 임포트
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts";

const AdminDashboardPage = () => {
    const navigate = useNavigate();
    const [loadingAuth, setLoadingAuth] = useState(true);
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
            try {
                let statisticsRes = await getStatistics();
                // 날짜 데이터를 Recharts에 맞게 포맷팅 (YYYY-MM-DD 또는 MM-DD)
                const formattedDailyData = statisticsRes.data.data.dailyMemberCount.map(item => ({
                    ...item,
                    date: item.date ? new Date(item.date).toLocaleDateString('ko-KR', { month: '2-digit', day: '2-digit' }) : 'N/A' // 예: '06.05'
                }));
                setStatistics({
                    ...statisticsRes.data.data,
                    dailyMemberCount: formattedDailyData
                });
            } catch (error) {
                console.error("Failed to fetch statistics:", error);
                // 에러 처리 또는 권한 없는 경우 리디렉션
                // navigate('/login'); // 예시: 권한 없는 경우 로그인 페이지로
            } finally {
                setLoadingAuth(false);
            }
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

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8"> {/* mb-8 추가하여 차트와의 간격 확보 */}

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

                {/* 일별 사용자 수 그래프 섹션 */}
                <div className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                    <h3 className="text-xl font-semibold mb-4">일별 신규 사용자 수</h3>
                    {statistics.dailyMemberCount && statistics.dailyMemberCount.length > 0 ? (
                        <ResponsiveContainer width="100%" height={300}>
                            <LineChart
                                data={statistics.dailyMemberCount}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="date" />
                                <YAxis />
                                <Tooltip />
                                <Legend />
                                <Line
                                    type="monotone"
                                    dataKey="createCount"
                                    stroke="#8884d8"
                                    activeDot={{ r: 8 }}
                                    name="신규 사용자 수" // 툴팁에 표시될 이름
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    ) : (
                        <p className="text-gray-600 text-center">일별 사용자 데이터가 없습니다.</p>
                    )}
                </div>
            </main>
        </AdminLayout>
    );
};

export default AdminDashboardPage;