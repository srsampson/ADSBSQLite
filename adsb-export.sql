BEGIN TRANSACTION;
DROP TABLE IF EXISTS `targethistory`;
CREATE TABLE IF NOT EXISTS `targethistory` (
	`record_num`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`flight_id`	INTEGER NOT NULL DEFAULT 0,
	`radar_id`	INTEGER NOT NULL DEFAULT 0,
	`acid`	TEXT NOT NULL,
	`utcdetect`	INTEGER NOT NULL,
	`utcfadeout`	INTEGER NOT NULL,
	`altitude`	INTEGER DEFAULT NULL,
	`groundSpeed`	REAL DEFAULT NULL,
	`groundTrack`	REAL DEFAULT NULL,
	`gsComputed`	REAL DEFAULT NULL,
	`gtComputed`	REAL DEFAULT NULL,
	`callsign`	TEXT DEFAULT NULL,
	`latitude`	REAL DEFAULT NULL,
	`longitude`	REAL DEFAULT NULL,
	`verticalRate`	INTEGER DEFAULT NULL,
	`squawk`	INTEGER DEFAULT NULL,
	`alert`	INTEGER NOT NULL DEFAULT 0,
	`emergency`	INTEGER NOT NULL DEFAULT 0,
	`spi`	INTEGER NOT NULL DEFAULT 0,
	`onground`	INTEGER NOT NULL DEFAULT 0,
	`hijack`	INTEGER NOT NULL DEFAULT 0,
	`comm_out`	INTEGER NOT NULL DEFAULT 0,
	`hadAlert`	INTEGER NOT NULL DEFAULT 0,
	`hadEmergency`	INTEGER NOT NULL DEFAULT 0,
	`hadSPI`	INTEGER NOT NULL DEFAULT 0,
	FOREIGN KEY(`acid`) REFERENCES `modestable`(`acid`)
);
DROP TABLE IF EXISTS `targetecho`;
CREATE TABLE IF NOT EXISTS `targetecho` (
	`record_num`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`flight_id`	INTEGER NOT NULL,
	`radar_id`	INTEGER NOT NULL DEFAULT 0,
	`acid`	TEXT NOT NULL,
	`utcdetect`	INTEGER NOT NULL,
	`altitude`	INTEGER DEFAULT NULL,
	`latitude`	REAL NOT NULL,
	`longitude`	REAL NOT NULL,
	`onground`	INTEGER NOT NULL DEFAULT 0,
	FOREIGN KEY(`acid`) REFERENCES `modestable`(`acid`)
);
DROP TABLE IF EXISTS `target`;
CREATE TABLE IF NOT EXISTS `target` (
	`flight_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`radar_id`	INTEGER NOT NULL DEFAULT 0,
	`acid`	TEXT NOT NULL,
	`utcdetect`	INTEGER NOT NULL,
	`utcupdate`	INTEGER NOT NULL,
	`altitude`	INTEGER DEFAULT NULL,
	`groundSpeed`	REAL DEFAULT NULL,
	`groundTrack`	REAL DEFAULT NULL,
	`gsComputed`	REAL DEFAULT NULL,
	`gtComputed`	REAL DEFAULT NULL,
	`callsign`	TEXT DEFAULT NULL,
	`latitude`	REAL DEFAULT NULL,
	`longitude`	REAL DEFAULT NULL,
	`verticalRate`	INTEGER DEFAULT NULL,
	`quality`	INTEGER DEFAULT NULL,
	`squawk`	INTEGER DEFAULT NULL,
	`alert`	INTEGER NOT NULL DEFAULT 0,
	`emergency`	INTEGER NOT NULL DEFAULT 0,
	`spi`	INTEGER NOT NULL DEFAULT 0,
	`onground`	INTEGER NOT NULL DEFAULT 0,
	`hijack`	INTEGER NOT NULL DEFAULT 0,
	`comm_out`	INTEGER NOT NULL DEFAULT 0,
	`hadAlert`	INTEGER NOT NULL DEFAULT 0,
	`hadEmergency`	INTEGER NOT NULL DEFAULT 0,
	`hadSPI`	INTEGER NOT NULL DEFAULT 0,
	FOREIGN KEY(`acid`) REFERENCES `modestable`(`acid`)
);
DROP TABLE IF EXISTS `modestable`;
CREATE TABLE IF NOT EXISTS `modestable` (
	`acid`	TEXT NOT NULL,
	`utcdetect`	INTEGER NOT NULL,
	`utcupdate`	INTEGER NOT NULL,
	`acft_reg`	TEXT DEFAULT NULL,
	`acft_model`	TEXT DEFAULT NULL,
	`acft_operator`	TEXT DEFAULT NULL,
	PRIMARY KEY(`acid`)
);
DROP TABLE IF EXISTS `metrics`;
CREATE TABLE IF NOT EXISTS `metrics` (
	`seq_num`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`utcupdate`	INTEGER NOT NULL,
	`callsignCount`	INTEGER NOT NULL,
	`surfaceCount`	INTEGER NOT NULL,
	`airborneCount`	INTEGER NOT NULL,
	`velocityCount`	INTEGER NOT NULL,
	`altitudeCount`	INTEGER NOT NULL,
	`squawkCount`	INTEGER NOT NULL,
	`trackCount`	INTEGER NOT NULL,
	`radar_id`	INTEGER NOT NULL
);
DROP TABLE IF EXISTS `metar`;
CREATE TABLE IF NOT EXISTS `metar` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`airport`	TEXT NOT NULL,
	`utcupdate`	INTEGER NOT NULL,
	`utcObserve`	TEXT NOT NULL,
	`temp`	INTEGER NOT NULL,
	`dewpoint`	INTEGER NOT NULL,
	`humidity`	INTEGER NOT NULL,
	`altimeter`	REAL NOT NULL,
	`pressureAlt`	INTEGER NOT NULL,
	`windDirection`	INTEGER NOT NULL,
	`windSpeed`	INTEGER NOT NULL,
	`windGust`	INTEGER NOT NULL
);
DROP TABLE IF EXISTS `callsign`;
CREATE TABLE IF NOT EXISTS `callsign` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`acid`	TEXT NOT NULL,
	`utcdetect`	INTEGER NOT NULL,
	`utcupdate`	INTEGER NOT NULL,
	`callsign`	TEXT NOT NULL,
	`flight_id`	INTEGER NOT NULL,
	`radar_id`	INTEGER NOT NULL,
	FOREIGN KEY(`acid`) REFERENCES `modestable`(`acid`)
);
DROP INDEX IF EXISTS `Index_reg`;
CREATE INDEX IF NOT EXISTS `Index_reg` ON `modestable` (
	`acft_reg`
);
DROP INDEX IF EXISTS `Index_operator`;
CREATE INDEX IF NOT EXISTS `Index_operator` ON `modestable` (
	`acft_operator`
);
DROP INDEX IF EXISTS `Index_model`;
CREATE INDEX IF NOT EXISTS `Index_model` ON `modestable` (
	`acft_model`
);
DROP INDEX IF EXISTS `Index_callsign`;
CREATE UNIQUE INDEX IF NOT EXISTS `Index_callsign` ON `callsign` (
	`acid`,
	`callsign`,
	`flight_id`,
	`radar_id`
);
DROP INDEX IF EXISTS `FltIDIndex`;
CREATE UNIQUE INDEX IF NOT EXISTS `FltIDIndex` ON `target` (
	`flight_id`,
	`radar_id`,
	`acid`
);
DROP INDEX IF EXISTS `FK_targethistory_acid`;
CREATE INDEX IF NOT EXISTS `FK_targethistory_acid` ON `targethistory` (
	`acid`
);
DROP INDEX IF EXISTS `FK_targetecho_acid`;
CREATE INDEX IF NOT EXISTS `FK_targetecho_acid` ON `targetecho` (
	`acid`
);
DROP INDEX IF EXISTS `FK_callsign_acid`;
CREATE INDEX IF NOT EXISTS `FK_callsign_acid` ON `callsign` (
	`acid`
);
DROP INDEX IF EXISTS `FK_acid`;
CREATE INDEX IF NOT EXISTS `FK_acid` ON `target` (
	`acid`
);
DROP TRIGGER IF EXISTS `updatemodes`;
CREATE TRIGGER updatemodes BEFORE UPDATE ON target FOR EACH ROW BEGIN UPDATE modestable SET utcupdate=NEW.utcupdate WHERE acid=NEW.acid; END;
DROP TRIGGER IF EXISTS `insertmodes`;
CREATE TRIGGER insertmodes BEFORE INSERT ON target BEGIN INSERT OR REPLACE INTO modestable (acid,utcdetect,utcupdate) VALUES (NEW.acid, NEW.utcupdate, NEW.utcupdate); END;
COMMIT;
