CREATE TABLE IF NOT EXISTS `basic_tb` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` varchar(11) NOT NULL,
  `Type` int(11) NOT NULL,
  `account` varchar(30) NOT NULL,
  `category` varchar(30) NOT NULL,
  `cost` float(15,2) NOT NULL,
  `currency` varchar(3) DEFAULT NULL,,
  `rate` float(8,4) DEFAULT NULL,
  `note` varchar(100) DEFAULT NULL,
  `makeyear` int(4) DEFAULT NULL,
  `makemonth` varchar(2) DEFAULT NULL,
  `makeday` varchar(2) DEFAULT NULL,
  `makedate` date NOT NULL,
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `exchange_rate` (
	`userID` varchar(11) NOT NULL,
  `currency` varchar(3) NOT NULL,
  `rate` float(8,4) NOT NULL,
  PRIMARY KEY (`userID`,`currency`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `account_tb` (
	`userID` varchar(11) NOT NULL,
	`account` varchar(30) NOT NULL,
	`remaining` float(15,2) NOT NULL,
	`currency` varchar(3) NOT NULL,
	`rate` float(8,4) NOT NULL,
	PRIMARY KEY (`userID`,`account`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `user_tb` (
  `userID` varchar(11) NOT NULL,
  `pwd` varchar(15) NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;