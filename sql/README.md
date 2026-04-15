# Introduction
This document is to record completed practice statements for SQL exercises.
Each code block is a single solution to the practice link provided within each section.
There is also a table setup query to outline the schema for the tables.


# SQL Queries

###### Table Setup (DDL)
```sql
CREATE TABLE IF NOT EXISTS PUBLIC.members (
    memid INTEGER PRIMARY KEY NOT NULL,
    surname VARCHAR NOT NULL,
    firstname VARCHAR NOT NULL,
    address VARCHAR NOT NULL,
    zipcode INTEGER NOT NULL,
    telephone VARCHAR NOT NULL,
    recommendedby INTEGER,
    joindate TIMESTAMP NOT NULL,
    CONSTRAINT members_recommendedby FOREIGN KEY (recommendedby) REFERENCES members(memid)
    );

CREATE TABLE IF NOT EXISTS PUBLIC.facilities (
     facid INTEGER PRIMARY KEY,
     name VARCHAR NOT NULL,
     membercost NUMERIC NOT NULL,
     guestcost NUMERIC NOT NULL,
     initialoutlay NUMERIC NOT NULL,
     monthlymaintenance NUMERIC NOT NULL
    );

CREATE TABLE IF NOT EXISTS PUBLIC.bookings (
    bookid INTEGER PRIMARY KEY NOT NULL,
    facid INTEGER NOT NULL,
    memid INTEGER NOT NULL,
    starttime TIMESTAMP NOT NULL,
    slots INTEGER NOT NULL,
    CONSTRAINT facid_facilities FOREIGN KEY (facid) REFERENCES facilities(facid),
    CONSTRAINT memid_members FOREIGN KEY (memid) REFERENCES members(memid)
    );
```

### Modifying data

###### Question 1: Insert some data into a table

```sql
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
VALUES
    (9, 'Spa', 20, 30, 100000, 800);
```

###### Question 2:  Insert calculated data into a table (Subquery Insertion)
https://pgexercises.com/questions/updates/insert3.html
```sql
INSERT INTO cd.facilities (
  facid, name, membercost, guestcost, 
  initialoutlay, monthlymaintenance
) 
VALUES 
  (
    (
      SELECT 
        (
          MAX(facid) + 1
        ) 
      FROM 
        cd.facilities
    ), 
    'Spa', 
    20, 
    30, 
    100000, 
    800
  );
```

###### Question 3: Update existing data
https://pgexercises.com/questions/updates/update.html
```sql
UPDATE
    cd.facilities
SET
    initialoutlay = 10000
WHERE
    facid = 1;
```

###### Question 4: Update a row based on the contents of another row
https://pgexercises.com/questions/updates/updatecalculated.html
```sql
UPDATE
    cd.facilities
SET
    membercost =(membercost * 1.1),
    guestcost =(guestcost * 1.1)
where
    facid = 1;
```

###### Question 5: Delete all bookings
https://pgexercises.com/questions/updates/delete.html
```sql
TRUNCATE cd.bookings;
```

###### Question 6: Delete a member from the cd.members table
https://pgexercises.com/questions/updates/deletewh.html
```sql
-- SELECT * FROM cd.members; -- Note to use a select statement to view before deletion
DELETE FROM
    cd.members
WHERE
    memid = 37;
```

### Basics

###### Question 7: Produce a list of facilities that charge a fee to members, and that fee is less than 1/50th of the monthly maintenance cost
https://pgexercises.com/questions/basic/where2.html
```sql
SELECT
    facid,
    name,
    membercost,
    monthlymaintenance
FROM
    cd.facilities
WHERE
    membercost > 0
  AND membercost < monthlymaintenance / 50.0;
```

###### Question 8: Basic string searches; Facilities with the word 'Tennis'
https://pgexercises.com/questions/basic/where3.html
```sql
SELECT
    *
FROM
    cd.facilities
WHERE
    name LIKE '%Tennis%';
```

###### Question 9: Matching against multiple possible values
https://pgexercises.com/questions/basic/where4.html
```sql
SELECT
    *
FROM
    cd.facilities
WHERE
    facid IN (1, 5);
```

