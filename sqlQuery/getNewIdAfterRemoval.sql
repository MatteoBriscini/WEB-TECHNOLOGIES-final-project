
SELECT  t.ID, t.ID-POWER(10, (LENGTH(CAST(t.ID AS nchar))-2)) FROM categories AS t, lastSon AS l
WHERE t.ID IN (
	WITH RECURSIVE subTree (fatherID, ID) AS (
		SELECT fatherID, ID FROM categories
		WHERE fatherID = 32
		UNION ALL
		SELECT c.fatherID, c.ID FROM (subTree AS f) JOIN (categories AS c) ON f.ID = c.fatherID
	)
	SELECT c.ID FROM subTree AS f JOIN categories AS c ON f.ID = c.ID
	UNION ALL
	SELECT ID from categories WHERE ID= 32
)