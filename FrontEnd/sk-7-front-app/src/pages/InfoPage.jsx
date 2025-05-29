import useAuthStore from "../store/useAuthStore";
import { useState } from "react";

const InfoPage = () => {
    const { isLoggedIn, accessToken } = useAuthStore();
    const [loading, setLoading] = useState(false);
    // 계정 관리, 나의 활동  [ account, activity ]
    const [activeTab, setActiveTab] = useState("account");

    return (
        <div className="h-full w-full overflow-auto p-6 bg-gray-50">
            <div>
                {/* 탭버튼 */}
                <div className="flex gap-4 mb-6 mx-20">
                    <button
                        className={`px-4 py-2 rounded-t-lg font-semibold border-b-2 transition ${
                            activeTab === "account"
                                ? "border-primary font-bold text-primary bg-white"
                                : "border-transparent text-gray-500"
                        }`}
                        onClick={() => setActiveTab("account")}
                    >
                        계정 관리
                    </button>
                    <button
                        className={`px-4 py-2 rounded-t-lg font-semibold border-b-2 transition ${
                            activeTab === "activity"
                                ? "border-primary font-bold text-primary bg-white"
                                : "border-transparent text-gray-500"
                        }`}
                        onClick={() => setActiveTab("activity")}
                    >
                        나의 활동
                    </button>
                </div>

                {/* 탭 내용 */}
                <div
                    className="border-solid border-2 border-black w-full h-full 
                        "
                >
                    {activeTab === "account" && (
                        <div>
                            <h2 className="text-3xl font-bold m-4 font-sourgummy text-primary">
                                계정 관리
                            </h2>
                            <h3 className="text-xl mx-5 mt-10 font-semibold font-sourgummy text-primary">
                                기본 정보
                            </h3>
                            <div className="my-5 mx-5 h-96 rounded-3xl border-2 "></div>
                        </div>
                    )}
                    {activeTab === "activity" && (
                        <div>
                            나의 활동 목록
                            <div>내가 푼 문제</div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default InfoPage;
