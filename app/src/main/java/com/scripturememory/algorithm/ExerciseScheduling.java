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

        long lngNextExerc = psg.getNextExerc();
        //
        long lngTimeUntilExerc = lngNextExerc - CurrentTimeMillis;
        //Ready to Exercise window = next scheduled exercise time plus previous fib sequence time interval
        long lngExercWindow = psg.getPrevSeq() * lngHourMillis;

        //Newly Added Passage
        if (lngNextExerc == 0){
            psg.setExercMsg("Ready To Exercise!");
        }

        //If within exercise window, ready to exercise
        else if (CurrentTimeMillis <= lngNextExerc + lngExercWindow && CurrentTimeMillis >= lngNextExerc) {
            psg.setExercMsg("Ready to Exercise!");
        }

        //Not ready to exercise yet
        else if (CurrentTimeMillis < lngNextExerc){
            psg.setExercMsg("Next Exercise in " + calcTimeUntil(lngTimeUntilExerc));
        }

        //Past due on exercise
        else {
            psg.setExercMsg("Ready To Exercise! " + calcTimeUntil(lngTimeUntilExerc) + " overdue");
        }
    }

    private static String calcTimeUntil(long lngDueDateMillis){

        String strReturn = "";

        //Years
        if (lngDueDateMillis / lngYearMillis > 0 || lngDueDateMillis / lngYearMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngYearMillis)) + " years";
        }

        //Months
        else if (lngDueDateMillis / lngMonthMillis > 0 || lngDueDateMillis / lngMonthMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngMonthMillis)) + " months";
        }

        //Weeks
        else if (lngDueDateMillis / lngWeekMillis > 0 || lngDueDateMillis / lngWeekMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngWeekMillis)) + " weeks";
        }

        //Days
        else if (lngDueDateMillis / lngDayMillis > 0 || lngDueDateMillis / lngDayMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngDayMillis)) + " days";
        }

        //Hours
        else if (lngDueDateMillis / lngHourMillis > 0 || lngDueDateMillis / lngHourMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngHourMillis)) + " hours";
        }

        //Minutes
        else if (lngDueDateMillis / lngMinuteMillis > 0 || lngDueDateMillis / lngMinuteMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngMinuteMillis)) + " minutes";
        }

        //Seconds
        else if (lngDueDateMillis / lngSecondMillis > 0 || lngDueDateMillis / lngSecondMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngSecondMillis)) + " seconds";
        }

        return strReturn;
    }

    public static boolean calcNextExercise (MemoryPassage psg, long ExercStartMillis, boolean ExerciseSuccess) {

        boolean blnUpdateDb = true;

        //First exercise case - start interval regardless of success or failure
        if (psg.getNextExerc() == 0){
            psg.setCurrentSeq(1);
            psg.setPrevSeq(0);
            psg.setLastExerc(ExercStartMillis);
            psg.setNextExerc(ExercStartMillis + psg.getCurrentSeq() * lngHourMillis);

            return blnUpdateDb;
        }

        else {
            long lngTimePassedSinceLastExerc = ExercStartMillis - psg.getLastExerc();

            //SuccessCases
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
                //Overdue case - reduce interval
                if(ExercStartMillis > psg.getNextExerc() + psg.getPrevSeq() * lngHourMillis) {
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

