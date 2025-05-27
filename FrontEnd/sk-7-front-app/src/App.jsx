import { Routes, Route, Link } from "react-router-dom"; // RouterLink로 별칭 사용 가능
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import Navbar from "./components/molecules/Navbar";

/* 
    앱 컨테이너
    
*/

function App() {
    return (
        <div className="h-screen flex flex-col">
            <header className="flex-none">
                <Navbar />
            </header>
            <main className="flex-1 overflow-hidden bg-black">
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<SignupPage />} />
                </Routes>
            </main>
        </div>
    );
}

export default App;
