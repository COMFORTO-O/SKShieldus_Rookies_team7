import MainContents from "../components/molecules/MainContents";

// 컨테이너 스타일
const containerClass = "bg-base-100 flex flex-col md:flex-row min-h-0";
// 컨텐츠 섹션 스타일
const mainContentClass = "flex-1 order-2 md:order-1 px-6 py-4";
// 유저 정보 섹션 스타일
const rightSideContentClass =
    "w-full md:w-72 border-t md:border-t-0 md:border-l bg-base-100 px-6 py-4 \
 flex-shrink-0 order-1 md:order-2 md:overflow-y-auto";

function MainPage() {
    return (
        // 메인 페이지 컨테이너
        <div className={containerClass}>
            <div className={mainContentClass}>
                <MainContents />
            </div>
            <div className={rightSideContentClass}>오른쪽 유저 정보 섹션</div>
        </div>
    );
}

export default MainPage;
