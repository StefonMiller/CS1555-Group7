--CS1555 Team 7
--Stefon Miller SMM248, Aaron Mathew aam150, Adam Buchinsky asb153
--



insert into mutual_fund (symbol,name,description,category,c_date) values('MM','money-market','money market,conservative','fixed','2020-01-06');
insert into mutual_fund (symbol,name,description,category,c_date) values('RE','real-estate','real estate','fixed','2020-01-09');
insert into mutual_fund (symbol,name,description,category,c_date) values('STB','short-term-bonds','short term bonds','bonds','2020-01-10');
insert into mutual_fund (symbol,name,description,category,c_date) values('LTB','long-term-bonds','long term bonds','bonds','2020-01-11');
insert into mutual_fund (symbol,name,description,category,c_date) values('BBS','balance-bonds-stocks','balance bonds and stocks','mixed','2020-01-12');
insert into mutual_fund (symbol,name,description,category,c_date) values('SRBS','social-response-bonds-stocks','social responsibility and stocks','mixed','2020-01-12');
insert into mutual_fund (symbol,name,description,category,c_date) values('GS','general-stocks','general stocks','stocks','2020-01-16');
insert into mutual_fund (symbol,name,description,category,c_date) values('AS','aggressive-stocks','aggressive stocks','stocks','2020-01-23');
insert into mutual_fund (symbol,name,description,category,c_date) values('IMS','international-markets-stock','international markets stock, risky','stocks','2020-01-30');

insert into closing_price(symbol,price,p_date) values ('MM',10.00,'2020-03-28'   );
insert into closing_price(symbol,price,p_date) values ('MM',11.00,'2020-03-29'   );
insert into closing_price(symbol,price,p_date) values ('MM',12.00,'2020-03-30'   );
insert into closing_price(symbol,price,p_date) values ('MM',15.00,'2020-03-31'   );
insert into closing_price(symbol,price,p_date) values ('MM',14.00,'2020-04-01'   );
insert into closing_price(symbol,price,p_date) values ('MM',15.00,'2020-04-02'   );
insert into closing_price(symbol,price,p_date) values ('MM',16.00,'2020-04-03'   );
insert into closing_price(symbol,price,p_date) values ('RE',10.00,'2020-03-28'   );
insert into closing_price(symbol,price,p_date) values ('RE',12.00,'2020-03-29'   );
insert into closing_price(symbol,price,p_date) values ('RE',15.00,'2020-03-30'   );
insert into closing_price(symbol,price,p_date) values ('RE',14.00,'2020-03-31'   );
insert into closing_price(symbol,price,p_date) values ('RE',16.00,'2020-04-01'   );
insert into closing_price(symbol,price,p_date) values ('RE',17.00,'2020-04-02'   );
insert into closing_price(symbol,price,p_date) values ('RE',15.00,'2020-04-03'   );
insert into closing_price(symbol,price,p_date) values ('STB',10.00,'2020-03-28'  );
insert into closing_price(symbol,price,p_date) values ('STB',9.00,'2020-03-29'   );
insert into closing_price(symbol,price,p_date) values ('STB',10.00,'2020-03-30'  );
insert into closing_price(symbol,price,p_date) values ('STB',12.00,'2020-03-31'  );
insert into closing_price(symbol,price,p_date) values ('STB',14.00,'2020-04-01'  );
insert into closing_price(symbol,price,p_date) values ('STB',10.00,'2020-04-02'  );
insert into closing_price(symbol,price,p_date) values ('STB',12.00,'2020-04-03'  );
insert into closing_price(symbol,price,p_date) values ('LTB',10.00,'2020-03-28'  );
insert into closing_price(symbol,price,p_date) values ('LTB',12.00,'2020-03-29'  );
insert into closing_price(symbol,price,p_date) values ('LTB',13.00,'2020-03-30'  );
insert into closing_price(symbol,price,p_date) values ('LTB',15.00,'2020-03-31'  );
insert into closing_price(symbol,price,p_date) values ('LTB',12.00,'2020-04-01'  );
insert into closing_price(symbol,price,p_date) values ('LTB',9.00,'2020-04-02'   );
insert into closing_price(symbol,price,p_date) values ('LTB',10.00,'2020-04-03'  );
insert into closing_price(symbol,price,p_date) values ('BBS',10.00,'2020-03-28'  );
insert into closing_price(symbol,price,p_date) values ('BBS',11.00,'2020-03-29'  );
insert into closing_price(symbol,price,p_date) values ('BBS',14.00,'2020-03-30'  );
insert into closing_price(symbol,price,p_date) values ('BBS',18.00,'2020-03-31'  );
insert into closing_price(symbol,price,p_date) values ('BBS',13.00,'2020-04-01'  );
insert into closing_price(symbol,price,p_date) values ('BBS',15.00,'2020-04-02'  );
insert into closing_price(symbol,price,p_date) values ('BBS',16.00,'2020-04-03'  );
insert into closing_price(symbol,price,p_date) values ('SRBS',10.00,'2020-03-28' );
insert into closing_price(symbol,price,p_date) values ('SRBS',12.00,'2020-03-29' );
insert into closing_price(symbol,price,p_date) values ('SRBS',12.00,'2020-03-30' );
insert into closing_price(symbol,price,p_date) values ('SRBS',14.00,'2020-03-31' );
insert into closing_price(symbol,price,p_date) values ('SRBS',17.00,'2020-04-01' );
insert into closing_price(symbol,price,p_date) values ('SRBS',20.00,'2020-04-02' );
insert into closing_price(symbol,price,p_date) values ('SRBS',20.00,'2020-04-03' );
insert into closing_price(symbol,price,p_date) values ('GS',10.00,'2020-03-28');
insert into closing_price(symbol,price,p_date) values ('GS',12.00,'2020-03-29');
insert into closing_price(symbol,price,p_date) values ('GS',13.00,'2020-03-30');
insert into closing_price(symbol,price,p_date) values ('GS',15.00,'2020-03-31');
insert into closing_price(symbol,price,p_date) values ('GS',14.00,'2020-04-01');
insert into closing_price(symbol,price,p_date) values ('GS',15.00,'2020-04-02');
insert into closing_price(symbol,price,p_date) values ('GS',12.00,'2020-04-03');
insert into closing_price(symbol,price,p_date) values ('AS',10.00,'2020-03-28');
insert into closing_price(symbol,price,p_date) values ('AS',15.00,'2020-03-29');
insert into closing_price(symbol,price,p_date) values ('AS',14.00,'2020-03-30');
insert into closing_price(symbol,price,p_date) values ('AS',16.00,'2020-03-31');
insert into closing_price(symbol,price,p_date) values ('AS',14.00,'2020-04-01');
insert into closing_price(symbol,price,p_date) values ('AS',17.00,'2020-04-02');
insert into closing_price(symbol,price,p_date) values ('AS',18.00,'2020-04-03');
insert into closing_price(symbol,price,p_date) values ('IMS',10.00,'2020-03-28' );
insert into closing_price(symbol,price,p_date) values ('IMS',12.00,'2020-03-29' );
insert into closing_price(symbol,price,p_date) values ('IMS',12.00,'2020-03-30' );
insert into closing_price(symbol,price,p_date) values ('IMS',14.00,'2020-03-31' );
insert into closing_price(symbol,price,p_date) values ('IMS',13.00,'2020-04-01' );
insert into closing_price(symbol,price,p_date) values ('IMS',12.00,'2020-04-02' );
insert into closing_price(symbol,price,p_date) values ('IMS',11.00,'2020-04-03' );


