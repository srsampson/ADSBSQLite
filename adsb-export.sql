BEGIN TRANSACTION;
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
CREATE TABLE IF NOT EXISTS `modestable` (
	`acid`	TEXT NOT NULL,
	`utcdetect`	INTEGER NOT NULL,
	`utcupdate`	INTEGER NOT NULL,
	`acft_reg`	TEXT DEFAULT NULL,
	`acft_model`	TEXT DEFAULT NULL,
	`acft_operator`	TEXT DEFAULT NULL,
	PRIMARY KEY(`acid`)
);
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
CREATE INDEX IF NOT EXISTS `Index_reg` ON `modestable` (
	`acft_reg`
);
CREATE INDEX IF NOT EXISTS `Index_operator` ON `modestable` (
	`acft_operator`
);
CREATE INDEX IF NOT EXISTS `Index_model` ON `modestable` (
	`acft_model`
);
CREATE UNIQUE INDEX IF NOT EXISTS `Index_callsign` ON `callsign` (
	`acid`,
	`callsign`,
	`flight_id`,
	`radar_id`
);
CREATE UNIQUE INDEX IF NOT EXISTS `FltIDIndex` ON `target` (
	`flight_id`,
	`radar_id`,
	`acid`
);
CREATE INDEX IF NOT EXISTS `FK_targethistory_acid` ON `targethistory` (
	`acid`
);
CREATE INDEX IF NOT EXISTS `FK_targetecho_acid` ON `targetecho` (
	`acid`
);
CREATE INDEX IF NOT EXISTS `FK_callsign_acid` ON `callsign` (
	`acid`
);
CREATE INDEX IF NOT EXISTS `FK_acid` ON `target` (
	`acid`
);
CREATE TRIGGER updatemodes BEFORE UPDATE ON target FOR EACH ROW BEGIN UPDATE modestable SET utcupdate=NEW.utcupdate WHERE acid=NEW.acid; END;
CREATE TRIGGER insertmodes BEFORE INSERT ON target BEGIN INSERT OR REPLACE INTO modestable (acid,utcdetect,utcupdate) VALUES (NEW.acid, NEW.utcupdate, NEW.utcupdate); END;
COMMIT;
