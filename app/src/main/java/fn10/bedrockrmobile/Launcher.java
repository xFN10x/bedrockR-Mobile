package fn10.bedrockrmobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import javax.swing.JOptionPane;

import fn10.bedrockr.utils.Greetings;
import fn10.bedrockr.utils.RFileOperations;

public class Launcher extends Activity {

    private static final String Tag = "LaunchPage";

    private void setupApp() {
        RFileOperations.setBaseDir(getFilesDir());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setupApp();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchpage);

        TextView greetingText = findViewById(R.id.greetingText);

        Greetings.Greeting greeting = Greetings.GetGreeting();

        greetingText.setText(greeting.Text);
        greetingText.setTextSize(greeting.Size);
    }
}
