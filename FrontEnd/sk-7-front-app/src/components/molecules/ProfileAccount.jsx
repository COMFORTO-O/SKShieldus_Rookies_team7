import { useState, useRef, useEffect } from "react";
import { FaCamera } from "react-icons/fa";

import getUserInfo from "../../api/getUserInfo";

import image from "../../../public/image.png";

import Input from "../atoms/Input";
import Label from "../atoms/Label";
import LoadingSpinner from "../atoms/LoadingSpinner";
import Button from "../atoms/Button";

export default function ProfileAccount() {
    const [isLoading, setIsLoading] = useState(false);
    const [profileImage, setProfileImage] = useState(image);
    const fileInputRef = useRef(null);

    const [profile, setProfile] = useState({
        name: "",
        email: "",
        password: "",
    });

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const data = await getUserInfo();
                setProfile(data);
            } catch (err) {
                console.log(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchUser();
    }, []);

    // Input Box 입력값 갱신
    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile((prev) => ({ ...prev, [name]: value }));
    };

    // 이미지 수정
    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setProfileImage(imageUrl);
            // 백엔드로 업로드할 파일은 file 객체 그대로 저장
        }
    };

    return (
        <div className="w-full h-full flex">
            <div className="w-[30%] pl-7">계정 정보</div>

            {/* 계정 정보 영역 */}
            <div className="w-[70%] space-y-4">
                <form>
                    <div className="flex flex-row items-center">
                        {/* 프로필 이미지와 아이콘 오버레이 */}
                        <div className="relative">
                            <img
                                src={profileImage}
                                className="w-[200px] h-[200px] border rounded-[20%] object-cover"
                            />
                            {/* 숨겨진 파일 input */}
                            <input
                                type="file"
                                accept="image/*"
                                ref={fileInputRef}
                                className="hidden"
                                onChange={handleImageChange}
                            />
                            {/* 아이콘 */}
                            <button
                                type="button"
                                onClick={() => fileInputRef.current.click()}
                                className="absolute bottom-2 right-2 bg-white p-2 rounded-full shadow-md"
                            >
                                <FaCamera className="text-gray-700" />
                            </button>
                        </div>

                        <div className="flex items-center ml-5 text-2xl font-bold">
                            Coding Test Name
                        </div>
                    </div>
                    <div className="w-[90%] mt-5">
                        <Label className="block mb-1 text-sm font-medium text-gray-700">
                            이름
                        </Label>
                        <Input
                            name="name"
                            type="text"
                            value={profile.name}
                            onChange={handleChange}
                            placeholder="이름"
                        />
                    </div>
                    <div className="w-[90%]">
                        <Label className="block mb-1 text-sm font-medium text-gray-700">
                            이메일
                        </Label>
                        <Input
                            name="email"
                            type="email"
                            value={profile.email}
                            onChange={handleChange}
                            placeholder="example@email.com"
                        />
                    </div>
                    <div className="w-[90%]">
                        <Label className="block mb-1 text-sm font-medium text-gray-700">
                            비밀번호
                        </Label>
                        <Input
                            name="password"
                            type="password"
                            value={profile.password}
                            onChange={handleChange}
                            placeholder="비밀번호"
                        />
                    </div>

                    <div className="flex justify-end mr-10">
                        <Button
                            type="submit"
                            disabled={isLoading}
                            className="px-4 py-2 rounded-lg mt-4 text-sm bg-gray-200 hover:text-blue-600"
                        >
                            {isLoading ? <LoadingSpinner /> : "저장하기"}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}
