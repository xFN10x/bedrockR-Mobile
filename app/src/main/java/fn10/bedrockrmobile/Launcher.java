package fn10.bedrockrmobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.logging.Logger;

import fn10.bedrockr.utils.Greetings;
import fn10.bedrockr.utils.RFileOperations;

public class Launcher extends Activity {

    private static final String Tag = "LaunchPage";

   /* public static void main(String[] args) {
        Log.i(Tag,System.getProperty("user.home"));
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(Tag,getFilesDir().getAbsolutePath());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchpage);



        EditText greetingText = findViewById(R.id.greetingText);

        greetingText.setText(Greetings.GetGreeting().Text);
    }
}
