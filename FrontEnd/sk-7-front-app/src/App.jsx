import { Routes, Route, useLocation, useNavigate } from "react-router-dom";
import React, { Suspense, useEffect } from "react"; // Suspense, useEffect 추가
// import MainPage from "./pages/MainPage"; // 주석 처리 또는 삭제
// import LoginPage from "./pages/LoginPage";
// import SignupPage from "./pages/SignupPage";
// import SolvePage from "./pages/SolvePage";
// import InfoPage from "./pages/InfoPage";
import Navbar from "./components/molecules/Navbar";
import useAuthStore from "./store/useAuthStore";
import LoadingSpinner from "./components/atoms/LoadingSpinner";

// React.lazy를 사용하여 페이지 컴포넌트 동적 임포트
const MainPage = React.lazy(() => import("./pages/MainPage"));
const LoginPage = React.lazy(() => import("./pages/LoginPage"));
const SignupPage = React.lazy(() => import("./pages/SignupPage"));
const SolvePage = React.lazy(() => import("./pages/SolvePage"));
const InfoPage = React.lazy(() => import("./pages/InfoPage"));

/*
    앱 컨테이너
*/

function App() {
    const { setLogin, user } = useAuthStore(); // Zustand에서 상태와 액션을 가져옵니다.
    const navigate = useNavigate();

    // 앱이 처음 마운트될 때 한 번만 실행되어 accessToken을 확인하고 상태를 업데이트합니다.
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            console.log("현재 로그인 상태입니다.\nAccess Token:", token);
            setLogin(token); // Zustand 상태 업데이트
        }
    }, [setLogin]); // setLogin은 일반적으로 안정적이므로 의존성 배열에 추가해도 문제 없습니다.

    const location = useLocation();
    // Navbar 표시 조건은 그대로 유지
    const showNavbar =
        location.pathname !== "/login" && location.pathname !== "/register";

    // 로그인 상태 감지하여 리다이렉션 (선택적 개선)
    // 이 로직은 user 상태가 변경될 때마다 실행됩니다.
    useEffect(() => {
        const publicPaths = ["/login", "/register"];
        // 로그인이 필요한 페이지에 비로그인 상태로 접근 시 로그인 페이지로 리다이렉트
        if (
            !user &&
            !publicPaths.includes(location.pathname) &&
            location.pathname !== "/" &&
            location.pathname !== "/info"
        ) {
            // 이 조건은 프로젝트의 인증 정책에 따라 달라질 수 있습니다.
            // 예를 들어, SolvePage는 로그인이 필요하다고 가정합니다.
            if (location.pathname === "/solve") {
                // alert("로그인이 필요한 서비스입니다."); // 사용자에게 알림
                // navigate("/login");
            }
        }
        // 로그인 상태에서 로그인/회원가입 페이지 접근 시 메인으로 리다이렉트
        if (
            user &&
            (location.pathname === "/login" ||
                location.pathname === "/register")
        ) {
            // navigate("/");
        }
    }, [user, location.pathname, navigate]);

    return (
        <div className="h-screen flex flex-col">
            {showNavbar && (
                <header className="flex-none sticky top-0 z-50 shadow-sm">
                    <Navbar />
                </header>
            )}
            <main className="flex-auto h-full min-h-0 overflow-auto">
                {/* Suspense로 Routes를 감싸고, fallback UI를 지정합니다. */}
                <Suspense fallback={<LoadingSpinner />}>
                    {" "}
                    {/* 로딩 중에 보여줄 컴포넌트 */}
                    <Routes>
                        <Route path="/" element={<MainPage />} />
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/register" element={<SignupPage />} />
                        <Route path="/solve" element={<SolvePage />} />
                        <Route path="/info" element={<InfoPage />} />
                    </Routes>
                </Suspense>
            </main>
        </div>
    );
}

export default App;
