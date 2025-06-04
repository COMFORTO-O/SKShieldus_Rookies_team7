const AdminCard = ({ title, value, icon }) => {
    return (
        <div className="bg-white p-4 rounded-2xl shadow flex items-center justify-between">
            <div>
                <p className="text-gray-500">{title}</p>
                <h3 className="text-2xl font-bold">{value}</h3>
            </div>
            <div className="text-blue-500">{icon}</div>
        </div>
    );
};

export default AdminCard;
