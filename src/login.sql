drop database if exists unip_aps_5s ;
create database unip_aps_5s ;
use unip_aps_5s;

CREATE TABLE credential (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    salt varchar(30),
    password_hash VARCHAR(128)
);
CREATE TABLE cookie (
    username VARCHAR(20) ,
    sessionID VARCHAR(128) ,
    timestamp LONG
);
alter table cookie add constraint primary key(username,sessionID);

insert into credential(username,password_hash) values('gabriel','FA585D89C851DD338A70DCF535AA2A92FEE7836DD6AFF1226583E88E0996293F16BC009C652826E0FC5C706695A03CDDCE372F139EFF4D13959DA6F1F5D3EABE');
insert into credential(username,password_hash) values('user','B109F3BBBC244EB82441917ED06D618B9008DD09B3BEFD1B5E07394C706A8BB980B1D7785E5976EC049B46DF5F1326AF5A2EA6D103FD07C95385FFAB0CACBC86');
