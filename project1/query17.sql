SELECT g.*, sg.lgrade FROM g_enrollments g, score_grade sg WHERE g.score = sg.score AND g.score IS NOT NULL ORDER BY g.score DESC;