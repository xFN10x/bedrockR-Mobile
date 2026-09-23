package fn10.bedrockrmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
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

import fn10.bedrockr.addons.element.elementSources.SourceBiomeElement;
import fn10.bedrockr.addons.element.elementSources.SourceBlockElement;
import fn10.bedrockr.addons.element.elementSources.SourceFoodElement;
import fn10.bedrockr.addons.element.elementSources.SourceItemElement;
import fn10.bedrockr.addons.element.elementSources.SourceRecipeElement;
import fn10.bedrockr.addons.element.elementSources.SourceScriptElement;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.utils.RMFileOperations;

public class RNewElementActivity extends AppCompatActivity {
    private static final String tag = "NewAddonActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.rnewelement);


        Button backButton = findViewById(R.id.backButton);
        LinearLayout InnerScroll = findViewById(R.id.innerScroll);

        backButton.setOnClickListener(v -> finish());
        for (Class<? extends ElementSource<?>> elementClass : RFileOperations.ELEMENTS) {
            ConstraintLayout RElement = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.relement, null);
            ElementDetails details;
            ElementSource<?> src;
            try {
                src = elementClass.getConstructor().newInstance();
                details = src.getDetails();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException e) {
                Log.e(tag, "Failed to get element details.");
                continue;
            }

            ImageView elementIcon = RElement.findViewById(R.id.elementIcon);
            TextView elementDescription = RElement.findViewById(R.id.elementDescription);
            TextView elementName = RElement.findViewById(R.id.elementName);

            assert details != null;
            Resources res = getApplicationContext().getResources();
            int iconId = res.getIdentifier("element_" + details.Icon.toLowerCase(), "drawable", getPackageName());
            if (iconId == 0) iconId = R.drawable.element_element;
            elementIcon. setImageIcon(Icon.createWithResource(getApplicationContext(), iconId));

            //elementIcon.setImageIcon(Icon.);
            //elementIcon.setImageIcon(Icon.createWithBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) Objects.requireNonNull(Icon.createWithData(details.Icon, 0, details.Icon.length()).loadDrawable(getBaseContext()))).getBitmap(), 640, 640, false)));

            elementDescription.setText(RMFileOperations.parseHTMLBackIntoString(details.Description).replace("\n", " ").replace("  ", " "));
            elementName.setText(details.Name);

            AppCompatButton elementCreationButton = RElement.findViewById(R.id.editElementButton);

            elementCreationButton.setOnClickListener(v -> {
                setResult(Activity.RESULT_OK,new Intent().putExtra("class", elementClass).putExtra("json", RFileOperations.gson.toJson(src)));
                finish();
                /*RMElementCreationScreen.setCreationListener(RWorkspaceViewActivity.currentActive);

                Intent creationScreenIntent = new Intent();
                creationScreenIntent.setAction("bedrockrmobile.intent.CREATEELEMENT")
                        .putExtra("ElementSource", elementClass);
                startActivity(creationScreenIntent);*/
            });

            InnerScroll.addView(RElement);
        }

    }

}
