-- =========================
-- USERS (FINAL)
-- =========================
INSERT INTO user_data VALUES (1,'Akash','123','customer');
INSERT INTO user_data VALUES (2,'HyundaiHyd','pass123','dealer');
INSERT INTO user_data VALUES (3,'HondaHyd','pass123','dealer');
INSERT INTO user_data VALUES (4,'ToyotaHyd','pass123','dealer');
INSERT INTO user_data VALUES (5,'TataHyd','pass123','dealer');
INSERT INTO user_data VALUES (6,'MahindraHyd','pass123','dealer');

-- BRANDS AS USERS
INSERT INTO user_data VALUES (7,'Hyundai','123','brand');
INSERT INTO user_data VALUES (8,'Honda','123','brand');
INSERT INTO user_data VALUES (9,'Toyota','123','brand');
INSERT INTO user_data VALUES (10,'Tata Motors','123','brand');
INSERT INTO user_data VALUES (11,'Mahindra','123','brand');


-- =========================
-- BRANDS
-- =========================
INSERT INTO brand_data VALUES (1,'Hyundai');
INSERT INTO brand_data VALUES (2,'Honda');
INSERT INTO brand_data VALUES (3,'Toyota');
INSERT INTO brand_data VALUES (4,'Tata Motors');
INSERT INTO brand_data VALUES (5,'Mahindra');


-- =========================
-- HYUNDAI
-- FIX: image_url now stores local filename (e.g. creta.jpg)
--      matching files in the resources/ folder on classpath
-- =========================
INSERT INTO car_data VALUES (1,'Creta',1650000,'1.5L Petrol','17 kmpl','Manual/Automatic',5,5,'creta.jpg',1);
INSERT INTO car_data VALUES (2,'Verna',1550000,'1.5L Turbo Petrol','20 kmpl','Automatic',5,5,'verna.jpg',1);
INSERT INTO car_data VALUES (3,'i20',950000,'1.2L Petrol','20 kmpl','Manual/CVT',5,4,'i20.jpg',1);
INSERT INTO car_data VALUES (4,'Venue',1200000,'1.0L Turbo Petrol','18 kmpl','Manual/Automatic',5,4,'venue.jpg',1);
INSERT INTO car_data VALUES (5,'Alcazar',2100000,'1.5L Diesel','20 kmpl','Automatic',7,5,'alcazar.jpg',1);


-- =========================
-- HONDA
-- =========================
INSERT INTO car_data VALUES (6,'City',1600000,'1.5L i-VTEC','18 kmpl','Manual/CVT',5,5,'city.jpg',2);
INSERT INTO car_data VALUES (7,'Amaze',950000,'1.2L Petrol','19 kmpl','Manual/CVT',5,4,'amaze.jpg',2);
INSERT INTO car_data VALUES (8,'Elevate',1700000,'1.5L Petrol','16 kmpl','Manual/Automatic',5,5,'elevate.jpg',2);
INSERT INTO car_data VALUES (9,'WRV',1200000,'1.2L Petrol','17 kmpl','Manual',5,4,'wrv.jpg',2);
INSERT INTO car_data VALUES (10,'Jazz',1000000,'1.2L Petrol','18 kmpl','CVT',5,4,'jazz.jpg',2);


-- =========================
-- TOYOTA
-- =========================
INSERT INTO car_data VALUES (11,'Innova',2400000,'2.4L Diesel','15 kmpl','Manual/Automatic',7,5,'innova.jpg',3);
INSERT INTO car_data VALUES (12,'Fortuner',3800000,'2.8L Diesel','14 kmpl','Automatic',7,5,'fortuner.jpg',3);
INSERT INTO car_data VALUES (13,'Glanza',900000,'1.2L Petrol','22 kmpl','AMT',5,4,'glanza.jpg',3);
INSERT INTO car_data VALUES (14,'Hyryder',1850000,'1.5L Hybrid','24 kmpl','Automatic',5,5,'hyryder.jpg',3);
INSERT INTO car_data VALUES (15,'Camry',4600000,'2.5L Hybrid','23 kmpl','Automatic',5,5,'camry.jpg',3);


-- =========================
-- TATA MOTORS
-- =========================
INSERT INTO car_data VALUES (16,'Nexon',1300000,'1.2L Turbo Petrol','17 kmpl','Manual/AMT',5,5,'nexon.jpg',4);
INSERT INTO car_data VALUES (17,'Punch',800000,'1.2L Petrol','20 kmpl','Manual/AMT',5,4,'punch.jpg',4);
INSERT INTO car_data VALUES (18,'Harrier',2200000,'2.0L Diesel','16 kmpl','Manual/Automatic',5,5,'harrier.jpg',4);
INSERT INTO car_data VALUES (19,'Safari',2400000,'2.0L Diesel','16 kmpl','Automatic',7,5,'safari.jpg',4);
INSERT INTO car_data VALUES (20,'Tiago',650000,'1.2L Petrol','19 kmpl','Manual/AMT',5,4,'tiago.jpg',4);


-- =========================
-- MAHINDRA
-- =========================
INSERT INTO car_data VALUES (21,'Thar',1700000,'2.2L Diesel','15 kmpl','Manual/Automatic',4,4,'thar.jpg',5);
INSERT INTO car_data VALUES (22,'XUV700',2400000,'2.0L Turbo Petrol','16 kmpl','Automatic',7,5,'xuv700.jpg',5);
INSERT INTO car_data VALUES (23,'Scorpio',2000000,'2.2L Diesel','15 kmpl','Manual/Automatic',7,5,'scorpio.jpg',5);
INSERT INTO car_data VALUES (24,'Bolero',1000000,'1.5L Diesel','16 kmpl','Manual',7,3,'bolero.jpg',5);
INSERT INTO car_data VALUES (25,'XUV300',1400000,'1.2L Turbo Petrol','18 kmpl','Manual/AMT',5,5,'xuv300.jpg',5);

COMMIT;
