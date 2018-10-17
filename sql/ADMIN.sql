-- Admin creation and access granting
CREATE USER ADMIN IDENTIFIED BY ADMIN;
GRANT CONNECT, RESOURCE, CREATE SESSION, DBA TO ADMIN;

CONN ADMIN/ADMIN;

CREATE TABLE TEACHER_VAULT (
    TNAME VARCHAR2(20),
    TPASSWORD  VARCHAR2(30),
    CONSTRAINT PK_TEACHER_VAULT PRIMARY KEY (TNAME)
);

INSERT INTO TEACHER_VAULT VALUES ('DEMOTEACHER', 'DEMOPASSWORD');

CREATE TABLE MANAGERSECTIONHUB (
    TNAME VARCHAR2(25),
    SECTION VARCHAR2(15),
    CONSTRAINT PK_MANAGERSECTIONHUB PRIMARY KEY (TNAME, SECTION)
);

INSERT INTO MANAGERSECTIONHUB VALUES ('DEMOTEACHER', 'DEMOSECTION');
INSERT INTO MANAGERSECTIONHUB VALUES ('DEMOTEACHER', 'CSE16');

CREATE TABLE MANAGERCLASSHUB (
    TNAME VARCHAR2(25),
    SECTION VARCHAR2(15),
    CLASS VARCHAR2(15),
    CONSTRAINT PK_MANAGERCLASSHUB PRIMARY KEY (TNAME, SECTION, CLASS)
);

INSERT INTO MANAGERCLASSHUB VALUES ('DEMOTEACHER', 'DEMOSECTION', 'DEMOCLASS');
INSERT INTO MANAGERCLASSHUB VALUES ('DEMOTEACHER', 'CSE16', 'CSE9406');
