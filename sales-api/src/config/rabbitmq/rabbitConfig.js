import amqp from "amqplib/callback_api.js";
import { listenToSalesConfirmationQueue } from '../rabbitmq/salesConfirmationListener.js';

import {
    PRODUCT_TOPIC,
    PRODUCT_STOCK_UPDATE_QUEUE,
    PRODUCT_STOCK_UPDATE_ROUTING_KEY,
    SALES_CONFIRMATION_QUEUE,
    SALES_CONFIRMATION_ROUTING_KEY
} from "./queue.js";
import { RABBIT_MQ_URL } from "../constants/secrtes.js";

const TWO_SECONDS = 2000;
const HALF_MINUTE = 30000;
const CONTAINER_ENV = "container";

export async function connectRabbitMq() {
    const env = process.env.NODE_ENV;

    if(CONTAINER_ENV === env) {
        console.info("Waiting for RabbitMQ to start...")
        setInterval(() => {
            connectRabbitMqAndCreateQueues();
        }, HALF_MINUTE);
    } else {
        connectRabbitMqAndCreateQueues();
    }
}

function connectRabbitMqAndCreateQueues() {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if(error) {
            throw error;
        }

        console.info("Starting RabbitMQ...");

        createQueue(connection, PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_STOCK_UPDATE_ROUTING_KEY, PRODUCT_TOPIC);
        createQueue(connection, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY, PRODUCT_TOPIC);
        
        console.info("Queues and Topics were definded.");
        setTimeout(function() {
            connection.close();
        }, TWO_SECONDS);
    });
    setTimeout(function() {
        listenToSalesConfirmationQueue();
    }, TWO_SECONDS);
}

function createQueue(connection, queue, rountingKey, topic) {
    connection.createChannel((error, channel) => {
        if(error) {
            throw error;
        }
        channel.assertExchange(topic, "topic", { durable: true });
        channel.assertQueue(queue, { durable: true });
        channel.bindQueue(queue, topic, rountingKey);
    });
}
