CREATE DATABASE IF NOT EXISTS Orderify;

USE Orderify;

DROP TABLE IF EXISTS addonsToWishes;             
DROP TABLE IF EXISTS wishes;                     
DROP TABLE IF EXISTS orders;                     
DROP TABLE IF EXISTS addonCategoriesToDishes;    
DROP TABLE IF EXISTS addons;                     
DROP TABLE IF EXISTS addonCategories;            
DROP TABLE IF EXISTS dishes;                     
DROP TABLE IF EXISTS dishCategories;             
DROP TABLE IF EXISTS messages;                
DROP TABLE IF EXISTS clients;                    
DROP TABLE IF EXISTS tables;                    

CREATE TABLE tables (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    number INTEGER, 
    description VARCHAR(50), 
    state INTEGER NOT NULL DEFAULT 1, 
    PRIMARY KEY (ID)
)   CHARSET=utf8;

CREATE TABLE clients (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    number INTEGER, 
    state INTEGER NOT NULL DEFAULT 1, 
    tableID INTEGER NOT NULL DEFAULT 1, 
    PRIMARY KEY (ID),
    FOREIGN KEY (tableID) REFERENCES tables(ID) 
)   CHARSET=utf8;

CREATE TABLE messages (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    content LONGTEXT NOT NULL, 
    PRIMARY KEY (ID)
)   CHARSET=utf8;


CREATE TABLE dishCategories (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    name VARCHAR(255) NOT NULL, 
    imgPath VARCHAR(255), 
    PRIMARY KEY (ID)
)   CHARSET=utf8;


CREATE TABLE dishes (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    name VARCHAR(50) NOT NULL, 
    price INTEGER NOT NULL DEFAULT 0, 
    descS VARCHAR(255), 
    descL LONGTEXT, 
    dishCategoryID INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (ID),
    FOREIGN KEY (dishCategoryID) REFERENCES dishCategories(ID) 
)   CHARSET=utf8;

CREATE TABLE addonCategories (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    name VARCHAR(255) NOT NULL, 
    description VARCHAR(100), 
    multiChoice BOOLEAN DEFAULT false, 
    PRIMARY KEY (ID)
)   CHARSET=utf8;

CREATE TABLE addons (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    name VARCHAR(255) NOT NULL, 
    price INTEGER NOT NULL DEFAULT 0, 
    addonCategoryID INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (ID),
    FOREIGN KEY (addonCategoryID) REFERENCES addonCategories(ID)
)   CHARSET=utf8;


CREATE TABLE addonCategoriesToDishes (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    dishID INTEGER NOT NULL DEFAULT 0, 
    addonCategoryID INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (ID),
    FOREIGN KEY (dishID) REFERENCES dishes(ID), 
    FOREIGN KEY (addonCategoryID) REFERENCES addonCategories(ID) 
)   CHARSET=utf8;


CREATE TABLE orders (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    time TIME NOT NULL DEFAULT '00:00:00', 
    date DATE NOT NULL DEFAULT '1970-1-1', 
    comments VARCHAR(255), 
    state INTEGER NOT NULL DEFAULT 1, 
    clientID INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (ID), 
    FOREIGN KEY (clientID) REFERENCES clients(ID)
)   CHARSET=utf8;

CREATE TABLE wishes (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    dishID INTEGER DEFAULT 0, 
    amount INTEGER DEFAULT 1, 
    orderID INTEGER DEFAULT 0, 
    PRIMARY KEY (ID), 
    FOREIGN KEY (dishID) REFERENCES dishes(ID),
    FOREIGN KEY (orderID) REFERENCES orders(ID)
)   CHARSET=utf8;


CREATE TABLE addonsToWishes (
    ID INTEGER NOT NULL AUTO_INCREMENT, 
    wishID INTEGER NOT NULL DEFAULT 0, 
    addonID INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (ID),
    FOREIGN KEY (wishID) REFERENCES wishes(ID),
    FOREIGN KEY (addonID) REFERENCES addons(ID)
)   CHARSET=utf8;


INSERT INTO tables (number, description, state)
VALUES  (1, 'Przy oknie', 1), (2, 'Przy barze', 1), (3, 'Na środku', 1), (4, 'W kącie', 1), (5, 'W odległej galaktyce', 1), (6, 'Na Żoliborzu', 1), (7, 'Na zewnątrz', 1);

INSERT INTO clients (number, state, tableID)
VALUES  (1, 1, 1), (2, 1, 1), (3, 1, 1), (4, 1, 1),
        (1, 1, 2), (2, 1, 2), (3, 1, 2), (4, 1, 2),
        (1, 1, 3), (2, 1, 3), (3, 1, 3), (4, 1, 3),
        (1, 1, 4), (2, 1, 4), (3, 1, 4), (4, 1, 4),
        (1, 1, 5), (2, 1, 5), (3, 1, 5), (4, 1, 5);

INSERT INTO messages (content)
VALUES  ('Tylko dzisiaj, kurczak w cieście bez kurczaka!'), ('Such design, big wow!');

INSERT INTO dishCategories (name)
VALUES  ('Pizza'), ('Dania główne'), ('Zupy'), ('Napoje'), ('Alkohole');

