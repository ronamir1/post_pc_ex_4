package exercise.find.roots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class Success extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        TextView results_text = findViewById(R.id.result);
        Intent incomingIntent = getIntent();
        long original_number = incomingIntent.getLongExtra("original_number", 0);
        long root1 = incomingIntent.getLongExtra("root1", 0);
        long root2 = incomingIntent.getLongExtra("root2", 0);
        long time = incomingIntent.getLongExtra("time", 0);

        String msg_1 = String.format(Locale.US,"original number is %d \n first root is %d" +
                " \n second root is %d\n total calculation time is %d seconds",
                original_number, root1, root2, time);
        results_text.setText(msg_1);
    }
}