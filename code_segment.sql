CREATE DATABASE runcode;

USE runcode;

CREATE TABLE `code_segment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `code_content` varchar(4000) CHARACTER SET utf8mb4 NOT NULL,
  `code_type` varchar(20) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;