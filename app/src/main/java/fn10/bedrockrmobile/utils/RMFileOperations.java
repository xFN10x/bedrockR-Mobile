package fn10.bedrockrmobile.utils;

import static fn10.bedrockr.utils.RFileOperations.getBaseDirectory;

import android.os.Environment;

import net.lingala.zip4j.ZipFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Files operations specifically for android.
 */
public class RMFileOperations {

    public static final Path BEDROCKR_PUBLIC_PATH = Path.of(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "bedrockR");
    public static final String OPEN_WORKSPACE_EXTRA_NAME = "workspaceName";
    private static final String tag = "RMFileOperations";

    /**
     * Creates MCAddons into build/MCA, by pairing the RP and BP with the workspaceName
     * @param workspaceName the Name of the workspace
     */
    public static void buildMCAddon(String workspaceName) throws IOException {
        Path BPdir = getBaseDirectory("build", "BP", workspaceName).toPath();
        Path RPdir = getBaseDirectory("build", "RP", workspaceName).toPath();
        Path MCADir = getBaseDirectory("build", "MCA").toPath();
        /*try {
            Os.symlink(MCADir.toString(), BEDROCKR_PUBLIC_PATH.resolve("Addons").toString());
        } catch (ErrnoException e) {
            Log.e(tag, "Failed to create symlink.", e);
        }*/
        try (ZipFile MCAddon = new ZipFile(MCADir.resolve(workspaceName + ".mcaddon").toFile())) {
            //String[] versionStringArray = RFileOperations.getWorkspaceFile(workspaceName).BPVersion.split("\\.");
            MCAddon.addFolder(BPdir.toFile());
            MCAddon.renameFile(BPdir.getFileName().toString() + "/", workspaceName + " BP");
            MCAddon.addFolder(RPdir.toFile());
            MCAddon.renameFile(BPdir.getFileName().toString() + "/", workspaceName + " RP");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getMCAddonOfWorkspace(String name) {
        return getBaseDirectory("build", "MCA").toPath().resolve(name + ".mcaddon");
    }

}
