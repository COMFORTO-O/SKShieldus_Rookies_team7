import { useState } from "react";

import image from "../../../public/image.png";

import Input from "../atoms/Input";
import Label from "../atoms/Label";

export default function ProfileAccount() {
    const [profile, setProfile] = useState({
        name: "",
        email: "",
        password: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile((prev) => ({ ...prev, [name]: value }));
    };

    const handleSave = () => {
        console.log("프로필 저장:", profile);
    };

    return (
        <div className="w-full h-full flex">
            <div className="w-[30%] pl-7">계정 정보</div>
            <div className="w-[70%] space-y-4">
                {/* 정보 영역 */}
                <div className="flex flex-row">
                    <img
                        src={image}
                        onClick={""}
                        className="w-[200px] h-[200px] border rounded-[20%]"
                    />
                    <div className="flex items-center ml-5 text-2xl font-bold">
                        Coding Test Name
                    </div>
                </div>
                <div className="w-[70%]">
                    <Label className="block mb-1 text-sm font-medium text-gray-700">
                        이름
                    </Label>
                    <Input
                        name="name"
                        value={profile.name}
                        onChange={handleChange}
                        placeholder="홍길동"
                    />
                </div>
                <div className="w-[70%]">
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
                <div className="w-[70%]">
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

                <div className="flex justify-end mr-5">
                    <button
                        onClick={handleSave}
                        className="px-4 py-2 rounded-lg mt-4 bg-gray-200 hover:text-blue-600"
                    >
                        저장하기
                    </button>
                </div>
            </div>
        </div>
    );
}
