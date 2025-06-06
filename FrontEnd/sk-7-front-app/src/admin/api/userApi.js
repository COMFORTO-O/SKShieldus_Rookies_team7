import api from "./api";

export const registerUser = (userData) => {
    return api.post("/api/account/register", userData); // email, password, name, phone
};

export const deleteUser = (id) => {
    return api.delete(`/api/member/delete/${id}`);
};

export const updateUser = (updateData) => {
    return api.post(`/api/member/update`, updateData);
};

export const getUserInfo = (searchData) => {
    return api.get("/api/member/info", searchData);
};

export const getUserList = () => {
    return api.get("/api/member/list", )
}

