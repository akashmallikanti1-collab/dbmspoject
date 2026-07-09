DROP SEQUENCE user_seq;
DROP SEQUENCE car_seq;
DROP SEQUENCE cart_seq;
DROP SEQUENCE booking_seq;
DROP SEQUENCE review_seq;
DROP SEQUENCE test_seq;
DROP SEQUENCE brand_seq;
-- DROP TABLES
DROP TABLE review_data CASCADE CONSTRAINTS;
DROP TABLE testdrive_data CASCADE CONSTRAINTS;
DROP TABLE booking_data CASCADE CONSTRAINTS;
DROP TABLE cart_data CASCADE CONSTRAINTS;
DROP TABLE car_data CASCADE CONSTRAINTS;
DROP TABLE brand_data CASCADE CONSTRAINTS;
DROP TABLE user_data CASCADE CONSTRAINTS;


-- USER TABLE
CREATE TABLE user_data (
user_id NUMBER PRIMARY KEY,
user_name VARCHAR2(50) NOT NULL,
user_pass VARCHAR2(50) NOT NULL,
user_role VARCHAR2(20) CHECK (user_role IN ('customer','dealer','brand')) NOT NULL,
CONSTRAINT uq_user_name UNIQUE(user_name)
);


-- BRAND
CREATE TABLE brand_data (
brand_id NUMBER PRIMARY KEY,
brand_name VARCHAR2(50) NOT NULL
);


-- CAR
CREATE TABLE car_data (
car_id NUMBER PRIMARY KEY,
model_name VARCHAR2(50) NOT NULL,
price_amount NUMBER NOT NULL,
engine_info VARCHAR2(100),
mileage_info VARCHAR2(50),
transmission_type VARCHAR2(50),
seating_capacity NUMBER,
safety_rating NUMBER,
image_url VARCHAR2(300),
brand_id NUMBER,
CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES brand_data(brand_id)
);


-- CART
CREATE TABLE cart_data (
cart_id NUMBER PRIMARY KEY,
customer_id NUMBER,
car_id NUMBER,
CONSTRAINT fk_cart_user FOREIGN KEY (customer_id) REFERENCES user_data(user_id),
CONSTRAINT fk_cart_car FOREIGN KEY (car_id) REFERENCES car_data(car_id)
);


-- BOOKING
CREATE TABLE booking_data (
booking_id NUMBER PRIMARY KEY,
customer_id NUMBER,
car_id NUMBER,
booking_date DATE DEFAULT SYSDATE,
CONSTRAINT fk_booking_user FOREIGN KEY (customer_id) REFERENCES user_data(user_id),
CONSTRAINT fk_booking_car FOREIGN KEY (car_id) REFERENCES car_data(car_id)
);


-- REVIEW
CREATE TABLE review_data (
review_id NUMBER PRIMARY KEY,
customer_id NUMBER,
car_id NUMBER,
rating NUMBER CHECK (rating BETWEEN 1 AND 5),
review_text VARCHAR2(200),
CONSTRAINT fk_review_user FOREIGN KEY (customer_id) REFERENCES user_data(user_id),
CONSTRAINT fk_review_car FOREIGN KEY (car_id) REFERENCES car_data(car_id)
);


-- TEST DRIVE
CREATE TABLE testdrive_data (
test_id NUMBER PRIMARY KEY,
customer_id NUMBER,
car_id NUMBER,
status VARCHAR2(20) CHECK (status IN ('pending','approved','rejected')),
CONSTRAINT fk_test_user FOREIGN KEY (customer_id) REFERENCES user_data(user_id),
CONSTRAINT fk_test_car FOREIGN KEY (car_id) REFERENCES car_data(car_id)
);


-- SEQUENCES (IMPORTANT)
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE car_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE cart_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE booking_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE review_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE test_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE brand_seq START WITH 1 INCREMENT BY 1;