package adsnet;

/*
 * This is the vehicle track object
 */
public final class Track {

    private String acid;            // Aircraft ID
    private String registration;    // N-Number if USA registered
    private int trackQuality;       // 0 - 9 quality value (9 means Best)
    private int verticalRate;       // fps
    private int verticalTrend;      // -1 = down, 0 = level, 1 = up
    private final int[] trend = new int[10];
    private static int trend_el = 0;
    private float groundSpeed;      // kts
    private float groundTrack;      // deg
    private float groundSpeedComputed;
    private float groundTrackComputed;
    private float latitude;         // aircraft position latitude (- is south)
    private float longitude;        // aircraft position longitude (- is west)
    private int altitude;           // aircraft current altitude in feet
    private String callsign;        // 8 character string
    private int squawk;             // 4 digit octal code
    //
    private boolean alert;          // octal code changed bit
    private boolean emergency;      // emergency bit
    private boolean spi;            // ident bit
    private boolean isOnGround;     // aircraft squat switch activated
    private boolean isVirtOnGround; // Virtual onGround for MMS2
    private boolean hijack;
    private boolean comm_out;
    //
    private boolean hadAlert;
    private boolean hadEmergency;
    private boolean hadSPI;
    //
    private long updateTime;        // zulu time object was updated
    private long updatePositionTime;// zulu time object lat/lon position was updated
    private boolean updated;        // set on update, cleared on sent
    private boolean updatePosition;

    public Track() {
        updateTime = System.currentTimeMillis();
        //
        acid = "";
        registration = "";
        callsign = "";
        //
        groundSpeed = -999.0F;
        groundTrack = -999.0F;
        groundSpeedComputed = -999.0F;
        groundTrackComputed = -999.0F;
        latitude = -999.0F;
        longitude = -999.0F;
        verticalRate = -9999;
        altitude = -9999;
        squawk = -9999;
        //
        trackQuality = 0;           // aircraft with positions
        updatePositionTime = 0L;    // time position updated
        //
        alert = false;
        emergency = false;
        spi = false;
        hadAlert = false;
        hadEmergency = false;
        hadSPI = false;
        hijack = false;
        comm_out = false;
        updated = false;
        updatePosition = false;
        isOnGround = false;
        isVirtOnGround = false;
    }

    /**
     * Method to increment track with position quality
     */
    public void incrementTrackQuality() {
        if (trackQuality < 9) {
            trackQuality++;
            updated = true;
        }
    }

    /**
     * Method to decrement track with position quality
     */
    public void decrementTrackQuality() {
        if (trackQuality > 0) {
            trackQuality--;
            updated = true;
        }
    }

    /**
     * Method to return track quality
     *
     * @return an integer representing the track quality [0...9]
     */
    public int getTrackQuality() {
        return trackQuality;
    }

    /**
     * Method to check if the track has been updated
     *
     * @return boolean which signals if the track has been updated
     */
    public boolean getUpdated() {
        return updated;
    }

    /**
     * Method to flag a track as being updated
     *
     * @param val a boolean which signals the track has been updated
     */
    public void setUpdated(boolean val) {
        updated = val;
    }

    /**
     * Method to check if the track position has been updated
     *
     * @return boolean which signals if the track position has been updated
     */
    public boolean getUpdatePosition() {
        return updatePosition;
    }

    /**
     * Method to flag a track position as being updated or not updated
     *
     * @param val a boolean to set or reset the track position updated status
     */
    public void setUpdatePosition(boolean val) {
        updatePosition = val;
    }

    /**
     * Method to return the Aircraft Mode-S Hex ID
     *
     * @return a string Representing the track Mode-S Hex ID
     */
    public String getAircraftID() {
        return acid;
    }

    /**
     * Method to set the Aircraft Mode-S Hex ID
     *
     * @param val a string Representing the track Mode-S Hex ID
     */
    public void setAircraftID(String val) {
        acid = val;
    }

    /**
     * Method to return the Aircraft N-Number Registration
     *
     * @return a string Representing the track registration
     */
    public String getRegistration() {
        return registration;
    }

    /**
     * Method to set the Aircraft N-Number Registration
     *
     * @param val a string Representing the track registration
     */
    public void setRegistration(String val) {
        registration = val;
    }

