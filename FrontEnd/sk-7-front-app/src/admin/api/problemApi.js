import api from "./api";

export const getProblems = (params) => {
    return api.get("/api/problem", { params });
};

export const createProblem = (data) => {
    return api.post("/api/problem/create", data);
};

export const getProblemDetail = (id) => {
    return api.get(`/api/problem/detail/${id}`);
};

export const updateProblem = (id, data) => {
    return api.put(`/api/problem/update/${id}`, data);
};

export const deleteProblem = (id) => {
    return api.delete(`/api/problem/delete/${id}`);
};