import bcrypt from "bcrypt";

import User from "../../modules/user/model/User.js";

export async function createMockData() {
    try {
        await User.sync({ force: true });

        let password = await bcrypt.hash("123456", 10);
    
        await User.create({
            name: "Mock User",
            email: "user@emailMock.com",
            password
        });
    } catch (error) {
        console.error(error);
    }
}