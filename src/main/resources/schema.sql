-- schema.sql
CREATE TABLE STUDENT (
                         ID BIGINT PRIMARY KEY,
                         NAME VARCHAR(255),
                         CLAZZ VARCHAR(255)
);

CREATE TABLE COMPANY (
                         ID BIGINT PRIMARY KEY,
                         NAME VARCHAR(255),
                         ADDRESS VARCHAR(255)
);

CREATE TABLE DOCUMENT (
                          ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                          DOCUMENT_TYPE VARCHAR(50),
                          STUDENT_ID BIGINT,
                          COMPANY_ID BIGINT,
                          FILE_PATH VARCHAR(255),
                          CREATED_DATE TIMESTAMP,
                          FOREIGN KEY (STUDENT_ID) REFERENCES STUDENT(ID),
                          FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID)
);
