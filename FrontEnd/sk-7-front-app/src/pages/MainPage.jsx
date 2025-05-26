import React from "react";
import { Link } from "react-router-dom"; // 페이지 이동을 위한 Link 컴포넌트

function MainPage() {
    return (
        <div>
            <h1>메인 페이지</h1>
            <p>메인 페이지에 오신 것을 환영합니다!</p>
            <nav>
                <Link to="/login">로그인 페이지로 이동</Link>
            </nav>
        </div>
    );
}

export default MainPage;