INSERT INTO dishes (name, price, descS, descL, dishCategoryID)
VALUES  ('Margarita', 15, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 1),        #1
        ('Peperoni', 17, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 1),         #2
        ('Hawajska', 18, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 1),         #3
        ('Kurczak w cieście', 13, 'takie on jest dobry', 'araby z południa będą przychodzić i oddawać swoje żony żeby spróbować tego wspaniałego dania', 2),                    #4
        ('Sznycel taki dobry, wspaniały i taki fantastyczny', 42.54, 'short', 'long', 2),                                                                                       #5
        ('Dewolaj', 15, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 2),          #6
        ('Zupa pomidorowa', 5, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 3),   #7
        ('Zupa ogórkowa', 5, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 3),     #8
        ('Herbata', 5, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 4),           #9
        ('Cola', 5, 'shokrótki opis daniart', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 4),         #10
        ('Sok pomarańczowy', 4, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 4),  #11
        ('Piwo', 5, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 5),              #12
        ('Wódka', 5, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 5),             #13
        ('Tequila', 6, 'krótki opis dania', 'długi opis dania mający na celu bliższe zapoznanie z jakością tego wspaniałego produktu oraz nakłonienie do zakupu', 5);           #14

INSERT INTO addonCategories (name, description, multiChoice) 
VALUES  ('Sosy', null, true),               #1
        ('Rozmiary', "do pizzy", false),    #2
        ('Sałatki', null, false),           #3
        ('Zapychacze', null, false),        #4 
        ('Do napojów', null, true),         #5
        ('Rozmiary', "do piwa", false),     #6
        ('Rozmiary', "do wódki", false);    #7

INSERT INTO addons (name, price, addonCategoryID)
VALUES  ('Sos tatarski', 1, 1), ('Sos czosnkowy', 1, 1), ('Sos pomidorowy', 1, 1),              #1-3
        ('Mała', 0, 2), ('Średnia', 7, 2), ('Duża', 13, 2),                                     #4-6
        ('Sałatka z kapusty', 0, 3), ('Warzywa gotowane', 0, 3), ('Marchewka', 0, 3),           #7-9
        ('Ziemniaki', 0, 4), ('Pieczone ziemniaki', 0, 4), ('Frytki', 0, 4), ('Kluski', 0, 4),  #10-13
        ('Lód', 0, 5), ('Cukier', 0, 5),                                                        #14-15
        ('0.4l', -1, 6), ('0.5l', 0, 6), ('1.0l', 4, 6),                                        #16-18
        ('30ml', -1, 7), ('50ml', 0, 7), ('0.5l', 35, 7);                                       #19-21

INSERT INTO addonCategoriesToDishes (dishID, addonCategoryID)
VALUES  (1, 1), (1, 2),
        (2, 1), (2, 2),
        (3, 1), (3, 2),
        (4, 3), (4, 4),
        (5, 3), (5, 4),
        (6, 3), (6, 4),
        (9, 5),
        (10, 5),
        (11, 5),
        (12, 6),
        (13, 7),
        (14, 7);

INSERT INTO orders (time, date, comments, state, clientID)
VALUES  ('21:37:00', '2018-07-31', 'z lodem', 1, 1),                                                                #1
        ('21:38:11', '2018-07-31', 'arkadiusz, designu prometeusz', 1, 2),                                          #2
        ('21:41:22', '2018-07-31', 'to ja szukam czy ci chlopi maja proce', 1, 5),                                  #3
        ('21:49:22', '2018-07-31', 'rzucam sobie na SQL', 1, 6),                                                    #4
        ('21:49:33', '2018-07-31', 'po co ja się w ogóle sile z tymi uwagami, nikt tego nie przeczyta :(', 1, 7),   #5
        ('21:49:33', '2018-07-31', 'podaje hasło!', 1, 9),                                                          #6
        ('21:49:33', '2018-07-31', 'napoje prosze przyniesć od razu', 1, 10),                                       #7
        ('21:49:33', '2018-07-31', 'ZTK, DVD, Hi-Fi, Midi, AGH, CBŚ, NPM, CKM, ZTM', 1, 13),                        #8
        ('21:49:33', '2018-07-31', 'Inne stoliki nie były obsłurzone, ja byłm obsłużony.', 1, 17),                  #9
        ('21:49:33', '2018-07-31', 'Ja byłem obsłużony, dla mnie była każda wódka i każda potrawa', 1, 17),         #10
        ('21:49:33', '2018-07-31', 'i każda muzyka w orkiestrze', 1, 17);                                           #11

 
INSERT INTO wishes (dishID, amount, orderID)
VALUES  (1, 1, 1), (9, 1, 1),                                       #1-2
        (5, 1, 2), (11, 1, 2),                                      #3-4
        (7, 1, 3),                                                  #5
        (9, 5, 4),                                                  #6
        (6, 1, 5), (5, 1, 5), (10, 2, 5), (7, 3, 5), (8, 1, 5),     #7-11
        (2, 1, 6), (3, 1, 6), (4, 1, 6),                            #12-14
        (7, 1, 7), (8, 1, 7),                                       #15-16
        (7, 1, 8), (8, 1, 8), (9, 1, 8), (10, 1, 8),                #17-20
        (12, 1, 9), (13, 2, 9), (14, 1, 9),                     #21-23
        (12, 1, 10), (13, 2, 10), (14, 1, 10),                  #24-26
        (12, 1, 11), (13, 2, 11), (14, 1, 11);                  #27-29

INSERT INTO addonsToWishes (wishID, addonID)
VALUES  (1, 2), (1, 3),
        (3, 7), (3, 10),
        (4, 14),
        (7, 9), (7, 12),
        (8, 7), (8, 10),
        (9, 14),
        (12, 5), 
        (13, 6),
        (14, 7), (14, 10),
        (20, 15),
        (21, 18),
        (22, 20),
        (23, 20),
        (24, 18),
        (25, 20),
        (26, 20),
        (27, 18),
        (28, 20),
        (29, 20);