import { Routes, Route, Link } from "react-router-dom"; // RouterLink로 별칭 사용 가능
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import Navbar from "./components/molecules/Navbar";

function App() {
    return (
        <>
            <header>
                <Navbar />
            </header>
            <main>
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<SignupPage />} />
                </Routes>
            </main>
        </>
    );
}

export default App;
