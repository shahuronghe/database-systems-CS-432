SELECT DISTINCT B#, first_name, last_name FROM students, classes, g_enrollments WHERE students.B# = g_enrollments.g_B# AND g_enrollments.classid = classes.classid AND classes.class_size = 13 AND classes.dept_code = 'CS';