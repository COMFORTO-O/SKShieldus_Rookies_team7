import api from "./api";

export const registerUser = (userData) => {
    return api.post("/api/account/register", userData); // email, password, name, phone
};

export const deleteUser = (id) => {
    return api.delete(`/api/member/delete/${id}`);
};

export const getUserInfo = () => {
    return api.get("/info");
};

export const getUserList = () => {
    return api.get("/api/member/list", )
}

