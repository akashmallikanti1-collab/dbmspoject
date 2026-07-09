CREATE OR REPLACE TRIGGER rating_check
BEFORE INSERT OR UPDATE ON review_data
FOR EACH ROW
BEGIN
    IF :NEW.rating < 1 OR :NEW.rating > 5 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Rating must be between 1 and 5');
    END IF;
END;
/