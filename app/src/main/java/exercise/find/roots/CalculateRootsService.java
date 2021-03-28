package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {
  public static final int MAX_SEARCH_TIME = 20000;

  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;
    long timeStartMs = System.currentTimeMillis();
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }
    else{
      long cur_time = System.currentTimeMillis();
      long div = 2;
      while (cur_time - timeStartMs < MAX_SEARCH_TIME && div <= numberToCalculateRootsFor){
        if (numberToCalculateRootsFor % div == 0){
          long dividend = numberToCalculateRootsFor / div;
          Intent outIntent = new Intent("found_roots");
          outIntent.putExtra("original_number", numberToCalculateRootsFor);
          outIntent.putExtra("root1", div);
          outIntent.putExtra("root2", dividend);
          long val = (long) ((long) (cur_time - timeStartMs) / 1000.0);
          outIntent.putExtra("time", val);
          sendBroadcast(outIntent);
          return;
        }
        div += 1;
        cur_time = System.currentTimeMillis();
      }

      Intent outIntent = new Intent("stopped_calculations");
      outIntent.putExtra("original_number", numberToCalculateRootsFor);
      long val = (long) ((long) (cur_time - timeStartMs) / 1000.0);
      outIntent.putExtra("time_until_give_up_seconds", val);
      sendBroadcast(outIntent);
    }

    /*
    TODO:
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up

     */
  }
}