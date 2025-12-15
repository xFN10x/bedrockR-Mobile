package fn10.bedrockrmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Logger;

import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo;
import fn10.bedrockr.utils.Greetings;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.SettingsFile;
import fn10.bedrockrmobile.activity.NewAddonActivity;

public class Launcher extends Activity {

    private void setupApp() {
        Logger.getGlobal().info(MessageFormat.format("bedrockR version: {0}, Java version: {1}, JVM: {2}", RFileOperations.VERSION, System.getProperty("java.version"),
                System.getProperty("java.vm.name")));
        RFileOperations.setBaseDir(getFilesDir());
        Logger.getGlobal().info(RFileOperations.getBaseDirectory().getAbsolutePath());
        SettingsFile settings = SettingsFile.load();
        RFileOperations.setBaseDir(new File(settings.comMojangPath));
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

        ImageButton newAddonButton = findViewById(R.id.newAddonButton);

        newAddonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("bedrockrmobile.intent.CREATE");
                startActivity(intent);
            }
        });
    }
}
