use mysql;

DROP DATABASE `cmpe275`;

CREATE DATABASE `cmpe275` /*!40100 DEFAULT CHARACTER SET utf8 */;

use cmpe275;

CREATE TABLE `mapper` (
  `idmapper` INT NOT NULL AUTO_INCREMENT,
  `nodeid` INT NOT NULL,
  `uuid` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`idmapper`));

  
CREATE TABLE `blobStorage` (
  `blobStorageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` varchar(100) NOT NULL,
  `caption` varchar(45) NOT NULL,
  `img` longblob NOT NULL,
  `contentLength` int(10) unsigned NOT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  `createdDate` datetime,
  `lastModifiedBy` varchar(45) DEFAULT NULL,
  `lastModifiedDate` datetime,
  PRIMARY KEY (`blobStorageId`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
