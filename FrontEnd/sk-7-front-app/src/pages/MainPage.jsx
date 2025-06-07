import MainContents from "../components/molecules/MainContents";
import UserInfo from "../components/molecules/UserInfo";

// 컨테이너 스타일
const containerClass = "bg-base-100 flex flex-col xl:flex-row min-h-0";
// 컨텐츠 섹션 스타일
const mainContentClass = "flex-1 order-2 xl:order-1 px-6 py-4";
// 유저 정보 섹션 스타일
const rightSideContentClass =
    "w-full xl:w-72 border-t xl:border-t-0 xl:border-l bg-base-100 px-6 py-4 \
 flex-shrink-0 order-1 xl:order-2 xl:overflow-y-auto";

function MainPage() {
    return (
        // 메인 페이지 컨테이너
        <>
            <div className={containerClass}>
                <div className={mainContentClass}>
                    <MainContents />
                </div>

                <div className={rightSideContentClass  + " h-fit self-start"}>
                    <UserInfo />
                </div>
            </div>
        </>
    );
}

export default MainPage;
