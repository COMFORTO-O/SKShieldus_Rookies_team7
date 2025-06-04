const AdminHeader = () => {
    return (
        <header className="bg-white shadow p-4 flex justify-between items-center">
            <h2 className="text-xl font-semibold">Dashboard</h2>
            <div className="flex items-center space-x-4">
                <span className="text-sm text-gray-600">admin@example.com</span>
                <img
                    src="https://via.placeholder.com/32"
                    alt="avatar"
                    className="rounded-full w-8 h-8"
                />
            </div>
        </header>
    );
};

export default AdminHeader;
