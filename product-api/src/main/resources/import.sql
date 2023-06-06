INSERT INTO category (id, description) VALUES (1000, 'Comic Books');
INSERT INTO category (id, description) VALUES (1002, 'Movies');
INSERT INTO category (id, description) VALUES (1003, 'Other Books');

INSERT INTO supplier (id, name) VALUES (1000, 'Amazon');
INSERT INTO supplier (id, name) VALUES (1002, 'Mercado Livre');

INSERT INTO product (id, name, FK_SUPPLIER, FK_CATEGORY, quantity_available, created_at) VALUES (1000, 'Lord of the Rings', 1000, 1003, 10, CURRENT_TIMESTAMP);

INSERT INTO product (id, name, FK_SUPPLIER, FK_CATEGORY, quantity_available, created_at) VALUES (1002, 'The Hobbit', 1000, 1003,, 10, CURRENT_TIMESTAMP);

INSERT INTO product (id, name, FK_SUPPLIER, FK_CATEGORY, quantity_available, created_at) VALUES (1003, 'Harry Potter: Secret Chamber', 1000, 1003,, 5, CURRENT_TIMESTAMP);