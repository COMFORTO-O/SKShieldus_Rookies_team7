import UserInfo from "../components/molecules/UserInfo";
import HelpRoomList from "../components/molecules/HelpRoomList.jsx";

export default function HelpRoomListPage() {
    return (
        <div className="bg-gray-50 min-h-[calc(100vh-4rem)] flex flex-col xl:flex-row p-4 sm:p-6 lg:p-8">
            {/* 메인 컨텐츠 섹션 */}
            <div className="flex-1 order-2 xl:order-1 xl:mr-6 mb-6 xl:mb-0">
                <HelpRoomList />
            </div>

            {/* 유저 정보 섹션 (오른쪽 사이드바) */}
            <div className="w-full xl:w-80 flex-shrink-0 order-1 xl:order-2 bg-white rounded-xl shadow-lg border border-gray-100 p-6 xl:p-0 xl:overflow-y-auto">
                <UserInfo />
            </div>
        </div>
    );
}
