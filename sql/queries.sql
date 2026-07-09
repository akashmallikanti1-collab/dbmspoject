-- ===============================
-- SET DISPLAY FORMAT
-- ===============================
SET LINESIZE 200;
SET PAGESIZE 200;

-- ===============================
-- VIEW ALL CARS
-- ===============================
SELECT c.car_id,
       c.model_name,
       b.brand_name,
       c.price_amount,
       c.engine_info,
       c.mileage_info,
       c.transmission_type,
       c.seating_capacity,
       c.safety_rating,
       c.image_url
FROM car_data c
JOIN brand_data b ON c.brand_id = b.brand_id
ORDER BY c.price_amount;

-- ===============================
-- VIEW SAMPLE FILTER (STATIC)
-- ===============================
SELECT *
FROM car_data
WHERE price_amount BETWEEN 800000 AND 2000000;

-- ===============================
-- COMPARE SAMPLE CARS
-- ===============================
SELECT model_name,
       price_amount,
       mileage_info,
       engine_info,
       transmission_type,
       seating_capacity,
       safety_rating
FROM car_data
WHERE car_id IN (1,2);

-- ===============================
-- CART SAMPLE VIEW
-- ===============================
SELECT ct.customer_id,
       c.model_name,
       c.price_amount
FROM cart_data ct
JOIN car_data c ON ct.car_id = c.car_id
WHERE ct.customer_id = 1;

-- ===============================
-- BOOKING HISTORY SAMPLE
-- ===============================
SELECT c.model_name,
       b.booking_date
FROM booking_data b
JOIN car_data c ON b.car_id = c.car_id
WHERE b.customer_id = 1
ORDER BY b.booking_date DESC;

-- ===============================
-- REVIEWS
-- ===============================
SELECT c.model_name,
       r.rating,
       r.review_text
FROM review_data r
JOIN car_data c ON r.car_id = c.car_id
ORDER BY r.rating DESC;

-- ===============================
-- TEST DRIVE REQUESTS
-- ===============================
SELECT c.model_name,
       t.status
FROM testdrive_data t
JOIN car_data c ON t.car_id = c.car_id;

-- ===============================
-- DEALER SALES ANALYSIS
-- ===============================
SELECT c.model_name,
       COUNT(b.booking_id) AS total_sales
FROM car_data c
LEFT JOIN booking_data b ON c.car_id = b.car_id
GROUP BY c.model_name
ORDER BY total_sales DESC;

-- ===============================
-- DEALER PROFIT ANALYSIS
-- ===============================
SELECT c.model_name,
       COUNT(b.booking_id) * c.price_amount AS revenue
FROM car_data c
LEFT JOIN booking_data b ON c.car_id = b.car_id
GROUP BY c.model_name, c.price_amount;

-- ===============================
-- BRAND SALES ANALYSIS
-- ===============================
SELECT c.model_name,
       COUNT(b.booking_id) AS total_sales
FROM car_data c
LEFT JOIN booking_data b ON c.car_id = b.car_id
GROUP BY c.model_name;

-- ===============================
-- BRAND PROFIT ANALYSIS
-- ===============================
SELECT c.model_name,
       COUNT(b.booking_id) * c.price_amount AS revenue
FROM car_data c
LEFT JOIN booking_data b ON c.car_id = b.car_id
GROUP BY c.model_name, c.price_amount;

-- ===============================
-- AREA ANALYTICS
-- ===============================
SELECT u.user_name AS area,
       COUNT(b.booking_id) AS total_sales
FROM booking_data b
JOIN user_data u ON b.customer_id = u.user_id
GROUP BY u.user_name;

-- ===============================
-- TOP RATED CARS
-- ===============================
SELECT c.model_name,
       AVG(r.rating) AS avg_rating
FROM review_data r
JOIN car_data c ON r.car_id = c.car_id
GROUP BY c.model_name
ORDER BY avg_rating DESC;

-- ===============================
-- NO SALES CARS
-- ===============================
SELECT c.model_name
FROM car_data c
LEFT JOIN booking_data b ON c.car_id = b.car_id
WHERE b.booking_id IS NULL;