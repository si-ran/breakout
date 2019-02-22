--breaker数据表，创建时间：2019/2/17

--用户信息表
create table user_info
(
	id int auto_increment,
	name text not null,
	account text not null,
	password text not null,
	win int not null,
	is_ban boolean not null,
	constraint user_info_pk
		primary key (id)
);

--用于测试
INSERT INTO BREAKER."PUBLIC".USER_INFO (NAME, ACCOUNT, PASSWORD, WIN, IS_BAN) VALUES ('joe', 'joe123', 'joe123', 0, FALSE);