###### Question 10: Dates; List of members who joined after the start of September 2012
https://pgexercises.com/questions/basic/date.html
```sql
SELECT
    memid,
    surname,
    firstname,
    joindate
FROM
    cd.members
WHERE
    joindate >= '2012-09-01';
```

###### Question 11: Union; combined list of all surnames and all facility names
https://pgexercises.com/questions/basic/union.html
```sql
SELECT
    surname
FROM
    cd.members
UNION
SELECT
    name
FROM
    cd.facilities;
```

### Joins

###### Question 12: Retrieve the start times of members' bookings
https://pgexercises.com/questions/joins/simplejoin.html
```sql
SELECT
    b.starttime
FROM
    cd.bookings b
        JOIN cd.members m ON m.memid = b.memid
WHERE
    m.firstname = 'David'
    AND m.surname = 'Farrell';
```

###### Question 13: Work out the start times of bookings for tennis courts
https://pgexercises.com/questions/joins/simplejoin2.html
```sql
SELECT 
  b.starttime AS START, 
  f.name AS name 
FROM 
  cd.bookings b 
  JOIN cd.facilities f ON f.facid = b.facid 
WHERE 
  date_trunc('day', b.starttime) = '2012-09-21' 
  AND f.name LIKE 'Tennis%' 
ORDER BY 
  b.starttime ASC;
```

###### Question 14: all members, with their recommender
https://pgexercises.com/questions/joins/self2.html
```sql
SELECT
    m.firstname AS memfname,
    m.surname AS memsname,
    rc.firstname AS recfname,
    rc.surname AS recsname
FROM
    cd.members m
        LEFT OUTER JOIN cd.members rc ON m.recommendedby = rc.memid
ORDER BY
    m.surname,
    m.firstname ASC;
```

###### Question 15: all members who have recommended another member
https://pgexercises.com/questions/joins/self.html
```sql
SELECT 
  DISTINCT m.firstname, 
  m.surname 
FROM 
  cd.members m 
  JOIN cd.members rc ON m.memid = rc.recommendedby 
ORDER BY 
  m.surname, 
  m.firstname ASC;
```

###### Question 16: all members, with their recommender, using no joins
https://pgexercises.com/questions/joins/sub.html
```sql
SELECT 
  DISTINCT concat(m.firstname, ' ', m.surname) AS member, 
  (
    SELECT 
      concat(r.firstname, ' ', r.surname) 
    FROM 
      cd.members r 
    WHERE 
      r.memid = m.recommendedby
  ) AS recommender 
FROM 
  cd.members m 
ORDER BY 
  member, 
  recommender ASC;
```

### Aggregation

###### Question 17: Count the number of recommendations each member makes
https://pgexercises.com/questions/aggregates/count3.html
```sql
SELECT 
  m.recommendedby, 
  count(*) 
FROM 
  cd.members m 
WHERE 
  m.recommendedby IS NOT NULL 
GROUP BY 
  m.recommendedby 
ORDER BY 
  m.recommendedby;
```

###### Question 18: List the total slots booked per facility
https://pgexercises.com/questions/aggregates/fachours.html
```sql
SELECT 
  b.facid, 
  sum(b.slots) AS slots 
FROM 
  cd.bookings b 
WHERE 
  b.facid IS NOT NULL 
GROUP BY 
  b.facid 
ORDER BY 
  b.facid ASC;
```

###### Question 19: Total slots booked per facility in a given month
https://pgexercises.com/questions/aggregates/fachoursbymonth.html
```sql
SELECT 
  b.facid, 
  sum(b.slots) AS slots 
FROM 
  cd.bookings b 
WHERE 
  b.facid IS NOT NULL 
  AND date(b.starttime) >= date('2012-09-01') 
  AND date(b.starttime) < date('2012-10-01') 
GROUP BY 
  b.facid 
ORDER BY 
  slots ASC;
```

