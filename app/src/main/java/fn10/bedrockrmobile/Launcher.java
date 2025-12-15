package fn10.bedrockrmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Logger;

import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.utils.Greetings;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.SettingsFile;

public class Launcher extends Activity {

    private static final String tag = "Launcher";
    private void setupApp() {
        Logger.getGlobal().info(MessageFormat.format("bedrockR version: {0}, Java version: {1}, JVM: {2}", RFileOperations.VERSION, System.getProperty("java.version"),
                System.getProperty("java.vm.name")));
        RFileOperations.setBaseDir(getFilesDir());
        Logger.getGlobal().info(RFileOperations.getBaseDirectory().getAbsolutePath());
        SettingsFile settings = SettingsFile.load();
        RFileOperations.setComMojangDir(new File(settings.comMojangPath));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupApp();
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

        LinearLayout RAddonInnerScroll = findViewById(R.id.ElementInnerScroll);

        for (String wpName : RFileOperations.getWorkspaces()) {
            Log.i(tag, "Found workspace: " + wpName);
            WorkspaceFile wpF = RFileOperations.getWorkspaceFile(wpName);

            ConstraintLayout RAddon = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.raddon, null);

            ImageView addonBG = (ImageView) RAddon.findViewById(R.id.addonBackground);
            TextView addonName = (TextView) RAddon.findViewById(R.id.addonName);

            addonName.setText(wpName, TextView.BufferType.NORMAL);
            addonBG.setImageIcon(Icon.createWithContentUri(Uri.fromFile(RFileOperations.getFileFromWorkspace(wpName, "icon." + wpF.IconExtension))));

            RAddonInnerScroll.addView(addonName);
        }
    }
}
