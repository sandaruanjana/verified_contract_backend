DROP TABLE IF EXISTS ABILITY;

CREATE TABLE ABILITY
(
    ID         varchar(45)  NOT NULL,
    NAME       varchar(100) NOT NULL,
    IS_ENABLED tinyint(1) DEFAULT '1',
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

INSERT INTO ABILITY (ID, NAME)
VALUES (UUID(), 'Ability 1');
INSERT INTO ABILITY (ID, NAME)
VALUES (UUID(), 'Ability 2');
INSERT INTO ABILITY (ID, NAME)
VALUES (UUID(), 'Ability 3');
INSERT INTO ABILITY (ID, NAME)
VALUES (UUID(), 'Ability 4');
INSERT INTO ABILITY (ID, NAME)
VALUES (UUID(), 'Ability 5');

CREATE TABLE IF EXISTS SKILL;

CREATE TABLE SKILL
(
    ID         varchar(45)  NOT NULL,
    TYPE       varchar(45)  NOT NULL,
    NAME       varchar(100) NOT NULL,
    IS_ENABLED tinyint(1) DEFAULT '1',
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Residential New Construction');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Commercial Remodel');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Concrete');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Electrical');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Plumbing');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Roofing');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Lawn Care/ Landscaping');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Arborist/Tree maintenance & removal');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Flooring (carpet, tile, wood)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Cabinetry (cabinet install & custom cabinetry)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Pool Care');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Custom counterparts (Granite, tile, solid surface, concrete)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Painters ( interior/exterior), powder coating, Power washing');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Metal worker (welder, Machinist)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Auto mechanic');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Appliance repair');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Architect');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Printer (Photography, custom printing)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Website Development');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Scrap Removal, Hauling');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Moving Service');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Service-Home Remodel', 'Computer Repair');

INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Education', 'Tutor (Math, Science, Spelling, History)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Education', 'Music Lessons (Guitar, Piano, Woodwind, Brass, Drums, Singing)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Education',
        'Arts instruction (painting, pottery, woodworking, metal working, sewing, knitting, glass-work)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Education', 'Technology (Computer, CAD, Photoshop, Video editing, Music editing');

INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Painter');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Sculptor');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Woodworker (Furniture, Woodturner, Carver)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Luther (Instrument maker, repair)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Furniture Repair/Restoration');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Glass (stained glass, glass blower)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Metal worker');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Musician, Band, DJ');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Digital Artist (Photoshop, Music, Video, CAD, Graphic Designer)');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Photographer');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Interior Designer');
INSERT INTO SKILL (ID, TYPE, NAME)
VALUES (UUID(), 'Arts', 'Upholstery');

DROP TABLE IF EXISTS ROLE;

