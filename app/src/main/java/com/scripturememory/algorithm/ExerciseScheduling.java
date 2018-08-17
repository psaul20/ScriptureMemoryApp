package com.scripturememory.algorithm;

import com.scripturememory.models.MemoryPassage;

import static java.lang.Math.abs;

public class ExerciseScheduling {

    private static long lngYearMillis = 31540000000L;
    private static long lngMonthMillis = 2628000000L;
    private static long lngWeekMillis = 604800000L;
    private static long lngDayMillis = 86400000L;
    private static long lngHourMillis = 3600000L;
    private static long lngMinuteMillis = 60000L;
    private static long lngSecondMillis = 1000L;

/*    public static void calcNextExerc (MemoryPassage psg, long CurrentTimeMillis){

        if(psg.getNextExerc() != 0)
            //Find time past since last exercise
            long lngTimeDiff = CurrentTimeMillis - psg.getLastExerc();
            //Convert intCurrentSeq from hours into Millis, subtract to find diff between time past since last
            //exercise and next exercise time
            psg.setNextExerc((psg.getCurrentSeq() * 3600000L) - lngTimeDiff);
        }
    }*/

//We will need string literals for these eventually
    public static void buildExercMsg(MemoryPassage psg, long CurrentTimeMillis){

        //Flag to return only the largest unit of time
        boolean blnOnlyBiggest = false;

        long lngNextExerc = psg.getNextExerc();

        long lngTimeUntilExerc = lngNextExerc - CurrentTimeMillis;
        //Ready to Exercise window = next scheduled exercise time plus previous fib sequence time interval
        long lngExercWindow = psg.getPrevSeq() * lngHourMillis;

        //Newly Added Passage
        if (lngNextExerc == 0){
            psg.setExercMsg("Ready to Exercise!");
        }

        //If within exercise window, ready to exercise
        else if (CurrentTimeMillis <= lngNextExerc + lngExercWindow && CurrentTimeMillis >= lngNextExerc) {
            psg.setExercMsg("Ready to Exercise!");
        }

        //Not ready to exercise yet
        else if (CurrentTimeMillis < lngNextExerc){
            psg.setExercMsg("Next Exercise in " + calcTimeUntil(lngTimeUntilExerc, blnOnlyBiggest));
        }

        //Past due on exercise
        else {
            blnOnlyBiggest = true;
            psg.setExercMsg("Ready to Exercise! " + calcTimeUntil(lngTimeUntilExerc, blnOnlyBiggest) + "overdue");
        }
    }

