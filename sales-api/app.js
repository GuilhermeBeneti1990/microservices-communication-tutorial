import express from "express";

import { connectMongoDb } from './src/config/db/mongoDbConfig.js';
import { createMockData } from './src/config/db/mockData.js'
import { connectRabbitMq } from "./src/config/rabbitmq/rabbitConfig.js"

import checkToken from "./src/config/auth/checkToken.js";
import OrderRoutes from "./src/routes/OrderRoutes.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8082;

connectMongoDb();
createMockData();
connectRabbitMq();

app.get("/api/status", async (req, res) => {
    return res.status(200).json({
        service: "sales-api",
        status: "up"
    });
});

app.use(express.json());
app.use(checkToken);
app.use(OrderRoutes);

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`);
})