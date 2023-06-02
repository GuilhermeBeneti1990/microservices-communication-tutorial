import express from "express";

import * as db from "./src/config/db/mockData.js";
import userRoutes from "./src/modules/user/routes/UserRoutes.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8080;

db.createMockData();

app.get("/api/status", (req, res) => {
    return res.status(200).json({
        service: "auth-api",
        status: "up"
    })
})

app.use(express.json());

app.use(userRoutes);


app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`);
})