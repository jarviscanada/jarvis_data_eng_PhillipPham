CREATE DATABASE exa;

\c exa;

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