package fn10.bedrockrmobile.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.SourceFoodElement;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.utils.RMFileOperations;

public class RNewElementActivity extends AppCompatActivity {

    public static final Class<? extends ElementSource<?>>[] ELEMENTS = new Class[]{
            SourceItemElement.class,
            SourceBlockElement.class,
            SourceScriptElement.class,
            SourceRecipeElement.class,
            SourceFoodElement.class,
            SourceBiomeElement.class,
    };
    private static final String tag = "NewAddonActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.rnewelement);


        Button backButton = findViewById(R.id.backButton);
        LinearLayout InnerScroll = findViewById(R.id.innerScroll);

        backButton.setOnClickListener(v -> finish());

        for (Class<? extends ElementSource<?>> elementClass : ELEMENTS) {
            ConstraintLayout RElement = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.relement, null);
            ElementDetails details;
            try {
                details = (ElementDetails) elementClass.getMethod("getDetails", null).invoke(null, null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            ImageView elementIcon = RElement.findViewById(R.id.elementIcon);
            TextView elementDescription = RElement.findViewById(R.id.elementDescription);
            TextView elementName = RElement.findViewById(R.id.elementName);

            assert details != null;
            elementIcon.setImageIcon(Icon.createWithBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) Objects.requireNonNull(Icon.createWithData(details.Icon, 0, details.Icon.length).loadDrawable(getBaseContext()))).getBitmap(), 640, 640, false)));

            elementDescription.setText(RMFileOperations.parseHTMLBackIntoString(details.Description).replace("\n", " ").replace("  ", " "));
            elementName.setText(details.Name);

            AppCompatButton elementCreationButton = RElement.findViewById(R.id.editElementButton);

            elementCreationButton.setOnClickListener(v -> {
                RMElementCreationScreen.setCreationListener(RWorkspaceViewActivity.currentActive);

                Intent creationScreenIntent = new Intent();
                creationScreenIntent.setAction("bedrockrmobile.intent.CREATEELEMENT")
                        .putExtra("ElementSource", elementClass);
                startActivity(creationScreenIntent);
            });

            InnerScroll.addView(RElement);
        }

    }

}
