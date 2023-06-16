import { Router } from "express";

import OrderController from "../controller/OrderController";

const router = new Router();

router.get("/api/orders", OrderController.findAll);
router.get("/api/orders/:id", OrderController.findById);
router.get("/api/orders/products/:productId", OrderController.findByProductId);
router.post("/api/orders", OrderController.createOrder);

export default router;