###### Question 20: List the total slots booked per facility per month (in 2012)
https://pgexercises.com/questions/aggregates/fachoursbymonth2.html
```sql
SELECT 
  b.facid, 
  extract(
    month 
    FROM 
      b.starttime
  ) AS month, 
  sum(b.slots) AS "Total Slots" 
FROM 
  cd.bookings b 
WHERE 
  EXTRACT(
    YEAR 
    FROM 
      b.starttime
  ) = 2012 
GROUP BY 
  b.facid, 
  month 
ORDER BY 
  b.facid, 
  month;
```

###### Question 21: Count of members who have made at least one booking
https://pgexercises.com/questions/aggregates/members1.html
```sql
SELECT 
  count(DISTINCT b.memid) 
FROM 
  cd.bookings b;
```

###### Question 22: List each member's first booking after September 1st 2012
https://pgexercises.com/questions/aggregates/nbooking.html
```sql
SELECT 
  m.surname, 
  m.firstname, 
  m.memid, 
  min(b.starttime) 
FROM 
  cd.bookings b 
  JOIN cd.members m ON b.memid = m.memid 
WHERE 
  b.starttime >= '2012-09-01' 
GROUP BY 
  m.memid 
ORDER BY 
  m.memid;
```

###### Question 23: Produce a list of member names, with each row containing the total member count
https://pgexercises.com/questions/aggregates/countmembers.html
```sql
SELECT
    (
        SELECT
            count(*)
        FROM
            cd.members
    ) AS count, 
  m.firstname, 
  m.surname
FROM
    cd.members m
GROUP BY
    m.memid
ORDER BY
    m.joindate ASC;
```

```sql
SELECT 
  count(*) over() AS count, 
  m.firstname, 
  m.surname 
FROM 
  cd.members m 
GROUP BY 
  m.memid 
ORDER BY 
  m.joindate ASC;
```

###### Question 24: Produce a numbered list of members
https://pgexercises.com/questions/aggregates/nummembers.html
```sql
SELECT 
  count(*) OVER(
    ORDER BY 
      m.joindate
  ) AS "row_number", 
  m.firstname, 
  m.surname 
FROM 
  cd.members m 
GROUP BY 
  m.memid 
ORDER BY 
  m.joindate ASC;
```

###### Question 25: Output the facility ID that has the highest number of slots booked
https://pgexercises.com/questions/aggregates/fachours4.html
```sql
SELECT 
  b.facid, 
  b.total 
FROM 
  (
    SELECT 
      facid, 
      sum(b2.slots) as total, 
      RANK() over(
        ORDER BY 
          sum(b2.slots) DESC
      ) 
    FROM 
      cd.bookings b2 
    GROUP BY 
      b2.facid
  ) AS b 
WHERE 
  RANK = 1
```

```sql
SELECT 
  facid, 
  sum(b.slots) 
FROM 
  cd.bookings b 
GROUP BY 
  b.facid 
HAVING 
  sum(b.slots) = (
    SELECT 
      max(sum2.s) 
    FROM 
      (
        SELECT 
          sum(b2.slots) AS s 
        FROM 
          cd.bookings b2 
        GROUP BY 
          b2.facid
      ) AS sum2
  );

```

### String

###### Question 26: Format the names of members
https://pgexercises.com/questions/string/concat.html
```sql
SELECT 
  CONCAT(m.surname, ', ', m.firstname) AS name 
FROM 
  cd.members m;
```

```sql
SELECT
    m.surname || ', ' || m.firstname AS name
FROM
    cd.members m;
```

###### Question 27: Find telephone numbers with parentheses
https://pgexercises.com/questions/string/reg.html
```sql
SELECT 
  m.memid, 
  m.telephone 
FROM 
  cd.members m 
WHERE 
  m.telephone LIKE '%(___)%';
```

###### Question 28: Count the number of members whose surname starts with each letter of the alphabet
https://pgexercises.com/questions/string/substr.html
```sql
SELECT 
  LEFT(m.surname, 1) AS letter, 
  count(*) 
FROM 
  cd.members m 
GROUP BY 
  letter 
ORDER BY 
  letter;
```