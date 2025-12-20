package fn10.bedrockrmobile.activity;


import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockrmobile.R;

public class RNewAddonActivity extends AppCompatActivity {

    private static final String key = "NewAddonActivity";
    private Bitmap selectedImg = null;
    private ImageView iconView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.new_addon);
        Button selectIconButton = findViewById(R.id.selectIconButton);
        iconView = findViewById(R.id.addonIcon);
        Button newAddonButton = findViewById(R.id.createAddonButton);
        Button backButton = findViewById(R.id.backButton);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        BitmapDrawable d = null;
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            //crop it to a square. get the smallest, width or height, then crop it to that.
                            int cropTo = Math.min(bitmap.getWidth(), bitmap.getHeight());
                            Bitmap smallerBitmap;
                            if (cropTo == bitmap.getHeight()) {//make it centered widthly if the height is smaller because its only cropping on that axis
                                smallerBitmap = Bitmap.createBitmap(bitmap, ((bitmap.getWidth() - cropTo) / 2), 0, cropTo, cropTo);
                            } else
                                smallerBitmap = Bitmap.createBitmap(bitmap, 0, ((bitmap.getHeight() - cropTo) / 2), cropTo, cropTo);
                            d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(smallerBitmap, 1000, 1000, false));
                            selectedImg = d.getBitmap();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        iconView.setImageIcon(Icon.createWithBitmap(d.getBitmap()));
                    }
                });

        iconView.setImageIcon(Icon.createWithResource(this, R.drawable.defaulticon));

        selectIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newAddonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText addonNameBox = findViewById(R.id.addonNameBox);
                EditText addonPrefixBox = findViewById(R.id.addonPrefixBox);
                Spinner addonMEV = findViewById(R.id.minimumEngineVersionSpinner);
                EditText addonDescriptionBox = findViewById(R.id.addonDescriptionBox);

                FieldFilters.FileNameLikeStringFilter fnlsf = new FieldFilters.FileNameLikeStringFilter();
                FieldFilters.IDStringFilter idsf = new FieldFilters.IDStringFilter();

                String name = addonNameBox.getText().toString();
                String prefix = addonPrefixBox.getText().toString();
                String mev = addonMEV.getSelectedItem().toString();
                String description = addonDescriptionBox.getText().toString();

                if (
                        name.isEmpty() || prefix.isEmpty() || mev.isEmpty() || description.isEmpty() ||
                                !fnlsf.getValid(name) || !idsf.getValid(prefix) || !fnlsf.getValid(description)
                ) {
                    return;
                }


                WorkspaceFile workspaceFile = new WorkspaceFile(name, mev, description, "png", prefix);

                try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                    boolean fromNull = false;
                    if (selectedImg == null) {
                        fromNull = true;
                        selectedImg = ((BitmapDrawable) Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.defaulticon, null))).getBitmap();
                    }
                    selectedImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    RFileOperations.createWorkspace(workspaceFile, ArrayUtils.toObject(byteArray));
                    Log.i(key, "Created workspace at " + RFileOperations.getBaseDirectory().getAbsolutePath());
                    finish();
                    if (!fromNull)
                        selectedImg.recycle();
                } catch (Exception e) {
                    Log.e(key, "Failed to create workspace", e);
                }
            }
        });


    }
}
