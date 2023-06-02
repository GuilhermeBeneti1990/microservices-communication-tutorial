import UserService from "../service/UserService.js";

class UserController {

    async getAccessToken(req, res) {
        let accessToken = await UserService.getAccessToken(req);
        return res.status(accessToken.status).json(accessToken);
    }

    async findByEmail(req, res) {
        let user = await UserService.findByEmail(req);
        return res.status(user.stauts).json(user);
    }

}

export default UserController;