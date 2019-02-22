--breaker数据表，创建时间：2019/2/17

--用户信息表
CREATE SEQUENCE user_info_id_seq START WITH 1;
CREATE TABLE user_info (
  id                BIGINT PRIMARY KEY DEFAULT nextval('user_info_id_seq'),
  user_name         VARCHAR(63)    NOT NULL,
  account           VARCHAR(127)   NOT NULL,
  password          VARCHAR(127)   NOT NULL,
  ban               BOOLEAN        NOT NULL,
  win               INT            NOT NULL
);
ALTER SEQUENCE user_info_id_seq OWNED BY user_info.id;
