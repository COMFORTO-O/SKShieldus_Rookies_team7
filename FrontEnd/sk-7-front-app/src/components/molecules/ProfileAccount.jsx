import { useState } from "react";
import image from "../../../public/image.png";
import Input from "../atoms/Input";
import Label from "../atoms/Label";
import PropTypes from "prop-types";

export default function ProfileAccount({ name, email }) {
    const [profile, setProfile] = useState({
        name,
        email,
        password: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile((prev) => ({ ...prev, [name]: value }));
    };

    return (
        <div className="w-full h-full flex">
            <div className="w-[30%] pl-7">계정 정보</div>

            <div className="w-[70%] space-y-4">
                <form>
                    <div className="flex flex-row items-center">
                        <div className="flex flex-row items-center">
                            <div>
                                <img
                                    src={image}
                                    className="w-[200px] h-[200px] border rounded-[20%] object-cover"
                                    alt="Profile"
                                />
                            </div>
                            <div className="flex items-center ml-5 text-2xl font-bold">
                                {profile.name}
                            </div>
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

ProfileAccount.PropTypes = {
    name: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
};
