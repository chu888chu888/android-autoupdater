package com.github.snowdream.android.app;

/**
 * The Period wich is used to check for update.<br/>
 * <p/>
 * Created by snowdream on 12/30/13.
 */
public class UpdatePeriod {
    /**
     * Each time you check for update.see@UpdateManager#check
     */
    public static final int EACH_TIME = 0;

    /**
     * Check for Update Every Day
     */
    public static final int EACH_ONE_DAY = 1;

    /**
     * Check for Update Every Two Days
     */
    public static final int EACH_TWO_DAYS = 2;

    /**
     * Check for Update Every Three Days
     */
    public static final int EACH_THREE_DAYS = 3;

    /**
     * Check for Update Every Week.
     */
    public static final int EACH_ONE_WEEK = 7;

    /**
     * Check for Update Two Weeks.
     */
    public static final int EACH_TWO_WEEKS = 14;

    /**
     * Check for Update Three Weeks.
     */
    public static final int EACH_THREE_WEEKS = 21;

    /**
     * Check for Update Every Month.
     */
    public static final int EACH_ONE_MONTH = 30;

    /**
     * The Minimum Period
     */
    public static final int EACH_MIN = 0;

    /**
     * The Maximum Period
     */
    public static final int EACH_MAX = 365;

    private int period = -1;

    public UpdatePeriod(int period) {
        if (period >= EACH_MIN && period <= EACH_MAX){
            this.period = period;
        }else if (period < EACH_MIN){
            this.period = EACH_MIN;
        }else {
            this.period = EACH_MAX;
        }
    }

    /**
     * Get the period
     * the unit is Day.
     * @return
     */
    public int getPeriod() {
        return period;
    }

    /**
     * Set the period
     * the unit is Day.
     * @param period
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * Get the period with the unit microsecond.
     *
     * @return
     */
    public long getPeriodMillis(){
        return this.period  * 24 * 60 * 60 * 1000;
    }
}
