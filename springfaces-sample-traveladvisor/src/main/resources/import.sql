insert into City(country,name) values ('UK', 'Bath');
insert into City(country,name) values ('USA', 'Melbourne');
insert into City(country,name) values ('Australia', 'Melbourne');

insert into Hotel(city_id,name) values (1,'The Royal');

insert into Review(hotel_id,index,checkInDate,rating,tripType,title,details) values (1,0,'1999-01-01',0,0,'Relaxing and Nice','This hote was nice');
insert into Review(hotel_id,index,checkInDate,rating,tripType,title,details) values (1,1,'1999-01-01',4,0,'Relaxing and Nice','This hote was nice');
