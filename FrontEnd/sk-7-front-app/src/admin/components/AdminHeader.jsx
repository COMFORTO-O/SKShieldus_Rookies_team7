import { useState, useEffect } from "react";
import { getUserInfo } from "../api/userApi.js";
import { useNavigate } from "react-router-dom";


const AdminHeader = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                setLoading(true);
                const res = await getUserInfo();
                console.log(res.data.data.member);
                if (res.data.data.member.role !== "ADMIN") {
                    alert("관리자 권한이 필요합니다. 접근이 거부되었습니다.");
                    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
                    // 2. 메인 페이지로 리다이렉트
                    navigate("/");
                }
                setUser(res.data.data.member || null);
                setError(null);
            } catch (err) {
                console.error("관리자 정보 가져오기 실패:", err);
                setError("관리자 정보를 불러오는 데 실패했습니다.");
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, []);

    return (
        <header className="bg-white shadow-md p-4 flex justify-between items-center border-b border-gray-200">
            <h2 className="text-xl font-semibold text-gray-800">대시보드</h2>
            <div className="flex items-center space-x-4">
                {loading ? (
                    <span className="text-sm text-gray-500">정보 로딩 중...</span>
                ) : error ? (
                    <span className="text-sm text-red-500">{error}</span>
                ) : user ? (
                    <span className="text-sm text-gray-700 font-medium">
                        {user.name || "관리자"} ({user.email || "ID 알 수 없음"})
                    </span>
                ) : (
                    <span className="text-sm text-gray-500">사용자 정보 없음</span>
                )}
                <img
                    src="/image.png"
                    alt="관리자 아바타"
                    className="rounded-full w-9 h-9 object-cover border border-gray-300"
                />
            </div>
        </header>
    );
};

export default AdminHeader;