import React from "react";

function AuthLayout({ children, title }) {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-secondary">
            <div className="w-full max-w-md p-8 space-y-8 bg-base-100 rounded-lg shadow-md">
                <h2 className="mt-6 text-center text-3xl font-extrabold text-primary">
                    {title}
                </h2>
                {children}
            </div>
        </div>
    );
}

export default AuthLayout;
