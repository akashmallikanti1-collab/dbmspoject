CREATE OR REPLACE PROCEDURE add_review (
    cid NUMBER,
    car_id_val NUMBER,
    rate NUMBER,
    txt VARCHAR2
)
AS
BEGIN
    -- VALIDATION
    IF rate < 1 OR rate > 5 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Rating must be between 1 and 5');
    END IF;

    -- INSERT
    INSERT INTO review_data 
    VALUES (review_seq.NEXTVAL, cid, car_id_val, rate, txt);

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/