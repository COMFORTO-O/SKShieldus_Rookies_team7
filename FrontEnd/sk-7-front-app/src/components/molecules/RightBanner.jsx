function RightBanner() {
    return (
        <div className="hidden 2xl:flex flex-col min-h-screen">
            <section className="h-full bg-primary text-white font-bold text-6xl">
                <div className="mt-[30%] ml-[10%]">
                    <div className="mb-2">안내 문구</div>
                    <div>홍보 타이틀</div>
                </div>
            </section>
        </div>
    );
}

export default RightBanner;
