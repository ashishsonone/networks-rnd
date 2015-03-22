-- table for users for login
create table users(
	username VARCHAR(100) NOT NULL ,
	password VARCHAR(100) NOT NULL,
	PRIMARY KEY(username)
);

create table sessions(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name varchar(100) NOT NULL,
	description varchar(1000),
	datetime INTEGER UNSIGNED NOT NULL,
	user VARCHAR(100) NOT NULL,
	CONSTRAINT FOREIGN KEY(user) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
);


-- table for experiment data
create table experiments(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name varchar(100) NOT NULL,
	location varchar(100) NOT NULL,
	description varchar(1000),
	user VARCHAR(100) NOT NULL,
	filename VARCHAR(100) NOT NULL,
	datetime INTEGER UNSIGNED NOT NULL,
	sid INT NOT NULL,
	tracefilereceived INT NOT NULL DEFAULT 0,
	CONSTRAINT FOREIGN KEY(user) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FOREIGN KEY(sid) REFERENCES sessions(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- table for experiment details
create table experimentdetails(
	expid INT NOT NULL,
	macaddress CHAR(17) NOT NULL,
	osversion INT NOT NULL,
	wifiversion VARCHAR(30),
	numberofcores INT NOT NULL,
	storagespace INT NOT NULL,
	memory INT NOT NULL,
	processorspeed INT NOT NULL,
	wifisignalstrength INT NOT NULL,
	filereceived BOOL NOT NULL,
	CONSTRAINT FOREIGN KEY(expid) REFERENCES experiments(id) ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY(expid, macaddress)
);