    private static String calcTimeUntil(long lngDueDateMillis, boolean blnOnlyBiggest){

        String strReturn = "";

        //Years
        if (lngDueDateMillis / lngYearMillis > 0 || lngDueDateMillis / lngYearMillis < 0){
            if (abs(lngDueDateMillis / lngYearMillis) == 1L){
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngYearMillis)) + " year ";
            }
            else {
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngYearMillis)) + " years ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }

            else {
                lngDueDateMillis = lngDueDateMillis % lngYearMillis;
            }
        }

        //Months
        if (lngDueDateMillis / lngMonthMillis > 0 || lngDueDateMillis / lngMonthMillis < 0){
            if (abs(lngDueDateMillis / lngMonthMillis) == 1L){
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngMonthMillis)) + " month ";
            }
            else {
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngMonthMillis)) + " months ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }

            else {
                lngDueDateMillis = lngDueDateMillis % lngMonthMillis;
            }
        }

        //Weeks
        if (lngDueDateMillis / lngWeekMillis > 0 || lngDueDateMillis / lngWeekMillis < 0){
            if (abs(lngDueDateMillis / lngWeekMillis) == 1L){
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngWeekMillis)) + " week ";
            }
            else {
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngWeekMillis)) + " weeks ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }

            else {
                lngDueDateMillis = lngDueDateMillis % lngWeekMillis;
            }
        }

        //Days
        if (lngDueDateMillis / lngDayMillis > 0 || lngDueDateMillis / lngDayMillis < 0){
            if (abs(lngDueDateMillis / lngDayMillis) == 1L){
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngDayMillis)) + " day ";
            }
            else {
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngDayMillis)) + " days ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }

            else {
                lngDueDateMillis = lngDueDateMillis % lngDayMillis;
            }
        }

        //Hours
        if (lngDueDateMillis / lngHourMillis > 0 || lngDueDateMillis / lngHourMillis < 0){
            if (abs(lngDueDateMillis / lngHourMillis) == 1L){
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngHourMillis)) + " hour ";
            }
            else {
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngHourMillis)) + " hours ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }

            else {
                lngDueDateMillis = lngDueDateMillis % lngHourMillis;
            }
        }

        //Minutes
        if (lngDueDateMillis / lngMinuteMillis > 0 || lngDueDateMillis / lngMinuteMillis < 0){
            if (abs(lngDueDateMillis / lngMinuteMillis) == 1L){
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngMinuteMillis)) + " minute ";
            }
            else {
                strReturn = strReturn + Long.toString(abs(lngDueDateMillis / lngMinuteMillis)) + " minutes ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }

            //Don't pass on to seconds, minutes will be largest time interval for composite message

        }

        //Seconds
        else if (lngDueDateMillis / lngSecondMillis > 0 || lngDueDateMillis / lngSecondMillis < 0){
            if (abs(lngDueDateMillis / lngSecondMillis) == 1L){
                strReturn = Long.toString(abs(lngDueDateMillis / lngSecondMillis)) + " second ";
            }
            else {
                strReturn = Long.toString(abs(lngDueDateMillis / lngSecondMillis)) + " seconds ";
            }

            if(blnOnlyBiggest){
                return strReturn;
            }
        }

        return strReturn;
    }

    public static boolean calcNextExercise (MemoryPassage psg, long ExercStartMillis, boolean ExerciseSuccess) {

        boolean blnUpdateDb = true;

        //First exercise case - start interval regardless of success or failure
        if (psg.getCurrentSeq() == 0){
            psg.setCurrentSeq(1);
            psg.setPrevSeq(1);
            psg.setLastExerc(ExercStartMillis);
            psg.setNextExerc(ExercStartMillis + psg.getCurrentSeq() * lngHourMillis);

            return blnUpdateDb;
        }

        else {
            long lngTimePassedSinceLastExerc = ExercStartMillis - psg.getLastExerc();

            //Exercise success cases
            if(ExerciseSuccess){
                //Underdue case - do nothing
                if(lngTimePassedSinceLastExerc < psg.getPrevSeq() * lngHourMillis) {
                    blnUpdateDb = false;
                    return blnUpdateDb;
                }

                else{
                    //Exercise ready case - increase fibonacci interval
                    int intHolder = psg.getPrevSeq();
                    psg.setPrevSeq(psg.getCurrentSeq());
                    psg.setCurrentSeq(psg.getCurrentSeq() + intHolder);
                    psg.setLastExerc(ExercStartMillis);
                    psg.setNextExerc(ExercStartMillis + psg.getCurrentSeq() * lngHourMillis);

                    return blnUpdateDb;
                }
            }

            //Exercise failure cases
            else {
                //Overdue case - reduce interval unless current sequence = 1
                if(ExercStartMillis > psg.getNextExerc() + psg.getPrevSeq() * lngHourMillis && psg.getCurrentSeq() != 1) {
                    int intHolder = psg.getCurrentSeq();
                    psg.setCurrentSeq(psg.getPrevSeq());
                    psg.setPrevSeq(intHolder - psg.getPrevSeq());
                    psg.setLastExerc(ExercStartMillis);
                    psg.setNextExerc(ExercStartMillis + psg.getCurrentSeq() * lngHourMillis);

                    return blnUpdateDb;
                }

                else {
                    //Normal failure case - maintain interval
                    psg.setLastExerc(ExercStartMillis);
                    psg.setNextExerc(ExercStartMillis + psg.getCurrentSeq() * lngHourMillis);

                    return blnUpdateDb;
                }
            }
        }
    }

}

