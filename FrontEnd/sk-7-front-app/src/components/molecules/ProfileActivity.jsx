import { useEffect, useState } from "react";
import getSubmitCodes from "../../api/getSubmitCodes";
import axios from "axios";

const ProfileActivity = () => {
    const [submissionPageData, setSubmissionPageData] = useState({
        content: [],
        totalPages: 1,
        number: 0,
    });

    const [selectedCodeList, setSelectedCodeList] = useState([]);
    const [openSubmitId, setOpenSubmitId] = useState(null);

    // ✅ 페이지 요청
    const fetchPage = async (pageNum) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await axios.get(
                `${
                    import.meta.env.VITE_API_URL
                }/api/mypage/mypage?page=${pageNum}&size=5`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                    withCredentials: true,
                }
            );

            const newPageData = response.data.data.submissions;
            setSubmissionPageData(newPageData);
            setOpenSubmitId(null); // 페이지 전환 시 펼쳐진 코드 초기화
            setSelectedCodeList([]);
        } catch (err) {
            console.error("문제 제출 내역 불러오기 실패", err);
        }
    };

    useEffect(() => {
        fetchPage(0); // 초기 1페이지
    }, []);

    // ✅ 제출 코드 토글
    const handleClick = async (submitProblemId) => {
        if (openSubmitId === submitProblemId) {
            setOpenSubmitId(null);
            setSelectedCodeList([]);
            return;
        }

        try {
            const codeList = await getSubmitCodes(submitProblemId);
            setSelectedCodeList(codeList);
            setOpenSubmitId(submitProblemId);
        } catch (err) {
            console.error("제출 코드 불러오기 실패", err);
        }
    };

    return (
        <div className="w-full h-full flex mt-10">
            <div className="w-[30%] pl-7">문제 내역</div>

            <div className="w-[60%] space-y-4 pr-2">
                {submissionPageData.content.map((s) => (
                    <div
                        key={s.submitProblemId}
                        className="border p-4 rounded shadow cursor-pointer hover:bg-slate-100"
                        onClick={() => handleClick(s.submitProblemId)}
                    >
                        <p className="font-bold">{s.problemTitle}</p>
                        <p>제출일: {new Date(s.createdAt).toLocaleString()}</p>
                        <p
                            className={
                                s.pass ? "text-green-600" : "text-red-600"
                            }
                        >
                            결과: {s.pass ? "통과" : "실패"}
                        </p>

                        {openSubmitId === s.submitProblemId && (
                            <div className="mt-4 space-y-3">
                                {selectedCodeList.map((code) => (
                                    <div
                                        key={code.id}
                                        className="border rounded p-3 bg-gray-50 text-sm font-mono whitespace-pre-wrap"
                                    >
                                        <p className="text-xs text-gray-600 mb-1">
                                            상태: {code.status} / 언어:{" "}
                                            {code.language}
                                        </p>
                                        <pre>{code.code}</pre>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                ))}

                {/* ✅ 페이지 네비게이션 */}
                <div className="flex justify-center gap-2 mt-4 flex-wrap">
                    {(() => {
                        const total = submissionPageData.totalPages;
                        const current = submissionPageData.number;
                        const maxVisible = 5;
                        const start = Math.max(
                            0,
                            current - Math.floor(maxVisible / 2)
                        );
                        const end = Math.min(total, start + maxVisible);
                        const visiblePages = Array.from(
                            { length: end - start },
                            (_, i) => start + i
                        );

                        return visiblePages.map((pageNum) => (
                            <button
                                key={pageNum}
                                onClick={() => fetchPage(pageNum)}
                                className={`px-3 py-1 rounded ${
                                    pageNum === current
                                        ? "bg-blue-500 text-white"
                                        : "bg-gray-200 text-gray-800"
                                }`}
                            >
                                {pageNum + 1}
                            </button>
                        ));
                    })()}
                </div>
            </div>
        </div>
    );
};

export default ProfileActivity;
