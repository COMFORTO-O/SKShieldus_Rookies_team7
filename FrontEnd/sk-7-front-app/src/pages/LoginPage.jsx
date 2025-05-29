import AuthLayout from "../components/auth/AuthLayout";
import LoginLeftBanner from "../components/molecules/LoginLeftBanner";
import LoginForm from "../components/molecules/LoginForm";

function LoginPage() {
    return (
        <div className="grid grid-cols-1 xl:grid-cols-[40%_1fr] overflow-y-auto">
            {/* 왼쪽: 안내 문구 영역 */}
            <LoginLeftBanner />

            {/* 오른쪽: 로그인 영역 */}
            <div className="w-full bg-white flex items-center justify-center">
                <section className="w-full max-w-xl">
                    <AuthLayout title="Login">
                        <LoginForm />
                    </AuthLayout>
                </section>
            </div>
        </div>
    );
}

export default LoginPage;
