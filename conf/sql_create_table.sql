CREATE TABLE `tasks` (
  `id` varchar(254) NOT NULL,
  `topic` varchar(254) NOT NULL,
  `description` varchar(2000) NOT NULL,
  `created` date DEFAULT NULL,
  `started` date DEFAULT NULL,
  `finished` date DEFAULT NULL,
  `deleted` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1