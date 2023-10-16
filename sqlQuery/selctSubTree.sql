SELECT  * FROM categories AS t, lastSon AS l
WHERE t.ID IN (
	WITH RECURSIVE subTree (fatherID, ID) AS (
		SELECT fatherID, ID FROM categories
		WHERE fatherID = 111
		UNION ALL
		SELECT c.fatherID, c.ID FROM (subTree AS f) JOIN (categories AS c) ON f.ID = c.fatherID
	)
	SELECT c.ID FROM subTree AS f JOIN categories AS c ON f.ID = c.ID
	UNION ALL
	SELECT ID from categories WHERE ID=111
)