CREATE TABLE ROLE
(
    ID         varchar(45) NOT NULL,
    NAME       varchar(50) NOT NULL,
    IS_ENABLED tinyint(1) DEFAULT '1',
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

INSERT INTO ROLE (ID, NAME)
VALUES (UUID(), 'CUSTOMER');
INSERT INTO ROLE (ID, NAME)
VALUES (UUID(), 'CONTRACTOR');

DROP TABLE IF EXISTS USER;

CREATE TABLE USER
(
    ID              varchar(45)  NOT NULL,
    ROLE_ID         varchar(45)  NOT NULL,
    NAME            varchar(100) NOT NULL,
    PROFILE_PICTURE varchar(100) DEFAULT NULL,
    EMAIL           varchar(50)  NOT NULL,
    PASSWORD        varchar(100) NOT NULL,
    TELEPHONE       varchar(20)  NOT NULL,
    ADDRESS_LINE_1  varchar(100) NOT NULL,
    ADDRESS_LINE_2  varchar(100) NOT NULL,
    ZIP_CODE        varchar(10)  NOT NULL,
    LONGITUDE       varchar(11)  NOT NULL,
    LATITUDE        varchar(11)  NOT NULL,
    SMALL_INFO      varchar(200) DEFAULT NULL,
    BIO             varchar(500) DEFAULT NULL,
    FACEBOOK_URL    varchar(100) DEFAULT NULL,
    TWITTER_URL     varchar(100) DEFAULT NULL,
    LINKEDIN_URL    varchar(100) DEFAULT NULL,
    CREATED_TIME    timestamp    NOT NULL,
    IS_ENABLED      tinyint(1) DEFAULT '1',
    UNIQUE (EMAIL),
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS USER_PREFERRED_ZIP_CODE;

CREATE TABLE USER_PREFERRED_ZIP_CODE
(
    ID        varchar(45) NOT NULL,
    USER_ID   varchar(45) NOT NULL,
    ZIP_CODE  varchar(10) NOT NULL,
    LONGITUDE varchar(11) NOT NULL,
    LATITUDE  varchar(11) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS USER_ABILITY;

CREATE TABLE USER_ABILITY
(
    ID         varchar(45) NOT NULL,
    USER_ID    varchar(45) NOT NULL,
    ABILITY_ID varchar(45) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS USER_SKILL;

CREATE TABLE USER_SKILL
(
    ID       varchar(45) NOT NULL,
    USER_ID  varchar(45) NOT NULL,
    SKILL_ID varchar(45) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS USER_FAVOURITE_USER;

CREATE TABLE USER_FAVOURITE_USER
(
    ID                varchar(45) NOT NULL,
    USER_ID           varchar(45) NOT NULL,
    FAVOURITE_USER_ID varchar(45) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS USER_IMAGE;

CREATE TABLE USER_IMAGE
(
    ID               varchar(45)  NOT NULL,
    USER_ID          varchar(45)  NOT NULL,
    PROJECT_IMAGE_ID varchar(45) DEFAULT NULL,
    NAME             varchar(100) NOT NULL,
    DESCRIPTION      varchar(500) NOT NULL,
    IS_PUBLIC        tinyint(1) DEFAULT '1',
    UPLOAD_TIME      timestamp    NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS USER_RATING;

CREATE TABLE USER_RATING
(
    ID               varchar(45)   NOT NULL,
    PROJECT_ID       varchar(45)   NOT NULL,
    USER_ID          varchar(45)   NOT NULL,
    REVIEWEE_USER_ID varchar(45)   NOT NULL,
    RATE             decimal(2, 2) NOT NULL,
    COMMENT          varchar(500) DEFAULT NULL,
    CREATED_TIME     timestamp     NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS REQUEST_USER_PASSWORD;

CREATE TABLE REQUEST_USER_PASSWORD
(
    ID           varchar(45) NOT NULL,
    USER_ID      varchar(45) NOT NULL,
    CREATED_TIME timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS PROJECT;

CREATE TABLE PROJECT
(
    ID                   varchar(45)  NOT NULL,
    USER_ID              varchar(45)  NOT NULL,
    ASSIGN_USER_ID       varchar(45)  DEFAULT NULL,
    NAME                 varchar(100) NOT NULL,
    PREFERRED_DATE       date         NOT NULL,
    ADDRESS_LINE_1       varchar(100) NOT NULL,
    ADDRESS_LINE_2       varchar(100) NOT NULL,
    NATURE               varchar(100) NOT NULL,
    ZIP_CODE             varchar(10)  NOT NULL,
    LONGITUDE            varchar(11)  NOT NULL,
    LATITUDE             varchar(11)  NOT NULL,
    CATEGORY             varchar(100) NOT NULL,
    CATEGORY_ONE_ID      varchar(45)  DEFAULT NULL,
    CATEGORY_TWO_ID      varchar(45)  DEFAULT NULL,
    CATEGORY_THREE_ID    varchar(45)  DEFAULT NULL,
    SPECIAL_INSTRUCTIONS varchar(500) NOT NULL,
    STATUS               varchar(20)  NOT NULL,
    REJECT_REASON        varchar(500) DEFAULT NULL,
    IS_ACTION            tinyint(1) DEFAULT '1',
    IS_REQUEST_QUOTATION tinyint(1) DEFAULT '0',
    IS_PUBLIC            tinyint(1) DEFAULT '1',
    CREATED_TIME         timestamp    NOT NULL,
    IS_ENABLED           tinyint(1) DEFAULT '1',
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS PROJECT_IMAGE;

CREATE TABLE PROJECT_IMAGE
(
    ID          varchar(45)  NOT NULL,
    PROJECT_ID  varchar(45)  NOT NULL,
    NAME        varchar(100) NOT NULL,
    DESCRIPTION varchar(500) NOT NULL,
    UPLOAD_TIME timestamp    NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS PROJECT_BID;

CREATE TABLE PROJECT_BID
(
    ID           varchar(45)    NOT NULL,
    PROJECT_ID   varchar(45)    NOT NULL,
    USER_ID      varchar(45)    NOT NULL,
    AMOUNT       decimal(10, 2) NOT NULL,
    CREATED_TIME timestamp      NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS PROJECT_PROGRESS;

CREATE TABLE PROJECT_PROGRESS
(
    ID           varchar(45)  NOT NULL,
    PROJECT_ID   varchar(45)  NOT NULL,
    WEEK         int(3) NOT NULL,
    TITLE        varchar(100) NOT NULL,
    DESCRIPTION  varchar(500) NOT NULL,
    CREATED_TIME timestamp    NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS PROJECT_PROGRESS_IMAGE;

CREATE TABLE PROJECT_PROGRESS_IMAGE
(
    ID                  varchar(45)  NOT NULL,
    PROJECT_PROGRESS_ID varchar(45)  NOT NULL,
    PROJECT_ID          varchar(45)  NOT NULL,
    NAME                varchar(100) NOT NULL,
    DESCRIPTION         varchar(500) NOT NULL,
    UPLOAD_TIME         timestamp    NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;
