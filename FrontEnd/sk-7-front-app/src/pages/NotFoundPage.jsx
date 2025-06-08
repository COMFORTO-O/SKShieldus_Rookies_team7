import { Link } from "react-router-dom";

function NotFound() {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 text-center px-4">
            <h1 className="text-6xl font-bold text-blue-600 mb-4">404</h1>
            <p className="text-xl font-medium text-gray-700 mb-2">
                페이지를 찾을 수 없습니다.
            </p>
            <p className="text-gray-500 mb-6">
                요청하신 페이지가 존재하지 않거나, 이동되었을 수 있습니다.
            </p>
            <Link
                to="/"
                className="px-5 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg shadow-md transition"
            >
                홈으로 돌아가기
            </Link>
        </div>
    );
}

export default NotFound;
