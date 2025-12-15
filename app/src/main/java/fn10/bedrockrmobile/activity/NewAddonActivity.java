package fn10.bedrockrmobile.activity;


import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallerKt;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import fn10.bedrockrmobile.R;

public class NewAddonActivity extends AppCompatActivity {

    private Uri selectedImg = null;
    private ImageView iconView;
    private Button selectIconButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.new_addon);
        selectIconButton = findViewById(R.id.selectIconButton);
        iconView = findViewById(R.id.addonIcon);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        selectedImg = uri;
                        BitmapDrawable d = null;
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            //crop it to a square. get the smallest, width or height, then crop it to that.
                            int cropTo = Math.min(bitmap.getWidth(), bitmap.getHeight());
                            Bitmap smallerBitmap;
                            if (cropTo == bitmap.getHeight()) {//make it centered widthly if the height is smaller because its only cropping on that axis
                                smallerBitmap = Bitmap.createBitmap(bitmap, ((bitmap.getWidth() - cropTo) /2), 0, cropTo, cropTo);
                            }
                            else
                                smallerBitmap = Bitmap.createBitmap(bitmap, 0, ((bitmap.getHeight() - cropTo) /2), cropTo, cropTo);

                            d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(smallerBitmap, 1000, 1000, false));
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


        //WorkspaceFile workspaceFile = new WorkspaceFile();
        //RFileOperations.createWorkspace(workspaceFile, null);
    }
}
