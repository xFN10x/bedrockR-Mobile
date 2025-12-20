package fn10.bedrockrmobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.GlobalBuildingVariables;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.dialog.RLoadingDialog;
import fn10.bedrockrmobile.utils.RMFileOperations;

public class RWorkspaceViewActivity extends AppCompatActivity {

    private static final String tag = "RWorkspace";
    private SourceWorkspaceFile swf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.r_workspace);

        Intent intent = getIntent();
        File workspaceDiscFile = RFileOperations.getFileFromWorkspace(intent.getStringExtra(RMFileOperations.OPEN_WORKSPACE_EXTRA_NAME), RFileOperations.WPFFILENAME);
        SourceWorkspaceFile SWPF;
        try {
            SWPF = new SourceWorkspaceFile(new String(Files.readAllBytes(workspaceDiscFile.toPath())));
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_BedrockRMobile_AlertDialog);
            builder.setTitle(R.string.workspace_failed);
            builder.setMessage(e.getMessage());
            builder.setNeutralButton(R.string.acknowledge, (dialog, which) -> {
                finish();
            });

            AlertDialog dio = builder.create();

            dio.show();

            return;
        }

        ImageButton launchMCButton = findViewById(R.id.launchMCButton);
        ImageButton buildButton = findViewById(R.id.buildElementsButton);
        ImageButton rebuildButton = findViewById(R.id.reBuildButton);
        ImageButton updateAddonAndPlayButton = findViewById(R.id.launchMCAndBuild);

        launchMCButton.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("minecraft:///")));
        });

        updateAddonAndPlayButton.setOnClickListener(v -> {
            RLoadingDialog dialog = new RLoadingDialog();
            dialog.show(getSupportFragmentManager(), "R_LOADING_SCREEN");
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
                swf.buildJSONFile(swf.workspaceName());

                buildElements(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            dialog.dismiss();
            Intent minecraftImportIntent = new Intent(Intent.ACTION_VIEW);
            minecraftImportIntent.setData(FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", RFileOperations.getBaseDirectory(
                            "build","MCA"
                    ).toPath().resolve(swf.workspaceName() + " - BP.mcpack").toFile()));
            minecraftImportIntent.setPackage("com.mojang.minecraftpe");
            minecraftImportIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(minecraftImportIntent);
            /*startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "minecraft:///?importpack=" +
                            RMFileOperations.BEDROCKR_PUBLIC_PATH.resolve("BP/"+swf.workspaceName()+" - BP.mcpack")
                            //+ "&importpack=" +
                            //RMFileOperations.BEDROCKR_PUBLIC_PATH.resolve("RP/"+swf.workspaceName()+" - RP.mcpack")
                    )));*/
        });


        rebuildButton.setOnClickListener(v -> {
            RLoadingDialog dialog = new RLoadingDialog();
            dialog.show(getSupportFragmentManager(), "R_LOADING_SCREEN");
            try {
                buildElements(true);
            } catch (IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_BedrockRMobile_AlertDialog);
                builder.setTitle(R.string.build_failed);
                builder.setMessage(e.getMessage());
                builder.setNeutralButton(R.string.acknowledge, (dia, which) -> {
                    dia.dismiss();
                });

                AlertDialog dio = builder.create();

                dio.show();
                return;
            }
            dialog.dismiss();
            Toast finishedToast = Toast.makeText(this, R.string.build_success, Toast.LENGTH_SHORT);
            finishedToast.show();
        });

        buildButton.setOnClickListener(v -> {
            RLoadingDialog dialog = new RLoadingDialog();
            dialog.show(getSupportFragmentManager(), "R_LOADING_SCREEN");
            try {
                buildElements(false);
            } catch (IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_BedrockRMobile_AlertDialog);
                builder.setTitle(R.string.build_failed);
                builder.setMessage(e.getMessage());
                builder.setNeutralButton(R.string.acknowledge, (dia, which) -> {
                    dia.dismiss();
                });

                AlertDialog dio = builder.create();

                dio.show();
                return;
            }
            dialog.dismiss();
            Toast finishedToast = Toast.makeText(this, R.string.build_success, Toast.LENGTH_SHORT);
            finishedToast.show();
        });

        RFileOperations.setCurrentWorkspace(SWPF);
        swf = SWPF;

        //checkMCSync(false);
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
    public void buildElements(boolean rebuild) throws IOException {
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
        //build mcpacks
        RMFileOperations.buildMCAddon();
    }
}
