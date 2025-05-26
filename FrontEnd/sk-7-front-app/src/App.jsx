import "./App.css";
import React from "react";
import { BrowserRouter, Routes, Route, Link } from "react-router-dom"; // RouterLink로 별칭 사용 가능
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";

function App() {
    return (
        <BrowserRouter>
            <header>
                <nav className="">
                    <Link to="/">메인</Link>
                    <Link to="/login">로그인</Link>
                </nav>
            </header>
            <main>
                <Routes>
                    {/* 루트 경로 ("/")에 MainPage 컴포넌트를 연결합니다. */}
                    <Route path="/" element={<MainPage />} />

                    {/* "/login" 경로에 LoginPage 컴포넌트를 연결합니다. */}
                    <Route path="/login" element={<LoginPage />} />
                </Routes>
            </main>
        </BrowserRouter>
    );
}

export default App;