insert into customer(login,name,email,address,password,balance) values('mike','Mike Costa','mike@betterfuture.com','1st street','pwd',750.00);
insert into customer(login,name,email,address,password,balance) values('mary','Mary Chrysanthis','mary@betterfuture.com','2nd street','pwd',0.00);

insert into allocation(allocation_no, login, p_date) values (0,'mike','2020-03-28');
insert into allocation(allocation_no, login, p_date) values (1,'mary','2020-03-29');
insert into allocation(allocation_no, login, p_date) values (2,'mike','2020-04-03');

insert into prefers(allocation_no,symbol,percentage) values (0,'MM',0.50);
insert into prefers(allocation_no,symbol,percentage) values (0,'RE',0.50);
insert into prefers(allocation_no,symbol,percentage) values (1,'STB',0.50);
insert into prefers(allocation_no,symbol,percentage) values (1,'LTB',0.50);
insert into prefers(allocation_no,symbol,percentage) values (1,'BBS',0.50);
insert into prefers(allocation_no,symbol,percentage) values (2,'GS',0.50);
insert into prefers(allocation_no,symbol,percentage) values (2,'AS',0.50);
insert into prefers(allocation_no,symbol,percentage) values (2,'IMS',0.50);

insert into owns(login,symbol,shares) values ('mike','RE',50);

insert into administrator(login,name,email,address,password) values ('admin','Administrator','admin@betterfuture.com','5th Ave, Pitt','root');

insert into mutual_date(p_date) values('2020-04-04');
