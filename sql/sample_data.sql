-- Additional sample data
-- Bookings
INSERT INTO booking_data VALUES (booking_seq.NEXTVAL, 1, 1, SYSDATE);
INSERT INTO booking_data VALUES (booking_seq.NEXTVAL, 1, 3, SYSDATE-1);
INSERT INTO booking_data VALUES (booking_seq.NEXTVAL, 1, 6, SYSDATE-2);
INSERT INTO booking_data VALUES (booking_seq.NEXTVAL, 1, 11, SYSDATE-3);
INSERT INTO booking_data VALUES (booking_seq.NEXTVAL, 1, 16, SYSDATE-4);
INSERT INTO booking_data VALUES (booking_seq.NEXTVAL, 1, 21, SYSDATE-5);

-- Reviews
INSERT INTO review_data VALUES (review_seq.NEXTVAL, 1, 1, 5, 'Excellent car, very comfortable!');
INSERT INTO review_data VALUES (review_seq.NEXTVAL, 1, 3, 4, 'Good value for money');
INSERT INTO review_data VALUES (review_seq.NEXTVAL, 1, 6, 5, 'Honda quality is amazing');
INSERT INTO review_data VALUES (review_seq.NEXTVAL, 1, 11, 4, 'Toyota reliability at its best');
INSERT INTO review_data VALUES (review_seq.NEXTVAL, 1, 16, 5, 'Nexon is fun to drive!');

-- Cart items
INSERT INTO cart_data VALUES (cart_seq.NEXTVAL, 1, 4);
INSERT INTO cart_data VALUES (cart_seq.NEXTVAL, 1, 9);
INSERT INTO cart_data VALUES (cart_seq.NEXTVAL, 1, 20);

-- Test drives
INSERT INTO testdrive_data VALUES (test_seq.NEXTVAL, 1, 2, 'pending');
INSERT INTO testdrive_data VALUES (test_seq.NEXTVAL, 1, 7, 'approved');
INSERT INTO testdrive_data VALUES (test_seq.NEXTVAL, 1, 12, 'pending');

COMMIT;