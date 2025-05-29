import { Routes, Route, Link } from "react-router-dom"; // RouterLink로 별칭 사용 가능
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import SolvePage from "./pages/SolvePage";
import Navbar from "./components/molecules/Navbar";

import { useLocation } from "react-router-dom";

/* 
    앱 컨테이너
    
*/

function App() {
    const location = useLocation();

    // SolvePage 네브바 숨기기
    const hideNavbar = location.pathname === "/solve";

    return (
        <div className="h-screen flex flex-col">
            <header className="flex-none sticky top-0 z-50">
                {!hideNavbar && <Navbar />}
            </header>
            <main className="flex-auto min-h-0 overflow-auto">
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<SignupPage />} />
                    <Route path="/solve" element={<SolvePage />} />
                </Routes>
            </main>
        </div>
    );
}

export default App;
