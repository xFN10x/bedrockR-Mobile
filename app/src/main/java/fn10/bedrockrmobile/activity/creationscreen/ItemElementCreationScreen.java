package fn10.bedrockrmobile.activity.creationscreen;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockrmobile.R;

public class ItemElementCreationScreen extends RMElementCreationScreen<SourceItemElement> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.rnewelement);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

    }
}
