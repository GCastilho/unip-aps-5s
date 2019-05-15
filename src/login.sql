CREATE TABLE login_data (
    ID int NOT NULL PRIMARY KEY ,
    name varchar(255) NOT NULL,
    password_enc varchar(130)
);
CREATE TABLE cookie (
    ID int NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    user_name varchar(255) NOT NULL,
    sessionID varchar(130),
    timeStamp varchar(30)
);
insert into login_data(ID,name,password_enc) values(0,'gabriel','FA585D89C851DD338A70DCF535AA2A92FEE7836DD6AFF1226583E88E0996293F16BC009C652826E0FC5C706695A03CDDCE372F139EFF4D13959DA6F1F5D3EABE');