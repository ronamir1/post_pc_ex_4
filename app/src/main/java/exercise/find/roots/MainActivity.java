package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForFailure = null;
  private Button buttonCalculateRoots;
  private EditText editTextUserInput;
  private ProgressBar progressBar;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressBar = findViewById(R.id.progressBar);
    editTextUserInput = findViewById(R.id.editTextInputNumber);
    buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      public void onTextChanged(CharSequence s, int start, int before, int count) { }
      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        try {
          long to_calc = Long.parseLong(newText);
          buttonCalculateRoots.setEnabled(true);
        }
        catch (NumberFormatException ignored){
          buttonCalculateRoots.setEnabled(false);
        }
      }
    });

    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();
      long userInputLong = Long.parseLong(userInputString);
      intentToOpenService.putExtra("number_for_service", userInputLong);
      startService(intentToOpenService);
      buttonCalculateRoots.setEnabled(false);
      editTextUserInput.setEnabled(false);
      progressBar.setEnabled(true);
      progressBar.setVisibility(View.VISIBLE);
    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")) return;
        progressBar.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);
        editTextUserInput.setEnabled(true);
        buttonCalculateRoots.setEnabled(true);
        long original_number = incomingIntent.getLongExtra("original_number", 0);
        long root1 = incomingIntent.getLongExtra("root1", 0);
        long root2 = incomingIntent.getLongExtra("root2", 0);
        long time = incomingIntent.getLongExtra("time", 0);

        Intent intent = new Intent(context, Success.class);
        intent.putExtra("original_number", original_number);
        intent.putExtra("root1", root1);
        intent.putExtra("root2", root2);
        intent.putExtra("time", time);
        context.startActivity(intent);
      }
    };

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForFailure = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations")) return;
        progressBar.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);
        editTextUserInput.setEnabled(true);
        buttonCalculateRoots.setEnabled(true);
        long original_number = incomingIntent.getLongExtra("original_number", 0);
        long time = incomingIntent.getLongExtra("time_until_give_up_seconds", 0);

        Toast.makeText(context, "calculation aborted after " + String.valueOf(time) +
                " seconds", Toast.LENGTH_SHORT).show();
      }
    };
    registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));
    registerReceiver(broadcastReceiverForFailure, new IntentFilter("stopped_calculations"));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.unregisterReceiver(broadcastReceiverForFailure);
    this.unregisterReceiver(broadcastReceiverForSuccess);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    CalculatorState state = new CalculatorState();
    state.progress_status = progressBar.isEnabled();
    state.button_status = buttonCalculateRoots.isEnabled();
    state.edit_text_status = editTextUserInput.isEnabled();
    state.text = editTextUserInput.getText().toString();
    outState.putSerializable("current state", state);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    CalculatorState state = (CalculatorState) savedInstanceState.getSerializable("current state");
    progressBar.setEnabled(state.progress_status);
    if (state.progress_status){
      progressBar.setVisibility(View.VISIBLE);
    }
    else {
      progressBar.setVisibility(View.INVISIBLE);
    }
    editTextUserInput.setEnabled(state.edit_text_status);
    editTextUserInput.setText(state.text);
    buttonCalculateRoots.setEnabled(state.button_status);
  }

  private static class CalculatorState implements Serializable {
    boolean progress_status;
    boolean edit_text_status;
    boolean button_status;
    String text;
  }
}
