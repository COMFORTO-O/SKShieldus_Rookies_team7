import api from "./api";

export const registerUser = (userData) => {
    return api.post("/register", userData); // email, password, name, phone
};

export const deleteUser = () => {
    return api.get("/delete");
};

export const getUserInfo = () => {
    return api.get("/info");
};
