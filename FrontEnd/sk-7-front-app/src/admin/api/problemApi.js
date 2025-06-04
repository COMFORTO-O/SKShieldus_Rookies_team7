import api from "./api";

export const getProblems = (params) => {
    return api.get("/api/problem", { params });
};

export const createProblem = (data) => {
    return api.post("/create", data);
};

export const getProblemDetail = (id) => {
    return api.get(`/detail/${id}`);
};

export const updateProblem = (id, data) => {
    return api.put(`/update/${id}`, data);
};

export const deleteProblem = (id) => {
    return api.delete(`/delete/${id}`);
};