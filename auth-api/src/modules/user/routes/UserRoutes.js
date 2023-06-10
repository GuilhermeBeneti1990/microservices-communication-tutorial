import { Router } from "express";

import UserController from "../controller/UserController.js";
import checkToken from "../../../config/authentication/checkToken.js";

const router = new Router();

router.post("/api/users/auth", UserController.getAccessToken);

router.use(checkToken);

router.get("/api/users/:email", UserController.findByEmail);

export default router;