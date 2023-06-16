import Order from '../../model/Order.js';

export async function createMockData() {
    await Order.collection.drop();
    await Order.create({
        products: [
            {
                productId: 1001,
                quantity: 3
            },
            {
                productId: 1002,
                quantity: 2
            },
            {
                productId: 1001,
                quantity: 1
            }
        ],
        user: {
            id: '1234',
            name: 'User Test',
            email: 'test@email.com'
        },
        status: 'APPROVED',
        createdAt: new Date(),
        updatedAt: new Date()
    });
    await Order.create({
        products: [
            {
                productId: 1001,
                quantity: 4
            },
            {
                productId: 1003,
                quantity: 2
            }
        ],
        user: {
            id: '4321',
            name: 'User Test 2',
            email: 'test2@email.com'
        },
        status: 'REJECTED',
        createdAt: new Date(),
        updatedAt: new Date()
    });


    let mockData = await Order.find();
    console.info(`Mock data created: ${JSON.stringify(mockData, undefined, 4)}`);
}