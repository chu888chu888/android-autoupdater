package com.github.snowdream.android.app;

/**
 * The Period wich is used to check for update.<br/>
 *
 * Created by snowdream on 12/30/13.
 */
public enum UpdatePeriod {
    /**
     * Each time you check for update.see@UpdateManager#check
     */
    EACH_TIME,

    /**
     * Check for Update Every Day
     */
    EACH_ONE_DAY,

    /**
     * Check for Update Every Two Days
     */
    EACH_TWO_DAYS,

    /**
     * Check for Update Every Three Days
     */
    EACH_THREE_DAYS,

    /**
     * Check for Update Every Week.
     */
    EACH_ONE_WEEK,

    /**
     * Check for Update Two Weeks.
     */
    EACH_TWO_WEEKS,

    /**
     * Check for Update Three Weeks.
     */
    EACH_THREE_WEEKS,

    /**
     * Check for Update Every Month.
     */
    EACH_ONE_MONTH,
}
