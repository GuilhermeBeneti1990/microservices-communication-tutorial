import OrderRepository from "../repository/OrderRepository.js";
import { sendProductStockUpdateQueue } from "../config/rabbitmq/productStockUpdateSender.js";
import { INTERNAL_SERVER_ERROR, SUCCESS, BAD_REQUEST } from "../config/constants/httpStatus.js";
import { PENDING, ACCEPTED, REJECTED } from "../config/constants/OrderStatus.js"
import OrderException from "../exceptions/OrderException.js";
import ProductClient from "../clients/ProductClient.js";

class OrderService {
    async createOrder(req) {
        try {
            let orderData = req.body;
            this.validateOrderData(orderData);
            const { authUser } = req;
            const { authorization } = req.headers;
            let order = this.createInitalData(orderData, authUser);
            await this.validateProductInStock(order, authorization);
            let createdOrder = await OrderRepository.save(order);
            this.sendMessage(createdOrder);
            return {
                status: SUCCESS,
                createdOrder
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async updateOrder(orderMessage) {
        try {
            const order = JSON.parse(orderMessage);
            if(order.salesId && order.status) {
                let existingOrder = await OrderRepository.findById(order.salesId);
                if(existingOrder && order.status !== existingOrder.status) {
                    existingOrder.status = order.status;
                    existingOrder.updatedAt = new Date();
                    await OrderRepository.save(existingOrder);
                }
            } else {
                console.warn("The order message was not complete.");
            }
        } catch (error) {
            console.error("Could not parse order message from queue!");
            console.error(error.message);
        }
    }

    async findById(req) {
        try {
            const { id } = req.params;
            this.validateInformedId(id);
            const existingOrder = await OrderRepository.findById(id);
            if(!existingOrder) {
                throw new OrderException(BAD_REQUEST, "The order was not found!");
            }
            return {
                status: SUCCESS,
                existingOrder
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findAll() {
        try {
            const orders = await OrderRepository.findAll();
            if(!orders) {
                throw new OrderException(BAD_REQUEST, "No orders were found!");
            }
            return {
                status: SUCCESS,
                orders
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findByProductId(req) {
        try {
            const { productId } = req.params;
            this.validateInformedProductId(productId);
            const orders = await OrderRepository.findByProductId(productId);
            if(!orders) {
                throw new OrderException(BAD_REQUEST, "No orders were found!");
            }
            return {
                status: SUCCESS,
                salesIds: orders.map(order => { return order.id })
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    validateInformedId(id) {
        if(!id) {
            throw new OrderException(BAD_REQUEST, "The order id must be informed!");
        }
    }

    validateInformedProductId(id) {
        if(!id) {
            throw new OrderException(BAD_REQUEST, "The product id must be informed!");
        }
    }

    createInitalData(orderData, authUser) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            products: orderData.products
        }
    }

    sendMessage(createdOrder) {
        const message = {
            salesId: createdOrder.id,
            products: createdOrder.products
        }
        sendProductStockUpdateQueue(message);
    }
    
    validateOrderData(data) {
        if(!data || !data.products) {
            throw new OrderException(BAD_REQUEST,"The products must be informed!");
        }
    }

    async validateProductInStock(order) {
        let stockIsOk = await ProductClient.checkProductStock(order, token);
        if(!stockIsOk) {
            throw new OrderException(BAD_REQUEST, "The stock is out for the products!");
        }
    }
}

export default new OrderService();