import { useState, useRef } from "react";
import { FaCamera } from "react-icons/fa";
import image from "../../../public/image.png";
import Input from "../atoms/Input";
import Label from "../atoms/Label";
import LoadingSpinner from "../atoms/LoadingSpinner";
import Button from "../atoms/Button";

export default function ProfileAccount({ name, email }) {
    const [isLoading, setIsLoading] = useState(false);
    const [profileImage, setProfileImage] = useState(image);
    const fileInputRef = useRef(null);

    const [profile, setProfile] = useState({
        name,
        email,
        password: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile((prev) => ({ ...prev, [name]: value }));
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setProfileImage(imageUrl);
        }
    };

    return (
        <div className="w-full h-full flex">
            <div className="w-[30%] pl-7">계정 정보</div>

            <div className="w-[70%] space-y-4">
                <form>
                    <div className="flex flex-row items-center">
                        <div className="relative">
                            <img
                                src={profileImage}
                                className="w-[200px] h-[200px] border rounded-[20%] object-cover"
                            />
                            <input
                                type="file"
                                accept="image/*"
                                ref={fileInputRef}
                                className="hidden"
                                onChange={handleImageChange}
                            />
                            <button
                                type="button"
                                onClick={() => fileInputRef.current.click()}
                                className="absolute bottom-2 right-2 bg-white p-2 rounded-full shadow-md"
                            >
                                <FaCamera className="text-gray-700" />
                            </button>
                        </div>

                        <div className="flex items-center ml-5 text-2xl font-bold">
                            {profile.name}
                        </div>
                    </div>

                    <div className="w-[90%] mt-5">
                        <Label>이름</Label>
                        <Input
                            name="name"
                            type="text"
                            value={profile.name}
                            onChange={handleChange}
                            placeholder="이름"
                        />
                    </div>
                    <div className="w-[90%]">
                        <Label>이메일</Label>
                        <Input
                            name="email"
                            type="email"
                            value={profile.email}
                            onChange={handleChange}
                            placeholder="example@email.com"
                        />
                    </div>
                    {/* <div className="w-[90%]">
                        <Label>비밀번호</Label>
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
                    </div> */}
                </form>
            </div>
        </div>
    );
}