    /**
     * Method to return the tracks updated position time in milliseconds
     *
     * @return a long Representing the track updated position time in
     * milliseconds
     */
    public long getUpdatePositionTime() {
        return updatePositionTime;
    }

    /**
     * Method to return the tracks updated time in milliseconds
     *
     * @return a long Representing the track updated time in milliseconds
     */
    public long getUpdateTime() {
        return updateTime;
    }

    /**
     * Method to set the track updated time in milliseconds
     *
     * @param val a long Representing the track updated time in milliseconds
     */
    public void setUpdateTime(long val) {
        updateTime = val;
    }

    /**
     * Method to return the track vertical rate in feet per second The
     * resolution is +/- 64 fps, with descent being negative
     *
     * @return an integer Representing the track climb or descent rate
     */
    public int getVerticalRate() {
        return verticalRate;
    }

    /**
     * Method to set the track vertical rate in feet per second The resolution
     * is +/- 64 fps, with descent being negative
     *
     * @param val an integer Representing the track climb or descent rate
     */
    public void setVerticalRate(int val) {
        if (verticalRate != val) {
            verticalRate = val;
            updated = true;
            
            int vt = 0;

            if (val > 192) {
                trend[trend_el] = 1;
            } else if (val < -192) {
                trend[trend_el] = -1;
            } else {
                trend[trend_el] = 0;
            }

            trend_el = (trend_el + 1) % 10;

            for (int i = 0; i < 10; i++) {
                vt += trend[i];
            }

            if (vt > 0) {
                verticalTrend = 1;
            } else if (vt < 0) {
                verticalTrend = -1;
            } else {
                verticalTrend = 0;
            }
        }
    }

    public synchronized int getVerticalTrend() {
        return verticalTrend;
    }
    
    /**
     * Method used to return the target ground speed in knots
     *
     * @return target groundspeed in knots
     */
    public float getGroundSpeed() {
        return groundSpeed;
    }

    /**
     * Method used to return the target ground track in degrees true north.
     *
     * @return target ground track in degrees true north
     */
    public float getGroundTrack() {
        return groundTrack;
    }

    /**
     * Method used to return the target computed ground speed in knots
     *
     * @return target groundspeed in knots
     */
    public float getComputedGroundSpeed() {
        return groundSpeedComputed;
    }

    /**
     * Method used to return the target computed ground track in degrees true north.
     *
     * @return target ground track in degrees true north
     */
    public float getComputedGroundTrack() {
        return groundTrackComputed;
    }

    public void setComputedGroundSpeed(float val) {
        groundSpeedComputed = val;
    }

    public void setComputedGroundTrack(float val) {
        groundTrackComputed = val;
    }

    /**
     * Method to set all three velocities
     *
     * <p>
     * Vertical Rate is set to zero for low values, as the aircraft tend to
     * bobble up and down in turbulence, which generates network traffic. The
     * Vertical Rate is negative for descent.
     *
     * @param val1 Ground Track in degrees
     * @param val2 Ground Speed in knots
     * @param val3 Vertical Rate in feet per second
     */
    public void setVelocityData(float val1, float val2, int val3) {
        boolean changed = false;

        if (groundTrack != val1) {
            groundTrack = val1;
            changed = true;
        }

        if (groundSpeed != val2) {
            groundSpeed = val2;
            changed = true;
        }

        if (verticalRate != val3) {
            setVerticalRate(val3);
            changed = true;
        }

        if (changed) {
            updated = true;
        }
    }

    /**
     * Method used to set the target altitude in feet MSL (29.92) The virtual
     * onground status is also set if altitude reads 0 feet.
     *
     * @param val an integer Representing altitude in feet MSL or -9999 for null
     */
    public void setAltitude(int val) {
        if (altitude != val) {
            altitude = val;
            isVirtOnGround = (val == 0);
            updated = true;
        }
    }

    /**
     * Method used to return the target altitude in feet MSL (29.92)
     *
     * @return an integer Representing the target altitude in feet MSL
     */
    public int getAltitude() {
        return altitude;
    }

