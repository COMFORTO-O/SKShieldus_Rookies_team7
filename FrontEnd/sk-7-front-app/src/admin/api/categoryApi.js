
import axios from './api';

// 카테고리 목록 조회
export const getCategories = async () => {
    return await axios.get('/api/problem/category');
};

// 카테고리 생성
export const createCategory = async (categoryData) => {
    return await axios.post('/api/problem/category/create', categoryData);
};

// 카테고리 업데이트
export const updateCategory = async (id, categoryData) => {
    // 백엔드 @PutMapping("/category/update/{id}") 이므로 id를 URL에 포함
    return await axios.put(`/api/problem/category/update/${id}`, categoryData);
};