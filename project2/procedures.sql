/* CS532 Project 2
*
* 	1. Shahu Ronghe
* 	2. Lovelesh Colaco
* 	3. Lalji Devda
*/

SET SERVEROUTPUT ON;
SET ERRORLOGGING ON;


-- Package Declaration.
CREATE OR REPLACE PACKAGE myPkg AS

	TYPE myCursor IS REF CURSOR;
	
	--	FOR STUDENTS
	PROCEDURE show_students(out_cur OUT myCursor);
	PROCEDURE remove_student(bnumber IN students.B#%TYPE);


	-- FOR COURSES
	PROCEDURE show_courses(out_cur OUT myCursor);
	PROCEDURE display_preq_by_dept(out_cur IN OUT myCursor, dept_code2 IN courses.dept_code%TYPE, course_no IN courses.course#%TYPE);
	
	
	-- FOR COURSE_CREDIT
	PROCEDURE show_course_credits(out_cur OUT myCursor);
	
	-- FOR CLASSES
	PROCEDURE show_classes(out_cur OUT myCursor);
	PROCEDURE display_std_by_classid(out_cur IN OUT myCursor, classid2 IN classes.classid%TYPE);
	
	-- FOR ENROLLMENTS
	PROCEDURE show_enrollments(out_cur OUT myCursor);
	PROCEDURE student_enrollment(bnumber IN students.B#%TYPE, classid2 IN classes.classid%TYPE);
	PROCEDURE student_drop(bnumber IN students.B#%TYPE, classid2 IN classes.classid%TYPE);
	
	-- FOR GRADE
	PROCEDURE show_grade(out_cur OUT myCursor);
		
	-- FOR PREREQUISITES
	PROCEDURE show_preq(out_cur OUT myCursor);

	-- FOR LOGS
	PROCEDURE show_logs(out_cur OUT myCursor);
END;
/

--Package Implementation.
CREATE OR REPLACE PACKAGE BODY myPkg AS

	-- FOR STUDENTS
	-- for displaying all tuples in this table.
	PROCEDURE show_students(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM students;
	END;
	
	
	-- delete student from students using BNumber
	PROCEDURE remove_student(bnumber IN students.B#%TYPE) AS
	chkbno char(9);
	BEGIN
		chkbno := 0;
		BEGIN
			SELECT B# INTO chkbno FROM students WHERE B# = bnumber;
			EXCEPTION
				WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'The BNumber is invalid.');
				RETURN;
		END;

		BEGIN
			DELETE FROM students WHERE B# = bnumber;
			COMMIT;
		END;
	END;
	
	
	
	
	-- FOR COURSES
	-- for displaying all tuples in this table.
	PROCEDURE show_courses(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM courses;
	END;
	
	-- 4. finding prequisites by dept_code and course#
	PROCEDURE display_preq_by_dept(out_cur IN OUT myCursor, dept_code2 IN courses.dept_code%TYPE, course_no IN courses.course#%TYPE) AS
	
		coursechk number(3);
	BEGIN
		coursechk := 0;
		
		-- check if dept_code and course# exist in courses to futher query from prequisites.
		BEGIN
			SELECT course# INTO coursechk FROM courses WHERE course# = course_no AND UPPER(dept_code) = UPPER(dept_code2);
				EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, dept_code2 || course_no || ' does not exist.');
				RETURN;
		END;
		
		
		-- get all prequisites course for the given dept_code and course#.
		BEGIN
			OPEN out_cur FOR WITH preq2 (pre_dept_code, pre_course#, dept_code, course#) AS (SELECT pre_dept_code, pre_course#, dept_code, course# FROM prerequisites m WHERE UPPER(dept_code) = dept_code2 AND course# = course_no UNION ALL SELECT  m.pre_dept_code, m.pre_course#, m.dept_code, m.course# FROM prerequisites m INNER JOIN preq2 p ON p.pre_dept_code = m.dept_code AND p.pre_course# = m.course#)
			
			SELECT CONCAT (pre_dept_code, pre_course#) AS prerequisites FROM preq2;
		END;
	END;
	
	
	
	
	
	-- FOR COURSE CREDIT
	-- for displaying all tuples in this table.
	PROCEDURE show_course_credits(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM course_credit;
	END;
	
	
	
	
	-- FOR CLASSES
	-- for displaying all tuples in this table.
	PROCEDURE show_classes(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM classes;
	END;	
	
	-- 3. finding students with classid.
	PROCEDURE display_std_by_classid(out_cur IN OUT myCursor, classid2 IN classes.classid%TYPE) AS
		classidstore char(5);
	BEGIN
		classidstore := 0;
		-- check if classid is available in DB, else throw exception and return.
		BEGIN
				SELECT classid INTO classidstore  FROM classes WHERE classid = classid2;
				
				EXCEPTION
						WHEN NO_DATA_FOUND THEN 
							raise_application_error(-20001, 'The classid is invalid.'); 
					RETURN;
				
		END;
		
		-- get all students information based on classid.
		BEGIN
			OPEN out_cur FOR SELECT DISTINCT s.B#, s.first_name, s.last_name FROM students s, g_enrollments ge, classes c WHERE s.B# = ge.g_B# AND ge.classid = c.classid AND c.classid = classid2;
			
		END;
	END;
	
	-- FOR ENROLLMENTS
	-- for displaying all tuples in this table.
	PROCEDURE show_enrollments(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM g_enrollments;
	END;
	
	-- 5. enroll a student in class by given classid and B number.
	PROCEDURE student_enrollment(bnumber IN students.B#%TYPE, classid2 IN classes.classid%TYPE) AS
	chkbno char(9);
	chkbno_grad char(9);
	chkclassid char(5);
	chkclassidsem char(5);
	chkclasssize char(5);
	chkenroll number(1);
	chktotalenroll number(2);
	chkpreqcount number(2);
	
	BEGIN
	
		-- check b number exist in student table.
		BEGIN
			chkbno := 0;
			
			SELECT B# INTO chkbno FROM students WHERE B# = bnumber;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'The B# is invalid. ' || bnumber);
						RETURN;
		END;
		
		-- check b number is graduate student or not.
		BEGIN
			chkbno_grad := 0;
			
			SELECT B# INTO chkbno_grad FROM students WHERE B# = bnumber AND (st_level = 'master' OR st_level = 'PhD');
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'This is not a graduate student.');
						RETURN;
		END;
		
		-- check classid exists in classes table.
		BEGIN
			chkclassid := 0;
			SELECT classid INTO chkclassid FROM classes WHERE classid = classid2;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'The classid is invalid.');
						RETURN;
		END;
		
		-- check if class offered in current semester.
		BEGIN
			chkclassidsem := 0;
			SELECT classid INTO chkclassidsem FROM classes WHERE year = '2021' AND semester = 'Spring' AND classid = classid2;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'Cannot enroll into a class from a previous semester.');
							
						RETURN;
		END;
		
		-- check whether the class is full.
		BEGIN
			chkclasssize := 0;
			SELECT classid INTO chkclasssize FROM classes WHERE classid = classid2 AND class_size < limit;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'The class is already full.');
							
						RETURN;
		END;
		
		-- check student already in the class.
		BEGIN
			chkenroll := 0;
			BEGIN
				SELECT COUNT(*) INTO chkenroll FROM g_enrollments WHERE g_B# = bnumber AND classid = classid2;
			END;
				
			IF(chkenroll = 1)
					THEN raise_application_error(-20001, 'The student is already in the class.');
							
						RETURN;
			END IF;
		END;
		
		-- check student is not enrolled in more than 5 classes in semester.
		BEGIN
			chktotalenroll := 0;
			BEGIN
				SELECT COUNT(*) INTO chktotalenroll FROM g_enrollments g, classes c WHERE g.classid = c.classid AND g_B# = bnumber AND year = 2021 AND semester = 'Spring';
			END;
			IF(chktotalenroll >= 5)
					THEN raise_application_error(-20001, 'Student cannot be enrolled in more than 5 classes in the same semester.');
							
						RETURN;
			END IF;
		END;
		
		-- check if student completed prerequisites with C grade.
		BEGIN
			chkpreqcount := 0;
			BEGIN
				SELECT COUNT(lgrade) INTO chkpreqcount FROM score_grade WHERE score IN (SELECT score FROM g_enrollments WHERE classid IN (SELECT classid FROM classes WHERE dept_code IN (SELECT pre_dept_code FROM prerequisites WHERE dept_code IN (SELECT dept_code FROM classes WHERE classid = classid2) AND course# IN (SELECT course# FROM classes WHERE classid = classid2)) AND course# IN (SELECT  pre_course# FROM prerequisites WHERE dept_code IN (SELECT dept_code FROM classes WHERE classid = classid2) AND course# IN (SELECT course# FROM classes WHERE classid = classid2))) AND g_B# = bnumber) AND lgrade > 'C';
			END;
			
			IF (chkpreqcount > 0)
				THEN raise_application_error(-20001, 'prerequisites not satisfied.');
							
						RETURN;
			END IF;
		END;
		
		
		-- all above conditions are satisfied so insert the tuple so that student can be enrolled and trigger the events.
		BEGIN
			INSERT INTO g_enrollments VALUES (bnumber, classid2, null);
			COMMIT;
			dbms_output.put_line('Student enrolled successfully!');
		END;		
		
	END;
	
	
	
	-- 6. to drop a student from g_enrollments
	PROCEDURE student_drop(bnumber IN students.B#%TYPE, classid2 IN classes.classid%TYPE) AS
	
	chkbno2 char(9);
	chkbno_grad2 char(9);
	chkclassid2 char(5);
	chkclassidsem2 char(5);
	chkenroll2 number(1);
	chktotalenroll2 number(2);
	chkpreqcount2 number(2);
	chkonlyoneclass2 number(2);
	
	BEGIN
	
		-- check b number exist in student table.
		BEGIN
			chkbno2 := 0;
			
			SELECT B# INTO chkbno2 FROM students WHERE B# = bnumber;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'The B# is invalid');
							
						RETURN;
		END;
		
		-- check b number is graduate student or not.
		BEGIN
			chkbno_grad2 := 0;
			
			SELECT B# INTO chkbno_grad2 FROM students WHERE B# = bnumber AND (st_level = 'master' OR st_level = 'PhD');
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'This is not a graduate student.');
							
						RETURN;
		END;
		
		-- check classid exists in classes table.
		BEGIN
			chkclassid2 := 0;
			SELECT classid INTO chkclassid2 FROM classes WHERE classid = classid2;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'The classid is invalid.');
							
						RETURN;
		END;
		
		-- check student already in the class.
		BEGIN
			chkenroll2 := 0;
			BEGIN
				SELECT COUNT(*) INTO chkenroll2 FROM g_enrollments WHERE g_B# = bnumber AND classid = classid2;
			END;
				
			IF(chkenroll2 = 0)
				THEN raise_application_error(-20001, 'The student is not enrolled in the class.');
							
						RETURN;
			END IF;
		END;
		
		-- check if class offered in current semester.
		BEGIN
			chkclassidsem2 := 0;
			SELECT classid INTO chkclassidsem2 FROM classes WHERE year = '2021' AND semester = 'Spring' AND classid = classid2;
			EXCEPTION
						WHEN NO_DATA_FOUND THEN raise_application_error(-20001, 'Only Enrollment in current semester can be dropped.');
							
						RETURN;
						
		END;
		
		
		-- check if the class is the only class in Spring 2021, then reject drop.
		BEGIN
			chkonlyoneclass2 := 0;
			SELECT COUNT(*) INTO chkonlyoneclass2 FROM g_enrollments ge where ge.classid in ( select classid from classes c where year=2021 and semester='Spring') and g_b#=bnumber;

			IF (chkonlyoneclass2 = 1)
				THEN
					raise_application_error(-20001, 'This is the only class for this student in Spring 2021 and cannot be dropped'); 
					RETURN;
			END IF;
		END;
		
		-- finally drop the class for this student
		BEGIN
			DELETE FROM g_enrollments WHERE g_B# = bnumber AND classid = classid2;
			COMMIT;
		END;
	END;
	
	
	
	-- FOR GRADE
	-- for displaying all tuples in this table.
	PROCEDURE show_grade(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM score_grade;
	END;
	
	
	
	
	-- FOR PREREQUISITES
	-- for displaying all tuples in this table.
	PROCEDURE show_preq(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM prerequisites;
	END;
	
	
	
	
	-- FOR LOGS
	-- for displaying all tuples in this table.
	PROCEDURE show_logs(out_cur OUT myCursor) AS
	BEGIN
			OPEN out_cur FOR SELECT * FROM logs;
	END;


END;
/