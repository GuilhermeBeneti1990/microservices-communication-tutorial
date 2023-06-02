import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

import UserRepository from "../repository/UserRepository.js";
import UserException from "../exception/UserException.js";
import * as httpStatus from "../../../config/constants/httpStatus.js";
import * as secrets from "../../../config/constants/secrets.js";

class UserService {

    async getAccessToken(req) {
        try {
            const { email, password } = req.body; 
            this.validateAccessTokenData(email, password);

            let user = await UserRepository.findByEmail(email);
            this.validateUserNotFound(user);
            await this.validatePassword(password, user.password);

            const authUser = {
                id: user.id,
                name: user.name,
                email: user.email
            }

            const accessToken = jwt.sign({authUser}, secrets.API_SECRET, {expiresIn: "1d"});

            return {
                status: httpStatus.SUCCESS,
                accessToken
            }
        } catch (error) {
            return {
                stauts: error.stauts ? error.stauts : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findByEmail(req) {
        try {
            const { email } = req.params;
            const { authUser } = req;
            this.validateRequest(email);
            let user = await UserRepository.findByEmail(email);
            this.validateUserNotFound(user);
            this.valdateAuthenticatedUser(user, authUser);

            return {
                stauts: httpStatus.SUCCESS,
                user: {
                    id: user.id,
                    name: user.name,
                    email: user.email
                }
            }
        } catch (error) {
            return {
                stauts: error.stauts ? error.stauts : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    validateAccessTokenData(email, password) {
        if(!email || !password) {
            throw new UserException(httpStatus.UNAUTHORIZED, "Email and password must be informed!");
        }
    }

    async validatePassword(password, hashPassword) {
        if(!await bcrypt.compare(password, hashPassword)) {
            throw new UserException(httpStatus.UNAUTHORIZED, "Password doesn't match!");
        }
    }

    valdateAuthenticatedUser(user, authUser) {
        if(!authUser || user.id !== authUser.id) {
            throw new UserException(httpStatus.FORBIDDEN, "You cannot see this user data!");
        }
    }

    validateEmail(email) {
        if(!email) {
            throw new UserException(httpStatus.BAD_REQUEST, "Email was not informed!");
        }
    }

    validateUserNotFound(user) {
        if(!user) {
            throw new UserException(httpStatus.BAD_REQUEST, "User was not informed!");
        }
    }

}

export default new UserService();