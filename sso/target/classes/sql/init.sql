prompt PL/SQL Developer import file
prompt Created on 2021年5月6日 星期四 by Administrator
set feedback off
set define off
prompt Creating OAUTH_CLIENT_DETAILS...
create table OAUTH_CLIENT_DETAILS
(
  client_id               VARCHAR2(128) not null,
  resource_ids            VARCHAR2(256),
  client_secret           VARCHAR2(256) not null,
  scope                   VARCHAR2(256) not null,
  authorized_grant_types  VARCHAR2(256),
  web_server_redirect_uri VARCHAR2(256),
  authorities             VARCHAR2(256),
  access_token_validity   NUMBER,
  refresh_token_validity  NUMBER,
  additional_information  VARCHAR2(4000),
  autoapprove             VARCHAR2(256)
)
tablespace TBS_DATA_DAT_TDM
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
grant select, insert, update, delete on OAUTH_CLIENT_DETAILS to AGG_TDM;

prompt Disabling triggers for OAUTH_CLIENT_DETAILS...
alter table OAUTH_CLIENT_DETAILS disable all triggers;
prompt Loading OAUTH_CLIENT_DETAILS...
insert into OAUTH_CLIENT_DETAILS (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
values ('declare', null, 'user123', 'read,write', 'password,refresh_token,authorization_code,implicit', 'http://127.0.0.1:8081/client/login', null, 604800, null, null, 'true');
insert into OAUTH_CLIENT_DETAILS (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
values ('web', null, 'user123', 'read,write', 'password,refresh_token,authorization_code,implicit', 'https://www.baidu.com', null, 604800, null, null, 'true');
commit;
prompt 2 records loaded
prompt Enabling triggers for OAUTH_CLIENT_DETAILS...
alter table OAUTH_CLIENT_DETAILS enable all triggers;
set feedback on
set define on
prompt Done.
