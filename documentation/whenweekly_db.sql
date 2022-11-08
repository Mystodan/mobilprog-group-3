-- MySQL Script generated by MySQL Workbench
-- Mon Nov  7 17:53:56 2022
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema whenweekly
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema whenweekly
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `whenweekly` DEFAULT CHARACTER SET utf8 ;
USE `whenweekly` ;

-- -----------------------------------------------------
-- Table `whenweekly`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `whenweekly`.`users` ;

CREATE TABLE IF NOT EXISTS `whenweekly`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uuid` BINARY(16) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `whenweekly`.`events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `whenweekly`.`events` ;

CREATE TABLE IF NOT EXISTS `whenweekly`.`events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `description` VARCHAR(45) NULL,
  `owner` INT NULL,
  `start_date` DATETIME NULL,
  `end_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `event_owner_fk_idx` (`owner` ASC),
  CONSTRAINT `event_owner_fk`
    FOREIGN KEY (`owner`)
    REFERENCES `whenweekly`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `whenweekly`.`event_user_available`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `whenweekly`.`event_user_available` ;

CREATE TABLE IF NOT EXISTS `whenweekly`.`event_user_available` (
  `event_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`event_id`, `user_id`),
  INDEX `user_id_fk_idx` (`user_id` ASC),
  CONSTRAINT `event_id_fk`
    FOREIGN KEY (`event_id`)
    REFERENCES `whenweekly`.`events` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_id_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `whenweekly`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
