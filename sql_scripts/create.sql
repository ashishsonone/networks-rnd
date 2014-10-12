-- table for users for login
create table users(
	username VARCHAR(100) NOT NULL ,
	password VARCHAR(100) NOT NULL,
	PRIMARY KEY(username)
);

-- table for experiment data
create table experiments(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name varchar(100) NOT NULL,
	location varchar(100) DEFAULT 'IIT Bombay',
	description varchar(1000)
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
	FOREIGN KEY(expid) REFERENCES experiments(id),
	PRIMARY KEY(expid, macaddress)
);
