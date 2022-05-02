/* CS532 Project 2
*
* 	1. Shahu Ronghe
* 	2. Lovelesh Colaco
* 	3. Lalji Devda
*/

-- DROP SECTION
DROP SEQUENCE gen_log_num;


-- 1. GENERATE LOG SEQUENCE WITH 1000
CREATE SEQUENCE gen_log_num
	START WITH 1000
	INCREMENT BY 1;
	

-- 8. All triggers.
-- student delete safe trigger
CREATE OR REPLACE TRIGGER delete_student_enrollments
BEFORE DELETE ON students
FOR EACH ROW
BEGIN
	DELETE FROM g_enrollments WHERE g_B# = :OLD.B#;
END;
/

-- update classes table to increment class size when students enrolls.
CREATE OR REPLACE TRIGGER increment_class_size
AFTER INSERT ON g_enrollments
FOR EACH ROW
BEGIN
	UPDATE classes SET class_size  = class_size + 1 WHERE classid = :NEW.classid;
END;
/

-- update classes table to decrement class size when student drops the class.
CREATE OR REPLACE TRIGGER decrement_class_size
AFTER DELETE ON g_enrollments
FOR EACH ROW
BEGIN
	UPDATE classes SET class_size  = class_size - 1 WHERE classid = :OLD.classid;
END;
/

-- Logging triggers.
-- student delete update log
CREATE OR REPLACE TRIGGER student_delete_log
AFTER DELETE ON students
FOR EACH ROW
DECLARE
	login VARCHAR2(24);
BEGIN
	login := USER;
	INSERT INTO logs VALUES (gen_log_num.nextval, login, sysdate, 'students', 'delete', :OLD.B#);
END;  
/

-- student enrolled update log
CREATE OR REPLACE TRIGGER student_enrolled_log
AFTER INSERT ON g_enrollments
FOR EACH ROW
DECLARE
	login VARCHAR2(24);
	keyvalue VARCHAR2(24);
BEGIN
	login := USER;
	keyvalue := :NEW.g_B# || ',' || :NEW.classid;
	INSERT INTO logs VALUES (gen_log_num.nextval, login, sysdate, 'g_enrollments', 'insert', keyvalue);
END;  
/

-- student drop class update log
CREATE OR REPLACE TRIGGER student_drop_log
AFTER DELETE ON g_enrollments
FOR EACH ROW
DECLARE
	login VARCHAR2(24);
	keyvalue VARCHAR2(24);
BEGIN
	login := USER;
	keyvalue := :OLD.g_B# || ',' || :OLD.classid;
	INSERT INTO logs VALUES (gen_log_num.nextval, login, sysdate, 'g_enrollments', 'delete', keyvalue);
END;  
/

-- class_size update log
CREATE OR REPLACE TRIGGER class_size_update_log
AFTER UPDATE ON classes
FOR EACH ROW
DECLARE
	login VARCHAR2(24);
	keyvalue VARCHAR2(24);
BEGIN
	login := USER;
	keyvalue := :OLD.classid || ',' || :NEW.class_size;
	INSERT INTO logs VALUES (gen_log_num.nextval, login, sysdate, 'classes', 'update', keyvalue);
END;  
/