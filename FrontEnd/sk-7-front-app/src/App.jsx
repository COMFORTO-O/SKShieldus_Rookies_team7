import {
    Routes,
    Route,
    useLocation,
    useNavigate,
    Link,
} from "react-router-dom";
import React, { Suspense, useEffect } from "react";
import Navbar from "./components/molecules/Navbar";
import useAuthStore from "./store/useAuthStore";
import LoadingSpinner from "./components/atoms/LoadingSpinner";
import useModalStore from "./store/useModalStore";
import { Avatar } from "@mui/material";
import InfoModal from "./components/modals/InfoModal";
import AdminRoute from "./admin/routes/AdminRoute";
import AdminUserDetailPage from "./admin/pages/user/AdminUserDetailPage.jsx";
import AdminProblemDetailPage from "./admin/pages/problem/AdminProblemDetailPage.jsx";
import AdminProblemEditPage from "./admin/pages/problem/AdminProblemEditPage.jsx";
import AdminCategoryManagePage from "./admin/pages/category/AdminCategoryManagePage.jsx";

// React.lazy를 사용하여 페이지 컴포넌트 동적 임포트
const MainPage = React.lazy(() => import("./pages/MainPage"));
const LoginPage = React.lazy(() => import("./pages/LoginPage"));
const SignupPage = React.lazy(() => import("./pages/SignupPage"));
const SolvePage = React.lazy(() => import("./pages/SolvePage"));
const InfoPage = React.lazy(() => import("./pages/InfoPage"));

// 어드민 페이지 임포트트
const AdminDashboardPage = React.lazy(() =>
    import("./admin/pages/AdminDashboardPage")
);
const AdminLoginPage = React.lazy(() => import("./admin/pages/AdminLoginPage"));
const AdminUserManagePage = React.lazy(() =>
    import("./admin/pages/user/AdminUserManagePage.jsx")
);
const AdminProblemManagePage = React.lazy(() =>
    import("./admin/pages/problem/AdminProblemManagePage.jsx")
);

/*앱 컨테이너*/
function App() {
    const { setLogin, user } = useAuthStore(); // Zustand에서 상태와 액션을 가져옵니다.
    const navigate = useNavigate();

    // Modal 상태 가져오기
    const { infoModalOpen } = useModalStore();

    // 앱이 처음 마운트될 때 한 번만 실행되어 accessToken을 확인하고 상태를 업데이트합니다.
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            // 토큰이 만료된 상태라면

            console.log("현재 로그인 상태입니다.\nAccess Token:", token);
            setLogin(token); // Zustand 상태 업데이트
        }
    }, [setLogin]);

    const location = useLocation();
    // Navbar 표시 조건은 그대로 유지
    const showNavbar =
        location.pathname !== "/login" &&
        location.pathname !== "/register" &&
        location.pathname !== "/adminlogin" &&
        !location.pathname.startsWith("/admin/user");

    // 로그인 상태 감지하여 리다이렉션
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
        <div className="h-screen flex flex-col min-w-[780px]">
            {showNavbar && (
                <header className="flex-none sticky top-0 z-50 shadow-sm">
                    <Navbar />
                </header>
            )}
            <main className="flex-auto h-full min-h-0 overflow-auto">
                {infoModalOpen && <InfoModal />}
                {/* Suspense로 Routes를 감싸고, fallback UI를 지정합니다. */}
                <Suspense fallback={<LoadingSpinner />}>
                    {/* 로딩 중에 보여줄 컴포넌트 */}
                    <Routes>
                        <Route path="/" element={<MainPage />} />
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/register" element={<SignupPage />} />
                        <Route path="/solve" element={<SolvePage />} />
                        <Route path="/info" element={<InfoPage />} />

                        {/* 어드민 라우트 */}
                        <Route element={<AdminRoute />}>
                            <Route
                                path="/adminlogin"
                                element={<AdminLoginPage />}
                            />
                            <Route
                                path="/admin"
                                element={<AdminDashboardPage />}
                            />
                            <Route
                                path="/admin/user"
                                element={<AdminUserManagePage />}
                            />
                            <Route
                                path="/admin/user/:id"
                                element={<AdminUserDetailPage />}
                            />
                            <Route
                                path="/admin/problem"
                                element={<AdminProblemManagePage />}
                            />
                            <Route
                                path="/admin/problem/:id"
                                element={<AdminProblemDetailPage />}
                            />
                            <Route
                                path="/admin/problem/:id/edit"
                                element={<AdminProblemEditPage />}
                            />
                            <Route
                                path="/admin/category"
                                element={<AdminCategoryManagePage />}
                            />
                        </Route>
                    </Routes>
                </Suspense>
            </main>
        </div>
    );
}

export default App;
