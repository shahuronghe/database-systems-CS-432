SELECT B#, first_name, last_name, COUNT(*) AS number_of_classes_taken FROM students s, g_enrollments g WHERE s.B#=g.g_B# GROUP BY B#, first_name, last_name HAVING COUNT(*)>=2;