    /**
     * Method used to return the target latitude in degrees (south is negative)
     *
     * @return a float Representing the target latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Method used to return the target longitude in degrees (west is negative)
     *
     * @return a float Representing the target longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Method used to set the target 2D position (latitude, longitude) (south
     * and west are negative)
     *
     * @param val1 a float Representing the target latitude
     * @param val2 a float Representing the target longitude
     */
    public void setPosition(float val1, float val2) {
        boolean changed = false;

        /*
         * Ignore the 0 Lat/ 0 Lon garbage
         */
        if (val1 == 0.0F && val2 == 0.0F) {
            return;
        }

        if (latitude != val1) {
            latitude = val1;
            changed = true;
        }

        if (longitude != val2) {
            longitude = val2;
            changed = true;
        }

        if (changed) {
            incrementTrackQuality();
            updated = updatePosition = true;
            updatePositionTime = System.currentTimeMillis();
        }
    }

    /**
     * Method used to return the target callsign
     *
     * @return a string Representing the target callsign
     */
    public String getCallsign() {
        return callsign;
    }

    /**
     * Method used to set the target callsign
     *
     * @param val a string Representing the target callsign
     */
    public synchronized void setCallsign(String val) {
        if (!val.equals(callsign)) {
            callsign = val;
            updated = true;
        }
    }

    /**
     * Method used to return the target octal 4-digit squawk
     *
     * @return an integer Representing the target octal squawk
     */
    public int getSquawk() {
        return squawk;
    }

    /**
     * Method used to set the target octal 4-digit squawk
     *
     * @param val an integer Representing the target octal squawk
     */
    public void setSquawk(int val) {
        if (squawk != val) {
            squawk = val;
            updated = true;
            hijack = val == 7500;
            comm_out = val == 7600;
        }
    }

    /**
     * Method used to return the Emergency status
     *
     * @return a boolean Representing the Emergency status
     */
    public boolean getEmergency() {
        return emergency;
    }

    /**
     * Method used to return the SPI status
     *
     * @return a boolean Representing the SPI status
     */
    public boolean getSPI() {
        return spi;
    }

    /**
     * Method used to return the Hijack status
     *
     * @return a boolean Representing the Hijack status
     */
    public boolean getHijack() {
        return hijack;
    }

    public boolean getCommOut() {
        return comm_out;
    }

    public boolean getVirtualOnGround() {
        return isVirtOnGround;
    }

    /**
     * Method used to return the OnGround status
     *
     * @return a boolean Representing the OnGround status
     */
    public boolean getOnGround() {
        return isOnGround;
    }

    /**
     * Method to set the OnGround status
     *
     * @param val a boolean Representing the OnGround status
     */
    public void setOnGround(boolean val) {
        if (isOnGround != val) {
            isOnGround = val;
            updated = true;
        }
    }

    /**
     * Method used to return the Alert status The Alert signals the 4-digit
     * octal squawk has changed
     *
     * @return a boolean Representing the Alert status
     */
    public boolean getAlert() {
        return alert;
    }

    /**
     * Method to set all the boolean bits
     *
     * <p>
     * The Alert bit is set if the 4-digit octal code is changed. The Emergency
     * bit is set if the pilot puts in the emergency code The SPI bit is set if
     * the pilots presses the Ident button.
     *
     * @param val1 a boolean Representing the status of the Alert
     * @param val2 a boolean Representing the status of the Emergency
     * @param val3 a boolean Representing the status of the SPI
     */
    public void setAlert(boolean val1, boolean val2, boolean val3) {
        boolean changed = false;

        if (alert != val1) {
            alert = val1;

            if (alert == true) {
                hadAlert = true;
            }

            changed = true;
        }

        if (emergency != val2) {
            emergency = val2;

            if (emergency == true) {
                hadEmergency = true;
            }

            changed = true;
        }

        if (spi != val3) {
            spi = val3;

            if (spi == true) {
                hadSPI = true;
            }

            changed = true;
        }

        if (changed) {
            updated = true;
        }
    }

    public boolean getHadAlert() {
        return hadAlert;
    }

    public boolean getHadEmergency() {
        return hadEmergency;
    }

    public boolean getHadSPI() {
        return hadSPI;
    }
}
