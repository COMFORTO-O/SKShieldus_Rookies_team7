import useModalStore from "../../store/useModalStore";

const NoticeModal = () => {
    const { noticeModalOpen, openNoticeModal, closeNoticeModal } = useModalStore();

    return(<div
        className="fixed mt-2 mr-5 right-0 z-50 flex bg-secondary h-[300px] w-[250px] rounded-lg border-solid border-2"
        style={{
            animation: "modalDropFade 0.4s cubic-bezier(0.4,0,0.2,1)",
        }}
    >
        알림 모달
    </div>);
};

export default NoticeModal;
