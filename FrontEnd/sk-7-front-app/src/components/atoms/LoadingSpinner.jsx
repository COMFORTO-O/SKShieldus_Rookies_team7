import React from "react";

const LoadingSpinner = () => (
    <div className="flex items-center justify-center w-full h-full py-8">
        <div className="animate-spin rounded-full h-12 w-12 border-t-4 border-b-4 border-blue-500"></div>
        <span className="ml-4 text-blue-600 font-semibold">로딩 중...</span>
    </div>
);

export default LoadingSpinner;
