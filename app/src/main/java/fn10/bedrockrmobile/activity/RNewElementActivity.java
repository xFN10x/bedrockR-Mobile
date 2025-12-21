package fn10.bedrockrmobile.activity;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;

import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.SourceFoodElement;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockrmobile.R;

public class RNewElementActivity extends AppCompatActivity  {

    public static final Class<? extends ElementSource<?>>[] ELEMENTS = new Class[] {
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (Class<? extends ElementSource<?>> elementClass : ELEMENTS) {
            ConstraintLayout RElement = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.relement, null);
            ElementDetails details;
            try {
                details = (ElementDetails) elementClass.getMethod("getDetails",null).invoke(null,null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            ImageView elementIcon = (ImageView) RElement.findViewById(R.id.elementIcon);
            TextView elementDescription = (TextView) RElement.findViewById(R.id.elementDescription);
            TextView elementName = (TextView) RElement.findViewById(R.id.elementName);

            assert details != null;
            elementIcon.setImageIcon(Icon.createWithData(details.Icon,0,details.Icon.length));

            elementDescription.setText(details.Description);
            elementName.setText(details.Name);

            InnerScroll.addView(RElement);
        }

    }
}
