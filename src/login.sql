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
    username VARCHAR(20),
    sessionID VARCHAR(128) UNIQUE,
    timestamp LONG
);
alter table cookie add constraint primary key(username,sessionID);

insert into credential(username,password_hash) values('gabriel','FA585D89C851DD338A70DCF535AA2A92FEE7836DD6AFF1226583E88E0996293F16BC009C652826E0FC5C706695A03CDDCE372F139EFF4D13959DA6F1F5D3EABE');
insert into credential(username,password_hash) values('user','B109F3BBBC244EB82441917ED06D618B9008DD09B3BEFD1B5E07394C706A8BB980B1D7785E5976EC049B46DF5F1326AF5A2EA6D103FD07C95385FFAB0CACBC86');
insert into credential(username,password_hash) values('GCastilho','4DFF4EA340F0A823F15D3F4F01AB62EAE0E5DA579CCB851F8DB9DFE84C58B2B37B89903A740E1EE172DA793A6E79D560E5F7F9BD058A12A280433ED6FA46510A');
insert into credential(username,password_hash) values('root','4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a');