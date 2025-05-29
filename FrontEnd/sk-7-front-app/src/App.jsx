import { Routes, Route, useLocation } from "react-router-dom"; // RouterLink로 별칭 사용 가능
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import Navbar from "./components/molecules/Navbar";
import InfoPage from "./pages/InfoPage";
import useAuthStore from "./store/useAuthStore";

/* 
    앱 컨테이너
*/

function App() {
    // App 처음에 accessToken 존재 확인할 것
    useAuthStore.getState().ensureAuth();

    const location = useLocation();
    // "/" 또는 "/info" 경로에서만 Navbar 표시
    const showNavbar =
        location.pathname !== "/login" && location.pathname !== "/register";

    return (
        <div className="h-screen flex flex-col">
            {showNavbar && (
                <header className="flex-none sticky top-0 z-50 shadow-sm">
                    <Navbar />
                </header>
            )}
            <main className="flex-auto h-full min-h-0 overflow-auto">
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<SignupPage />} />
                    <Route path="/info" element={<InfoPage />} />
                </Routes>
            </main>
        </div>
    );
}

export default App;
