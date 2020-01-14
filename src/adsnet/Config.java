package adsnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * A Class to store configuration parameters.
 */
public final class Config {

    private int socketPort;
    private int radarid;
    private int radarscan;
    private int databaseTargetTimeout;
    private String socketIP;
    private String databaseName;
    private boolean disablegui;
    //
    private Properties Props;
    private String userDir;
    private String fileSeparator;
    private String OSConfPath;

    public Config(String val) {
        String temp;

        socketIP = "127.0.0.1";
        radarscan = 3;
        socketPort = 30003;
        databaseTargetTimeout = 3;    // 3 minutes
        //
        Props = null;
        //
        userDir = System.getProperty("user.dir");
        fileSeparator = System.getProperty("file.separator");
        OSConfPath = userDir + fileSeparator + val;

        /*
         * Read the config file into the Properties class Note: The file must
         * exist, or we can not proceed.
         */
        try {
            FileInputStream in = new FileInputStream(OSConfPath);
            Props = new Properties();
            Props.load(in);
        } catch (IOException e) {
            System.out.println("ADSBSQLite::Config Fatal: Unable to read configuration file " + OSConfPath);
            System.exit(-1);
        }

        /*
         * Now check which properties were selected, and take the defaults if
         * none given.
         */
        if (Props != null) {
            temp = Props.getProperty("radar.id");
            if (temp == null) {
                radarid = 0;
                System.out.println("radar.id not set, set to 0");
            } else {
                try {
                    radarid = Integer.parseInt(temp.trim());
                } catch (NumberFormatException e) {
                    radarid = 0;
                }
            }

            temp = Props.getProperty("radar.scan");
            if (temp == null) {
                radarscan = 3;
                System.out.println("radar.scan not set, set to 3 seconds");
            } else {
                try {
                    radarscan = Integer.parseInt(temp.trim());

                    if (radarscan < 1) {
                        radarscan = 1;
                    } else if (radarscan > 13) {
                        radarscan = 13;
                    }
                } catch (NumberFormatException e) {
                    radarscan = 3;
                }
            }

            temp = Props.getProperty("gui.disable");
            if (temp == null) {
                disablegui = false;
                System.out.println("gui.disable not set, set to false");
            } else {
                disablegui = temp.trim().toLowerCase().equals("true");
            }

            temp = Props.getProperty("socket.address");
            if (temp == null) {
                socketIP = "127.0.0.1";
                System.out.println("socket.address not set, set to 127.0.0.1");
            } else {
                socketIP = temp.trim();
            }

            temp = Props.getProperty("socket.port");
            if (temp == null) {
                socketPort = 30003;
                System.out.println("socket.port not set, set to 30003");
            } else {
                try {
                    socketPort = Integer.parseInt(temp.trim());
                } catch (NumberFormatException e) {
                    socketPort = 30003;
                }
            }

            temp = Props.getProperty("db.targettimeout");
            if (temp == null) {
                databaseTargetTimeout = 3;
                System.out.println("db.targettimeout not set, set to 3 minutes");
            } else {
                try {
                    databaseTargetTimeout = Integer.parseInt(temp.trim());
                } catch (NumberFormatException e) {
                    databaseTargetTimeout = 3;
                }
            }

            temp = Props.getProperty("db.name");
            if (temp == null) {
                databaseName = "adsb.db";
                System.out.println("db.name not set, set to adsb.db");
            } else {
                databaseName = temp;
            }
        }
    }

    /**
     * Getter to provide for multiple detector data in the same database Each
     * network connect should be configured with a different radar ID
     *
     * @return a int Representing a numeric radar ID
     */
    public int getRadarID() {
        return this.radarid;
    }

    public int getRadarScanTime() {
        return this.radarscan;
    }

    /**
     * Getter to return whether the GUI display is wanted
     *
     * @return a boolean Where true means disable the GUI display
     */
    public boolean getDisableGui() {
        return this.disablegui;
    }

    /**
     * Getter to return the configuration Basestation TCP port
     *
     * @return an integer Representing the Basestation TCP port
     */
    public int getSocketPort() {
        return socketPort;
    }

    /**
     * Getter to return the Basestation IP or Hostname
     *
     * @return a string Representing the IP or Hostname of the Basestation host
     */
    public String getSocketHost() {
        return socketIP;
    }

    /**
     * Getter to return the filename path of the configuration file
     *
     * @return a string Representing the configuration file path
     */
    public String getOSConfPath() {
        return OSConfPath;
    }

    /**
     * Getter to return the database connection URL
     *
     * @return a string Representing the database URL
     */
    public String getDatabaseURL() {
        return "jdbc:sqlite:" + databaseName;
    }

    /**
     * Getter to return the database timeout value
     *
     * @return an int Representing the database timeout for archiving
     */
    public int getDatabaseTimeout() {
        return databaseTargetTimeout;
    }
}
