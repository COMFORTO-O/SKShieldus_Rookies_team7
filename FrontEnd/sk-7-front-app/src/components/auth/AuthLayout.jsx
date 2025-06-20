import React from "react";

function AuthLayout({ children, title }) {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center">
            <div className="w-full max-w-md">
                <h2 className="mb-14 text-center text-3xl font-extrabold text-primary">
                    {title}
                </h2>
                {children}
            </div>
        </div>
    );
}

export default AuthLayout;
