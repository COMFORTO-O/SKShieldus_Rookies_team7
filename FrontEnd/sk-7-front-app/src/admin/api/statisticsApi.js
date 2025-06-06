import api from "./api";

export const getStatistics = () => {
    return api.get("/api/admin/statistics"); // email, password, name, phone
};


