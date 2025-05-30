import AuthLayout from "../components/auth/AuthLayout";
import SignupForm from "../components/molecules/SignupForm";
import RightBanner from "../components/molecules/RightBanner";

function SignupPage() {
    return (
        <div className="grid grid-cols-1 2xl:grid-cols-[1fr_40%] min-h-screen">
            {/* 왼쪽: 회원가입 영역 */}
            <div className="w-full bg-white flex items-center justify-center">
                <section className="w-full max-w-2xl">
                    <AuthLayout title="User Register">
                        <SignupForm />
                    </AuthLayout>
                </section>
            </div>

            {/* 오른쪽: 안내 문구 영역 */}
            <RightBanner />
        </div>
    );
}

export default SignupPage;
