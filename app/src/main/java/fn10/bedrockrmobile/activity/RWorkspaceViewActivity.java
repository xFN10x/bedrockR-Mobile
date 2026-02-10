package fn10.bedrockrmobile.activity;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.GlobalBuildingVariables;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.contracts.PickElementContract;
import fn10.bedrockrmobile.dialog.RAlertDialog;
import fn10.bedrockrmobile.dialog.RLoadingDialog;
import fn10.bedrockrmobile.utils.RMFileOperations;

public class RWorkspaceViewActivity extends AppCompatActivity implements ElementCreationListener {

    public static RWorkspaceViewActivity currentActive;
    private static final String tag = "RWorkspace";
    private SourceWorkspaceFile swf;
    private final ActivityResultLauncher<ObjectUtils.Null> getSourceElementClass = registerForActivityResult(new PickElementContract(), result -> {
        if (result != null) {
            Intent creationScreenIntent = new Intent();
            creationScreenIntent.setAction("bedrockrmobile.intent.CREATEELEMENT")
                    .putExtra("ElementSource", result);
            startActivity(creationScreenIntent);
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.rworkspace);

        Intent intent = getIntent();
        File workspaceDiscFile = RFileOperations.getFileFromWorkspace(intent.getStringExtra(RMFileOperations.OPEN_WORKSPACE_EXTRA_NAME), RFileOperations.WPFFILENAME);
        SourceWorkspaceFile SWPF;
        try {
            SWPF = new SourceWorkspaceFile(new String(Files.readAllBytes(workspaceDiscFile.toPath())));
        } catch (IOException e) {
            RAlertDialog.showError(getResources(), getSupportFragmentManager(), R.string.workspace_failed);
            return;
        }

        ImageButton launchMCButton = findViewById(R.id.launchMCButton);
        ImageButton buildButton = findViewById(R.id.buildElementsButton);
        ImageButton rebuildButton = findViewById(R.id.reBuildButton);
        ImageButton updateAddonAndPlayButton = findViewById(R.id.launchMCAndBuild);
        ImageButton newElementButton = findViewById(R.id.addElementButton);

        newElementButton.setOnClickListener(v -> {
            getSourceElementClass.launch(null);
            /*Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setAction("bedrockrmobile.intent.NEWELEMENT");
            intent1.putExtra("wpname", swf.workspaceName());
            startActivity(intent1);*/
        });

        launchMCButton.setOnClickListener(v -> {
            try {
                Intent minecraftImportIntent = new Intent(Intent.ACTION_VIEW);
                minecraftImportIntent.setPackage("com.mojang.minecraftpe");
                startActivity(minecraftImportIntent);
            } catch (Exception e) {
                RAlertDialog.showError(getSupportFragmentManager(), getResources().getString(R.string.failed_launch));
            }
        });

        updateAddonAndPlayButton.setOnClickListener(v -> {
            try {
                String stringVersion = swf.getSerilized().BPVersion;
                List<Long> longList = new ArrayList<Long>();
                // turn the string version (1.0.0) into array...
                // which it should have been in the first place. ([1,0,0])
                for (String number : stringVersion.split("\\.")) {
                    longList.add(Long.parseLong(number));
                }
                long buildVersion = longList.get(longList.size() - 1);
                long currentBuildVersion = buildVersion + 1;

                String currentStringVersion = "1.0." + currentBuildVersion;

                swf.getSerilized().BPVersion = currentStringVersion;
                swf.getSerilized().RPVersion = currentStringVersion;

                swf.getSerilized().RPSuffix = " RP (v" + currentBuildVersion + ")";
                swf.getSerilized().BPSuffix = " BP (v" + currentBuildVersion + ")";

                swf.saveJSONFile(swf.workspaceName());

                buildElements(false, () -> {
                    try {
                        Intent minecraftImportIntent = new Intent(Intent.ACTION_VIEW);
                        minecraftImportIntent.setData(FileProvider.getUriForFile(
                                this,
                                getPackageName() + ".provider",
                                RMFileOperations.getMCAddonOfWorkspace(swf.workspaceName()).toFile()));
                        minecraftImportIntent.setPackage("com.mojang.minecraftpe");
                        minecraftImportIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(minecraftImportIntent);
                    } catch (Exception e) {
                        RAlertDialog.showError(getSupportFragmentManager(), getResources().getString(R.string.failed_launch));
                    }
                });


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        rebuildButton.setOnClickListener(v -> {
            try {
                buildElements(true, () -> {
                    Looper.prepare();
                    Toast finishedToast = Toast.makeText(this, R.string.build_success, Toast.LENGTH_SHORT);
                    finishedToast.show();
                });
            } catch (IOException e) {
                RAlertDialog.showError(getSupportFragmentManager(), e);
            }

        });

        buildButton.setOnClickListener(v -> {
            try {
                buildElements(false, () -> {
                    Looper.prepare();
                    Toast finishedToast = Toast.makeText(this, R.string.build_success, Toast.LENGTH_SHORT);
                    finishedToast.show();
                });
            } catch (IOException e) {
                RAlertDialog.showError(getSupportFragmentManager(), e);
            }
        });

        RFileOperations.setCurrentWorkspace(SWPF);
        swf = SWPF;

        refreshElements();
    }

    /*public void checkMCSync(boolean showMessage) {
        if (!swf.getSerilized().MinecraftSync) {// || SettingsFile.load().comMojangPath.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_BedrockRMobile_AlertDialog);
            builder.setTitle(R.string.mc_sync_ask);
            builder.setMessage(R.string.mc_sync_desc);
            builder.setPositiveButton(R.string.affirm, (dialog, which) -> {
                swf.getSerilized().MinecraftSync = true;
                swf.buildJSONFile(swf.workspaceName());

                SettingsFile settings = SettingsFile.load();
                settings.comMojangPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.mojang.minecraftpe/files/games/com.mojang";
                settings.save();

                checkMCSync(true);

                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.deny, (dialog, which) -> {
                swf.getSerilized().MinecraftSync = false;
                dialog.dismiss();
            });

            AlertDialog dio = builder.create();

            dio.show();
            return;
        }

        if (Environment.isExternalStorageManager()) {
            if (!new File(SettingsFile.load().comMojangPath).exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_BedrockRMobile_AlertDialog);
                builder.setTitle(R.string.mc_sync_failure);
                builder.setMessage(R.string.mc_sync_failure_desc);
                builder.setNeutralButton(R.string.acknowledge, (dialog, which) -> {
                    dialog.dismiss();
                });
                Log.i(tag, SettingsFile.load().comMojangPath);
                AlertDialog dio = builder.create();

                dio.show();

                swf.getSerilized().MinecraftSync = false;
                swf.buildJSONFile(swf.workspaceName());

            } else if (showMessage) {
                Toast.makeText(this, R.string.mc_sync_success, Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_BedrockRMobile_AlertDialog);
            builder.setTitle(R.string.needs_permissions);
            builder.setMessage(R.string.mc_sync_permission_ask);
            builder.setPositiveButton(R.string.affirm, (dialog, which) -> {
                startActivity(new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                checkMCSync(true);
            });

            builder.setNegativeButton(R.string.deny, (dialog, which) -> {
                swf.getSerilized().MinecraftSync = false;
                swf.buildJSONFile(swf.workspaceName());
                dialog.dismiss();
            });

            AlertDialog dio = builder.create();

            dio.show();
        }
    }
*/

    /**
     * Builds all the elements in the workspace.
     *
     * @param rebuild Specifies if the build folders should be deleted before the workspace is made
     * @throws IOException If deleting the build folders fails
     */
    public void buildElements(boolean rebuild, Runnable whenDone) throws IOException {
        RLoadingDialog dialog = new RLoadingDialog();
        runOnUiThread(() -> dialog.show(getSupportFragmentManager(), "R_LOADING_SCREEN"));

        new Thread(() -> {
            try {
                if (rebuild)
                    Log.i(tag, "Rebuilding workspace: " + swf.workspaceName());
                else
                    Log.i(tag, "Building workspace: " + swf.workspaceName());
                String BPdir = RFileOperations.getBaseDirectory("build", "BP", swf.workspaceName()).toString();
                String RPdir = RFileOperations.getBaseDirectory("build", "RP", swf.workspaceName()).toString();

                if (rebuild) {
                    try {
                        FileUtils.deleteDirectory(new File(BPdir));
                        FileUtils.deleteDirectory(new File(RPdir));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                GlobalBuildingVariables gbv = new GlobalBuildingVariables(swf.getSerilized(), RFileOperations.getResources(swf.workspaceName()).getSerilized());
                List<ElementFile<?>> toBuild = List.of(RFileOperations.getElementsFromWorkspace(swf.workspaceName()));

                //build all elements
                for (ElementFile<?> element : toBuild) {
                    element.build(BPdir, swf.getSerilized(), RPdir, gbv);
                }

                //build resources
                gbv.build(BPdir, swf.getSerilized(), RPdir, gbv);
                //build workspace
                swf.getSerilized().build(BPdir, swf.getSerilized(), RPdir, gbv);

                //build mcpack
                RMFileOperations.buildMCAddon(swf.workspaceName());

                whenDone.run();
                runOnUiThread(dialog::dismiss);
            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void refreshElements() {
        LinearLayout InnerScroll = findViewById(R.id.ElementInnerScroll);
        for (ElementFile<?> file : RFileOperations.getElementsFromWorkspace(swf.workspaceName())) {
            ConstraintLayout RElement = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.rworkspaceelement, null);
            Class<? extends ElementSource<?>> sourceElementClass = file.getSourceClass();

            ElementDetails details;
            try {
                details = (ElementDetails) sourceElementClass.getMethod("getDetails", null).invoke(null, null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            ImageView elementIcon = RElement.findViewById(R.id.elementIcon);
            TextView elementDescription = RElement.findViewById(R.id.elementDescription);
            TextView elementName = RElement.findViewById(R.id.elementName);

            assert details != null;
            elementIcon.setImageIcon(Icon.createWithData(details.Icon, 0, details.Icon.length));

            elementDescription.setText(RMFileOperations.parseHTMLBackIntoString(details.Description).replace("\n", " ").replace("  ", " "));
            elementName.setText(file.getElementName());

            InnerScroll.addView(RElement);
        }
        currentActive = this;
    }

    @Override
    public void onElementCreate(ElementSource<?> element) {
        element.getSerilized().setDraft(false);

        refreshElements();
    }

    @Override
    public void onElementDraft(ElementSource<?> element) {
        element.getSerilized().setDraft(true);

        refreshElements();
    }

    @Override
    public void onElementCancel() {
        refreshElements();
    }
}
