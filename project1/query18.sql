SELECT B#, first_name, last_name FROM students WHERE B# IN (SELECT g_B# FROM g_enrollments WHERE classid IN (SELECT classid FROM g_enrollments WHERE g_B# = 'B00000004'));