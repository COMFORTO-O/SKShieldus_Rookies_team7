import { useEffect, useState } from 'react';
import AdminLayout from '../../layout/AdminLayout.jsx';
import Button from '../../../components/atoms/Button.jsx';
import Input from '../../../components/atoms/Input.jsx';
import { getCategories, createCategory, updateCategory } from '../../api/categoryApi.js';

const AdminCategoryManagePage = () => {
    const [categories, setCategories] = useState([]);
    const [form, setForm] = useState({
        id: null,
        code: '',
        description: '',
    });
    const [isEditing, setIsEditing] = useState(false);

    const fetchCategories = async () => {
        try {
            const res = await getCategories();

            setCategories(res.data.data || []);
        } catch (err) {
            console.error("카테고리 목록 가져오기 실패:", err);
            alert("카테고리 목록을 불러오는 데 실패했습니다.");
        }
    };

    const handleFormChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleCreateOrUpdate = async (e) => {
        e.preventDefault();
        try {
            if (isEditing) {



                const { id, code, description } = form;
                await updateCategory(id, { code, description });
                alert("카테고리가 성공적으로 업데이트되었습니다.");
            } else {


                await createCategory({ code: form.code, description: form.description });
                alert("카테고리가 성공적으로 생성되었습니다.");
            }

            setForm({ id: null, code: '', description: '' });
            setIsEditing(false);
            fetchCategories();
        } catch (err) {
            console.error("카테고리 작업 실패:", err.response?.data || err.message);
            alert("카테고리 작업에 실패했습니다: " + (err.response?.data?.message || err.message));
        }
    };


    const handleEditClick = (category) => {
        setForm({ id: category.id, code: category.code, description: category.description });
        setIsEditing(true);
    };


    const handleResetForm = () => {
        setForm({ id: null, code: '', description: '' });
        setIsEditing(false);
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    return (
        <AdminLayout>
            <main className="p-6 md:p-8 lg:p-10 bg-gray-100 min-h-screen">
                <h2 className="text-3xl font-bold text-gray-800 mb-8">카테고리 관리</h2>

                {/* 카테고리 생성/수정 폼 */}
                <section className="bg-white p-6 rounded-xl shadow-lg mb-10 border border-gray-200">
                    <h3 className="text-2xl font-semibold text-gray-700 mb-6 border-b pb-3">
                        {isEditing ? '카테고리 수정' : '새 카테고리 생성'}
                    </h3>
                    <form onSubmit={handleCreateOrUpdate} className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4">
                        <Input
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                            placeholder="카테고리 코드 (예: JAVA)"
                            name="code"
                            value={form.code}
                            onChange={handleFormChange}
                            required
                        />
                        <Input
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                            placeholder="카테고리 설명 (예: Java 언어 문제)"
                            name="description"
                            value={form.description}
                            onChange={handleFormChange}
                            required
                        />
                        <div className="md:col-span-2 flex justify-end space-x-3 mt-4">
                            <Button
                                type="submit"
                                className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg shadow-md transition duration-200 ease-in-out transform hover:scale-105"
                            >
                                {isEditing ? '카테고리 수정 완료' : '카테고리 생성'}
                            </Button>
                            {isEditing && (
                                <Button
                                    type="button"
                                    onClick={handleResetForm}
                                    variant="secondary"
                                    className="py-3 px-6 rounded-lg shadow-md transition duration-200 ease-in-out"
                                >
                                    취소
                                </Button>
                            )}
                        </div>
                    </form>
                </section>

                {/* 카테고리 목록 */}
                <section className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                    <h3 className="text-2xl font-semibold text-gray-700 mb-6 border-b pb-3">기존 카테고리 목록</h3>
                    <div className="overflow-x-auto max-h-[600px] overflow-y-auto rounded-lg border border-gray-300">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-100 sticky top-0 z-10 shadow-sm">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">ID</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">코드</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">설명</th>
                                <th className="px-6 py-3 text-center text-xs font-semibold text-gray-700 uppercase tracking-wider">작업</th>
                            </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                            {categories.length > 0 ? (
                                categories.map((category) => (
                                    <tr key={category.id} className="hover:bg-gray-50 transition duration-150 ease-in-out">
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{category.id}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{category.code}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{category.description}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                                            <Button
                                                variant="secondary"
                                                onClick={() => handleEditClick(category)}
                                                className="py-2 px-4 text-xs font-medium rounded-md shadow-sm hover:shadow-md transition duration-150 ease-in-out"
                                            >
                                                수정
                                            </Button>
                                            {/* 삭제 기능은 백엔드 API가 있다면 추가할 수 있습니다. */}
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan={4} className="px-6 py-4 text-center text-gray-500">
                                        등록된 카테고리가 없습니다.
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </AdminLayout>
    );
};

export default AdminCategoryManagePage;