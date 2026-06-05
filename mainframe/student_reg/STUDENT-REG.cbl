      ******************************************************************
      * Author: PHIQW
      * Date: MAY 27, 2026
      * Purpose: MANIPULATE AND QUERY STUDENT REGISTRATION DATA
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. STUDENT-REG.
       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT INITFILE ASSIGN TO "STUDENTSEQUENTIAL.DAT"
           ORGANIZATION IS LINE SEQUENTIAL
           FILE STATUS IS FILE-CHECK-KEY-INIT.

           SELECT STUDENTINDEX ASSIGN TO "STUDENTINDEX.DAT"
           ORGANIZATION IS INDEXED
           ACCESS MODE IS DYNAMIC
           RECORD KEY IS STUD-IDX-ID
           ALTERNATE RECORD KEY IS STUD-IDX-INSERT
               WITH DUPLICATES
           ALTERNATE RECORD KEY IS STUD-IDX-COURSE
               WITH DUPLICATES
           FILE STATUS IS FILE-CHECK-KEY-IDX.

           SELECT SORTFILE ASSIGN TO "STUDENTSORT.TMP".

           SELECT CLASSREPORT ASSIGN TO "CLASSREPORT.DAT"
           ORGANIZATION IS LINE SEQUENTIAL
           FILE STATUS IS FILE-CHECK-KEY-REPORT.

       DATA DIVISION.
       FILE SECTION.

       FD  INITFILE.
       01  STUDENT-SEQ-RECORD.
           88 ENDOFFILE-INIT                   VALUE HIGH-VALUES.
           05 STUD-SEQ-ID                      PIC 9999.
           05 FILLER                           PIC X.
           05 STUD-SEQ-FULLNAME                PIC X(25).
           05 FILLER                           PIC X.
           05 STUD-SEQ-BIRTHDATE.
               10 STUD-SEQ-BIRTH-YEAR          PIC 9(4).
               10 STUD-SEQ-BIRTH-MONTH         PIC 9(2).
               10 STUD-SEQ-BIRTH-DAY           PIC 9(2).
           05 FILLER                           PIC X.
           05 STUD-SEQ-COURSE                  PIC X(15).

       FD  STUDENTINDEX.
       01  STUDENT-IDX-RECORD.
           88 ENDOFFILE-IDX                    VALUE HIGH-VALUES.
           05 STUD-IDX-ID                      PIC 9999.
           05 STUD-IDX-FULLNAME                PIC X(25).
           05 STUD-IDX-BIRTHDATE.
               10 STUD-IDX-BIRTH-YEAR          PIC 9(4).
               10 STUD-IDX-BIRTH-MONTH         PIC 9(2).
               10 STUD-IDX-BIRTH-DAY           PIC 9(2).
           05 STUD-IDX-COURSE                  PIC X(15).
           05 STUD-IDX-INSERT.
               10 STUD-IDX-INSERT-YEAR          PIC 9(4).
               10 STUD-IDX-INSERT-MONTH         PIC 9(2).
               10 STUD-IDX-INSERT-DAY           PIC 9(2).
           05 STUD-IDX-UPDATE.
               10 STUD-IDX-UPDATE-YEAR          PIC 9(4).
               10 STUD-IDX-UPDATE-MONTH         PIC 9(2).
               10 STUD-IDX-UPDATE-DAY           PIC 9(2).

       SD  SORTFILE.
       01  SORTREC.
           05 FILLER                           PIC X(37).
           05 SORT-IDX-COURSE                  PIC X(15).
           05 FILLER                           PIC X(16).

       FD  CLASSREPORT.
       01  CLASS-REPORT-LINE                   PIC X(132).

       WORKING-STORAGE SECTION.

       01  WS-WORKING-STORAGE.
           05 FILE-CHECK-KEYS.
               10 FILE-CHECK-KEY-INIT          PIC XX.
                   88 VALID-INIT               VALUE "00".
               10 FILE-CHECK-KEY-IDX           PIC XX.
                   88 VALID-IDX                VALUE "00".
                   88 IDX-EOF                  VALUE "10".
                   88 IDX-NOTFOUND             VALUE "23".
               10 FILE-CHECK-KEY-REPORT        PIC XX.
                   88 VALID-REPORT             VALUE "00".
           05 COUNT-RECORDS                    PIC 9999.
           05 WS-NEW-STUDENT.
               10 WS-NEW-FULLNAME              PIC X(25).
               10 WS-NEW-BIRTHDAY              PIC 9(8).
               10 WS-NEW-COURSE                PIC X(15).
           05 WS-STUDENT-ID                    PIC 9999.
           05 WS-REP-COURSE                    PIC X(15).
           05 WS-INCLUSION-DATE.
               10 WS-INCLUSION-YEAR            PIC 9(4).
               10 WS-INCLUSION-MONTH           PIC 9(2).
               10 WS-INCLUSION-DAY             PIC 9(2).
           05 WS-CURRENT-DATE.
               10 WS-CURRENT-YEAR              PIC 9(4).
               10 WS-CURRENT-MONTH             PIC 9(2).
               10 WS-CURRENT-DAY               PIC 9(2).
           05 WS-STUD-FOUND                    PIC X VALUE "N".
               88 STUD-FOUND                   VALUE "Y".
           05 WS-CONFIRMATION                  PIC X VALUE "N".
               88 CONFIRM-YES                  VALUE "Y" "y".
           05 WS-END-SORTED                    PIC X VALUE "N".
               88 END-SORTED                   VALUE "Y".


       01  MENU-READ.
           05 MENU-SELECT                      PIC 9.
           05 MENU-VSAM                        PIC 9 VALUE 1.
           05 MENU-STUD-INSERT                 PIC 9 VALUE 2.
           05 MENU-STUD-UPDATE                 PIC 9 VALUE 3.
           05 MENU-STUD-DELETE                 PIC 9 VALUE 4.
           05 MENU-QUERY-ALL                   PIC 9 VALUE 5.
           05 MENU-QUERY-ID                    PIC 9 VALUE 6.
           05 MENU-QUERY-DATE                  PIC 9 VALUE 7.
           05 MENU-REPORT-BREAK                PIC 9 VALUE 8.
           05 MENU-EXIT                        PIC 9 VALUE 9.

       01  FORMAT-LINES.
           05 FRM-LINE-TITLE                   PIC X(42) VALUE
           "+----------------------------------------+".
           05 FRM-LINE-SEP.
               10 FILLER                       PIC X(40) VALUE
                   "----------------------------------------".
               10 FILLER                       PIC X(40) VALUE
                   "----------------------------------------".
               10 FILLER                       PIC X(11) VALUE
                   "-----------".

           05 FRM-LINE-COLUMNS.
               10 FILLER           PIC X VALUE SPACES.
               10 FILLER           PIC X(2) VALUE "ID".
               10 FILLER           PIC X(2) VALUE SPACES.
               10 FILLER           PIC X(3) VALUE " | ".
               10 FILLER           PIC X(12) VALUE "STUDENT NAME".
               10 FILLER           PIC X(13) VALUE SPACES.
               10 FILLER           PIC X(3) VALUE " | ".
               10 FILLER           PIC X(8) VALUE "BIRTHDAY".
               10 FILLER           PIC X(3) VALUE " | ".
               10 FILLER           PIC X(6) VALUE "COURSE".
               10 FILLER           PIC X(9) VALUE SPACES.
               10 FILLER           PIC X(3) VALUE " | ".
               10 FILLER           PIC X(11) VALUE "INSERT DATE".
               10 FILLER           PIC X(3) VALUE " | ".
               10 FILLER           PIC X(11) VALUE "UPDATE DATE".
               10 FILLER           PIC X VALUE SPACES.
           05 FRM-HEADER-CLASS-REPORT.
               10 FILLER                       PIC X(28) VALUE SPACES.
               10 FILLER                       PIC X(23) VALUE
                   "C L A S S   R E P O R T".
           05 FRM-LINE-TOTAL.
               10 FILLER                       PIC X(17) VALUE
                   "TOTAL STUDENTS : ".
               10 FRM-COUNT                    PIC ZZZ9.
           05 FRM-LINE-INCL-DATE.
               10 FILLER                       PIC X(31) VALUE
                   "LIST OF STUDENTS INCLUDED ON : ".
               10 FRM-DATE.
                   15 FRM-DATE-DAY             PIC 99.
                   15 FILLER                   PIC X VALUE "/".
                   15 FRM-DATE-MONTH           PIC 99.
                   15 FILLER                   PIC X VALUE "/".
                   15 FRM-DATE-YEAR            PIC 9999.
           05 FRM-LINE-COURSE-BREAK.
               10 FILLER                       PIC X(11) VALUE
                   "  COURSE : ".
               10 FRM-COURSE                   PIC X(15).

       01  REPORT-LINE-SEP.
               10 FILLER                       PIC X(40) VALUE
                   "----------------------------------------".
               10 FILLER                       PIC X(33) VALUE
                   "---------------------------------".

       01  STUDENT-LINE-DETAIL.
           05 FILLER                       PIC X VALUE SPACES.
           05 STUD-DET-ID                  PIC 9(4).
           05 FILLER                       PIC X(3) VALUE " | ".
           05 STUD-DET-FULLNAME            PIC X(25).
           05 FILLER                       PIC X(3) VALUE " | ".
           05 STUD-DET-BIRTHDATE           PIC 9(8).
           05 FILLER                       PIC X(3) VALUE " | ".
           05 STUD-DET-COURSE              PIC X(15) VALUE "COURSE".
           05 FILLER                       PIC X(3) VALUE " | ".
           05 STUD-DET-INSERT              PIC 9(8).
           05 FILLER                       PIC X(3) VALUE SPACES.
           05 FILLER                       PIC X(3) VALUE " | ".
           05 STUD-DET-UPDATE              PIC 9(8).
           05 FILLER                       PIC X(4) VALUE SPACES.

       01  REPORT-LINE-COLUMNS.
           05 FILLER           PIC X VALUE SPACES.
           05 FILLER           PIC X(2) VALUE "ID".
           05 FILLER           PIC X(2) VALUE SPACES.
           05 FILLER           PIC X(3) VALUE " | ".
           05 FILLER           PIC X(12) VALUE "STUDENT NAME".
           05 FILLER           PIC X(13) VALUE SPACES.
           05 FILLER           PIC X(3) VALUE " | ".
           05 FILLER           PIC X(8) VALUE "BIRTHDAY".
           05 FILLER           PIC X(3) VALUE " | ".
           05 FILLER           PIC X(11) VALUE "INSERT DATE".
           05 FILLER           PIC X(3) VALUE " | ".
           05 FILLER           PIC X(11) VALUE "UPDATE DATE".
           05 FILLER           PIC X VALUE SPACES.

       01  REPORT-LINE-DETAIL.
           05 FILLER                           PIC X VALUE SPACES.
           05 REP-STUD-ID                      PIC 9(4).
           05 FILLER                           PIC X(3) VALUE " | ".
           05 REP-STUD-FULLNAME                PIC X(25).
           05 FILLER                           PIC X(3) VALUE " | ".
           05 REP-STUD-BIRTHDAY                PIC 9(8).
           05 FILLER                           PIC X(3) VALUE " | ".
           05 REP-STUD-INSERT                  PIC 9(8).
           05 FILLER                           PIC X(3) VALUE SPACES.
           05 FILLER                           PIC X(3) VALUE " | ".
           05 REP-STUD-UPDATE                  PIC 9(8).
           05 FILLER                           PIC X(4) VALUE SPACES.


       PROCEDURE DIVISION.

       0100-RUN-MAIN.
           PERFORM 0110-MAIN-MENU UNTIL 0=1.
       0100-END-RUN-MAIN.

       0110-MAIN-MENU.
           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|           M A I N    M E N U           |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|                OPTIONS                 |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|    1 - GENERATE VSAM FILE              |".
           DISPLAY "|    2 - INSERT STUDENT DATA             |".
           DISPLAY "|    3 - UPDATE STUDENT DATA             |".
           DISPLAY "|    4 - DELETE STUDENT DATA             |".
           DISPLAY "|    5 - CLASS QUERY (ALL STUDENTS)      |".
           DISPLAY "|    6 - QUERY STUDENT BY ID             |".
           DISPLAY "|    7 - QUERY BY DATE OF INCLUSION      |".
           DISPLAY "|    8 - REPORT FILE WITH COURSE BREAK   |".
           DISPLAY "|    9 - EXIT                            |".
           DISPLAY FRM-LINE-TITLE.

           DISPLAY "CHOOSE YOUR OPTION (1 TO 9) >>".

           ACCEPT MENU-SELECT.

           EVALUATE MENU-SELECT
               WHEN MENU-VSAM
                   PERFORM 0210-GENERATE-VSAM
               WHEN MENU-STUD-INSERT
                   PERFORM 0220-STUDENT-INSERT
               WHEN MENU-STUD-UPDATE
                   PERFORM 0230-STUDENT-UPDATE
               WHEN MENU-STUD-DELETE
                   PERFORM 0240-STUDENT-DELETE
               WHEN MENU-QUERY-ALL
                   PERFORM 0250-QUERY-CLASS-ALL
               WHEN MENU-QUERY-ID
                   PERFORM 0260-QUERY-ID
               WHEN MENU-QUERY-DATE
                   PERFORM 0270-QUERY-INCLUSION-DATE
               WHEN MENU-REPORT-BREAK
                   PERFORM 0280-REPORT-COURSE
               WHEN MENU-EXIT
                   PERFORM 0290-EXIT-MENU
               WHEN OTHER
                   DISPLAY "INVALID SELECTION"
           END-EVALUATE.

       0110-END-MAIN-MENU.

       0150-GET-DATE.
           ACCEPT WS-CURRENT-DATE FROM DATE YYYYMMDD.
       0150-END-GET-DATE.

       0170-MOVE-IDX-TO-DET.
           MOVE STUD-IDX-ID TO STUD-DET-ID.
           MOVE STUD-IDX-FULLNAME TO STUD-DET-FULLNAME.
           MOVE STUD-IDX-BIRTHDATE TO STUD-DET-BIRTHDATE.
           MOVE STUD-IDX-COURSE TO STUD-DET-COURSE.
           MOVE STUD-IDX-INSERT TO STUD-DET-INSERT.
           MOVE STUD-IDX-UPDATE TO STUD-DET-UPDATE.
       0170-END-MOVE-IDX-TO-DET.

       0180-MOVE-IDX-TO-REP.
           MOVE STUD-IDX-ID TO REP-STUD-ID.
           MOVE STUD-IDX-FULLNAME TO REP-STUD-FULLNAME.
           MOVE STUD-IDX-BIRTHDATE TO REP-STUD-BIRTHDAY.
           MOVE STUD-IDX-INSERT TO REP-STUD-INSERT.
           MOVE STUD-IDX-UPDATE TO REP-STUD-UPDATE.
       0180-END-MOVE-IDX-TO-REP.

       0210-GENERATE-VSAM.
           OPEN INPUT INITFILE.
           OPEN OUTPUT STUDENTINDEX.

           IF NOT VALID-INIT
               DISPLAY "INIT FILE STATUS: " FILE-CHECK-KEY-INIT
           END-IF.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
           END-IF.

           IF NOT VALID-INIT OR NOT VALID-IDX
               CLOSE INITFILE STUDENTINDEX
               EXIT PARAGRAPH
           END-IF.

      *>   SKIP HEADER WHEN READING FIRST LINE
           READ INITFILE AT END SET ENDOFFILE-INIT TO TRUE.
           READ INITFILE AT END SET ENDOFFILE-INIT TO TRUE.
           PERFORM 0150-GET-DATE.
           PERFORM 0215-PROCESS-INITFILE UNTIL ENDOFFILE-INIT.


           CLOSE INITFILE STUDENTINDEX.
       0210-END-GENERATE-VSAM.

       0215-PROCESS-INITFILE.
           MOVE STUD-SEQ-ID TO STUD-IDX-ID.
           MOVE STUD-SEQ-FULLNAME TO STUD-IDX-FULLNAME.
           MOVE STUD-SEQ-BIRTHDATE TO STUD-IDX-BIRTHDATE.
           MOVE STUD-SEQ-COURSE TO STUD-IDX-COURSE.

           MOVE WS-CURRENT-DATE TO STUD-IDX-INSERT.
           MOVE ZEROES TO STUD-IDX-UPDATE.

           WRITE STUDENT-IDX-RECORD
               INVALID KEY DISPLAY
                   "STUDENT INDEX FILE STATUS : " FILE-CHECK-KEY-IDX
           END-WRITE.

           READ INITFILE AT END SET ENDOFFILE-INIT TO TRUE.
       0215-END-PROCESS-INITFILE.

       0220-STUDENT-INSERT.
           INITIALIZE COUNT-RECORDS.
           OPEN I-O STUDENTINDEX.

           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|      A D D   N E W   S T U D E N T     |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY SPACES.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
               CLOSE STUDENTINDEX
               EXIT PARAGRAPH
           END-IF.

           READ STUDENTINDEX
               KEY IS STUD-IDX-ID
               AT END SET ENDOFFILE-IDX TO TRUE
           END-READ.

           PERFORM 0225-STUDENT-INSERT-INDEX UNTIL ENDOFFILE-IDX.

           INITIALIZE WS-NEW-STUDENT.
           DISPLAY "ENTER FULL NAME (MAX 25 CHARS) >>"
           ACCEPT WS-NEW-FULLNAME.
           DISPLAY "ENTER BIRTHDAY (YYYYMMDD) >>"
           ACCEPT WS-NEW-BIRTHDAY.
           DISPLAY "ENTER COURSE (MAX 15 CHARS) >>"
           ACCEPT WS-NEW-COURSE.

           PERFORM 0150-GET-DATE.

           MOVE COUNT-RECORDS TO STUD-IDX-ID.
           MOVE WS-NEW-FULLNAME TO STUD-IDX-FULLNAME.
           MOVE WS-NEW-BIRTHDAY TO STUD-IDX-BIRTHDATE.
           MOVE WS-NEW-COURSE TO STUD-IDX-COURSE.
           MOVE WS-CURRENT-DATE TO STUD-IDX-INSERT.
           MOVE ZEROES TO STUD-IDX-UPDATE.

           DISPLAY STUDENT-IDX-RECORD.

           WRITE STUDENT-IDX-RECORD
               INVALID KEY DISPLAY "INVALID WRITE"
               NOT INVALID KEY
                   PERFORM 0170-MOVE-IDX-TO-DET
                   DISPLAY "<--- NEW STUDENT DETAILS --->"
                   DISPLAY FRM-LINE-SEP
                   DISPLAY FRM-LINE-COLUMNS
                   DISPLAY FRM-LINE-SEP
                   DISPLAY STUDENT-LINE-DETAIL
                   DISPLAY FRM-LINE-SEP
           END-WRITE.

           CLOSE STUDENTINDEX.
       0220-END-STUDENT-INSERT.

       0225-STUDENT-INSERT-INDEX.
           ADD 1 TO COUNT-RECORDS.
           IF NOT (COUNT-RECORDS EQUALS STUD-IDX-ID)
               SET ENDOFFILE-IDX TO TRUE
               EXIT PARAGRAPH
           END-IF.
           READ STUDENTINDEX NEXT RECORD
               AT END
                   SET ENDOFFILE-IDX TO TRUE
                   ADD 1 TO COUNT-RECORDS
           END-READ.
       0225-END-STUDENT-INSERT-INDEX.

       0230-STUDENT-UPDATE.
           INITIALIZE WS-STUD-FOUND.
           OPEN I-O STUDENTINDEX.

           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|       U P D A T E   S T U D E N T      |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY SPACES.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
               CLOSE STUDENTINDEX
               EXIT PARAGRAPH
           END-IF.

           INITIALIZE WS-STUDENT-ID.
           DISPLAY "ENTER THE 4 DIGIT STUDENT ID >>".
           ACCEPT WS-STUDENT-ID.

           MOVE WS-STUDENT-ID TO STUD-IDX-ID.
           READ STUDENTINDEX
               KEY IS STUD-IDX-ID
               NOT INVALID KEY SET STUD-FOUND TO TRUE
           END-READ.

           IF NOT STUD-FOUND
               CLOSE STUDENTINDEX
               DISPLAY "STUDENT NOT FOUND, CANCELLING UPDATE PROCESS"
               EXIT PARAGRAPH
           END-IF.

           PERFORM 0170-MOVE-IDX-TO-DET.
           DISPLAY "<--- STUDENT TO BE UPDATED --->".
           DISPLAY FRM-LINE-SEP.
           DISPLAY FRM-LINE-COLUMNS.
           DISPLAY FRM-LINE-SEP.
           DISPLAY STUDENT-LINE-DETAIL.
           DISPLAY FRM-LINE-SEP.

           INITIALIZE WS-NEW-STUDENT.
           DISPLAY "ENTER THE DETAILS TO BE CHANGED".
           DISPLAY
           "ENTER THE NEW STUDENT NAME(MAX 25 CHAR) - SPACE TO SKIP >>".
           ACCEPT WS-NEW-FULLNAME.
           DISPLAY "NEW BIRTHDAY(YYYYMMDD) - SPACE TO SKIP >>".
           ACCEPT WS-NEW-BIRTHDAY.
           DISPLAY "NEW COURSE NAME(MAX 15 CHAR) - SPACE TO SKIP >>".
           ACCEPT WS-NEW-COURSE.

           IF NOT (WS-NEW-FULLNAME EQUAL SPACES)
               MOVE WS-NEW-FULLNAME TO STUD-IDX-FULLNAME
           END-IF.
           IF NOT (WS-NEW-BIRTHDAY EQUAL ZEROES)
               MOVE WS-NEW-BIRTHDAY TO STUD-IDX-BIRTHDATE
           END-IF.

           IF NOT (WS-NEW-COURSE EQUAL SPACES)
               MOVE WS-NEW-COURSE TO STUD-IDX-COURSE
           END-IF.

           PERFORM 0150-GET-DATE.
           MOVE WS-CURRENT-DATE TO STUD-IDX-UPDATE.

           REWRITE STUDENT-IDX-RECORD
               INVALID KEY DISPLAY "ERROR WITH UPDATE"
               NOT INVALID KEY
                   PERFORM 0170-MOVE-IDX-TO-DET
                   DISPLAY "<--- UPDATED STUDENT DETAILS --->"
                   DISPLAY FRM-LINE-SEP
                   DISPLAY FRM-LINE-COLUMNS
                   DISPLAY FRM-LINE-SEP
                   DISPLAY STUDENT-LINE-DETAIL
                   DISPLAY FRM-LINE-SEP
           END-REWRITE.

           CLOSE STUDENTINDEX.
       0230-END-STUDENT-UPDATE.

       0240-STUDENT-DELETE.
           INITIALIZE WS-STUD-FOUND.
           OPEN I-O STUDENTINDEX.

           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|       D E L E T E   S T U D E N T      |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY SPACES.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
               CLOSE STUDENTINDEX
               EXIT PARAGRAPH
           END-IF.

           DISPLAY "ENTER THE 4 DIGIT STUDENT ID >>".
           ACCEPT WS-STUDENT-ID.

           MOVE WS-STUDENT-ID TO STUD-IDX-ID.
           READ STUDENTINDEX
               KEY IS STUD-IDX-ID
               NOT INVALID KEY SET STUD-FOUND TO TRUE
           END-READ.

           IF NOT STUD-FOUND
               CLOSE STUDENTINDEX
               DISPLAY "STUDENT NOT FOUND, CANCELLING DELETE PROCESS"
               EXIT PARAGRAPH
           END-IF.

           PERFORM 0170-MOVE-IDX-TO-DET.
           DISPLAY FRM-LINE-SEP.
           DISPLAY FRM-LINE-COLUMNS.
           DISPLAY FRM-LINE-SEP.
           DISPLAY STUDENT-LINE-DETAIL.
           DISPLAY FRM-LINE-SEP.

           DISPLAY "ARE YOU SURE TO DELETE THE ABOVE STUDENT(Y/N) >>".
           ACCEPT WS-CONFIRMATION.

           IF CONFIRM-YES
               DELETE STUDENTINDEX
                   INVALID KEY DISPLAY "ERROR WITH DELETION"
                   NOT INVALID KEY DISPLAY
                       "<<----- DELETED THE ABOVE STUDENT ----->>"
               END-DELETE
           ELSE
               DISPLAY "DELETION CANCELLED"
           END-IF.

           CLOSE STUDENTINDEX.
       0240-END-STUDENT-DELETE.

       0250-QUERY-CLASS-ALL.
           OPEN INPUT STUDENTINDEX.

           IF NOT VALID-IDX AND NOT IDX-EOF
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
               CLOSE STUDENTINDEX
           END-IF.

           IF VALID-IDX OR IDX-EOF
               DISPLAY FRM-LINE-SEP
               DISPLAY FRM-HEADER-CLASS-REPORT
               DISPLAY FRM-LINE-SEP
               DISPLAY FRM-LINE-COLUMNS
               DISPLAY FRM-LINE-SEP

               PERFORM 0255-QUERY-CLASS-ALL-START

               DISPLAY FRM-LINE-SEP

               MOVE COUNT-RECORDS TO FRM-COUNT
               DISPLAY SPACES
               DISPLAY FRM-LINE-TOTAL
           END-IF.

           CLOSE STUDENTINDEX.
       0250-END-QUERY-CLASS-ALL.

       0255-QUERY-CLASS-ALL-START.
           INITIALIZE COUNT-RECORDS.

           READ STUDENTINDEX
               KEY IS STUD-IDX-ID
               AT END SET ENDOFFILE-IDX TO TRUE
           END-READ.

           PERFORM 0256-QUERY-CLASS-ALL-NEXT
               UNTIL ENDOFFILE-IDX.
       0255-END-QUERY-CLASS-ALL-START.

       0256-QUERY-CLASS-ALL-NEXT.
           ADD 1 TO COUNT-RECORDS.

           PERFORM 0170-MOVE-IDX-TO-DET.

           DISPLAY STUDENT-LINE-DETAIL.

           READ STUDENTINDEX NEXT RECORD
               AT END SET ENDOFFILE-IDX TO TRUE
           END-READ.
       0256-END-QUERY-CLASS-ALL-NEXT.

       0260-QUERY-ID.
           OPEN INPUT STUDENTINDEX.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
               CLOSE STUDENTINDEX
               EXIT PARAGRAPH
           END-IF.

           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|           QUERY STUDENT BY ID          |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY SPACES.

           INITIALIZE WS-STUDENT-ID.
           DISPLAY "ENTER STUDENT ID (MAX 4 DIGITS) >>".
           ACCEPT WS-STUDENT-ID.

           MOVE WS-STUDENT-ID TO STUD-IDX-ID.
           READ STUDENTINDEX
               KEY IS STUD-IDX-ID
               INVALID KEY DISPLAY "STUDENT INDEX FILE STATUS: ",
                   FILE-CHECK-KEY-IDX
           END-READ.

           IF IDX-NOTFOUND
               DISPLAY "STUDENT WITH ID " STUD-IDX-ID " NOT FOUND"
           ELSE
               PERFORM 0170-MOVE-IDX-TO-DET

               DISPLAY FRM-LINE-SEP
               DISPLAY FRM-LINE-COLUMNS
               DISPLAY FRM-LINE-SEP
               DISPLAY STUDENT-LINE-DETAIL
               DISPLAY FRM-LINE-SEP
               DISPLAY SPACES
           END-IF.

           CLOSE STUDENTINDEX.
       0260-END-QUERY-ID.

       0270-QUERY-INCLUSION-DATE.
           OPEN INPUT STUDENTINDEX.
           INITIALIZE COUNT-RECORDS.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
               CLOSE STUDENTINDEX
               EXIT PARAGRAPH
           END-IF.

           DISPLAY FRM-LINE-TITLE.
           DISPLAY "|   QUERY STUDENT BY DATE OF INCLUSION   |".
           DISPLAY FRM-LINE-TITLE.
           DISPLAY SPACES.

           INITIALIZE WS-INCLUSION-DATE.
           DISPLAY "ENTER DATE OF INCLUSION (YYYYMMDD) >>".
           ACCEPT WS-INCLUSION-DATE.

           MOVE WS-INCLUSION-YEAR TO FRM-DATE-YEAR.
           MOVE WS-INCLUSION-MONTH TO FRM-DATE-MONTH.
           MOVE WS-INCLUSION-DAY TO FRM-DATE-DAY.

           MOVE WS-INCLUSION-DATE TO STUD-IDX-INSERT.
           READ STUDENTINDEX
               KEY IS STUD-IDX-INSERT
               AT END SET ENDOFFILE-IDX TO TRUE
           END-READ.

           DISPLAY SPACES.
           DISPLAY FRM-LINE-INCL-DATE.
           DISPLAY FRM-LINE-SEP.
           DISPLAY FRM-LINE-COLUMNS.
           DISPLAY FRM-LINE-SEP.
           PERFORM 0275-QUERY-INCLUSION-NEXT UNTIL ENDOFFILE-IDX.
           DISPLAY FRM-LINE-SEP.

           MOVE COUNT-RECORDS TO FRM-COUNT.
           DISPLAY SPACES.
           DISPLAY FRM-LINE-TOTAL.

           CLOSE STUDENTINDEX.
       0270-END-QUERY-INCLUSION-DATE.

       0275-QUERY-INCLUSION-NEXT.
           IF STUD-IDX-INSERT = WS-INCLUSION-DATE
               ADD 1 TO COUNT-RECORDS
               PERFORM 0170-MOVE-IDX-TO-DET
               DISPLAY STUDENT-LINE-DETAIL
           END-IF.

           READ STUDENTINDEX NEXT RECORD
               AT END SET ENDOFFILE-IDX TO TRUE
           END-READ.
       0275-END-QUERY-INCLUSION-NEXT.


       0280-REPORT-COURSE.
           INITIALIZE COUNT-RECORDS WS-REP-COURSE.
           OPEN INPUT STUDENTINDEX.
           OPEN OUTPUT CLASSREPORT.

           IF NOT VALID-IDX
               DISPLAY "STUDENT INDEX FILE STATUS: " FILE-CHECK-KEY-IDX
           END-IF.

           IF NOT VALID-REPORT
               DISPLAY "REPORT FILE STATUS: " FILE-CHECK-KEY-REPORT
           END-IF.

           IF NOT VALID-IDX OR NOT VALID-REPORT
               CLOSE STUDENTINDEX CLASSREPORT
               EXIT PARAGRAPH
           END-IF.

           SORT SORTFILE ON ASCENDING KEY SORT-IDX-COURSE
               USING STUDENTINDEX
               OUTPUT PROCEDURE IS 0285-REPORT-COURSE-MAIN.

           CLOSE STUDENTINDEX CLASSREPORT.
           DISPLAY "CLASS REPORT WRITTEN SUCCESSFULLY".
       0280-END-REPORT-COURSE.

       0285-REPORT-COURSE-MAIN.
           RETURN SORTFILE INTO STUDENT-IDX-RECORD
               AT END SET END-SORTED TO TRUE
           END-RETURN.

           WRITE CLASS-REPORT-LINE FROM REPORT-LINE-SEP.
           WRITE CLASS-REPORT-LINE FROM FRM-HEADER-CLASS-REPORT
               AFTER ADVANCING 1 LINE.

           PERFORM 0288-REPORT-COURSE-SECTION UNTIL END-SORTED.

           WRITE CLASS-REPORT-LINE FROM REPORT-LINE-SEP
               AFTER ADVANCING 1 LINE.

           MOVE COUNT-RECORDS TO FRM-COUNT.
           WRITE CLASS-REPORT-LINE FROM FRM-LINE-TOTAL
               AFTER ADVANCING 2 LINES.
       0285-END-REPORT-COURSE-MAIN.

       0288-REPORT-COURSE-SECTION.
           ADD 1 TO COUNT-RECORDS.

           IF STUD-IDX-COURSE NOT EQUAL TO WS-REP-COURSE
               MOVE STUD-IDX-COURSE TO WS-REP-COURSE FRM-COURSE
               WRITE CLASS-REPORT-LINE FROM FRM-LINE-COURSE-BREAK
                   AFTER ADVANCING 2 LINES
               WRITE CLASS-REPORT-LINE FROM REPORT-LINE-SEP
                   AFTER ADVANCING 1 LINE
               WRITE CLASS-REPORT-LINE FROM REPORT-LINE-COLUMNS
                   AFTER ADVANCING 1 LINE
               WRITE CLASS-REPORT-LINE FROM REPORT-LINE-SEP
                   AFTER ADVANCING 1 LINE
           END-IF.

           PERFORM 0180-MOVE-IDX-TO-REP.
           WRITE CLASS-REPORT-LINE FROM REPORT-LINE-DETAIL
               AFTER ADVANCING 1 LINE.

           RETURN SORTFILE INTO STUDENT-IDX-RECORD
               AT END SET END-SORTED TO TRUE
           END-RETURN.
       0288-END-REPORT-COURSE-SECTION.


       0290-EXIT-MENU.
           DISPLAY "EXITING PROGRAM"
           PERFORM 9000-END-RUN.
       0290-END-EXIT-MENU.

       9000-END-RUN.
           STOP RUN.
           END PROGRAM STUDENT-REG.
