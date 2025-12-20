package fn10.bedrockrmobile.utils;

import static fn10.bedrockr.utils.RFileOperations.getBaseDirectory;

import android.os.Environment;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import net.lingala.zip4j.ZipFile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import fn10.bedrockr.utils.RFileOperations;

/**
 * Files operations specifically for android.
 */
public class RMFileOperations {

    public static final Path BEDROCKR_PUBLIC_PATH = Path.of(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "bedrockR");
    public static final String OPEN_WORKSPACE_EXTRA_NAME = "workspaceName";
    private static final String tag = "RMFileOperations";

    /**
     * Creates to .mcpacks, in build/mcpr, and build/mcpb
     * <p/>
     * This function only takes the packs already in the build folders.
     *
     * @return The file of the mcaddon
     */
    public static File buildMCAddon(String workspaceName) throws IOException {
        Path BPdir = getBaseDirectory("build", "BP", workspaceName).toPath();
        Path RPdir = getBaseDirectory("build", "RP", workspaceName).toPath();
        Path MCADir = getBaseDirectory("build", "MCA").toPath();
        try {
            Os.symlink(MCADir.toString(), BEDROCKR_PUBLIC_PATH.resolve("Addons").toString());
        } catch (ErrnoException e) {
            Log.e(tag, "Failed to create symlink.", e);
        }
        try (ZipFile MCAddon = new ZipFile(MCADir.resolve(workspaceName + ".mcaddon").toFile())) {
            String[] versionStringArray = RFileOperations.getWorkspaceFile(workspaceName).BPVersion.split("\\.");

            MCAddon.addFolder(BPdir.toFile());
            //rename the BP, which should have the workspaceName
            MCAddon.renameFile(workspaceName, workspaceName + " - BP (v" + versionStringArray[versionStringArray.length-1] + ")");

            MCAddon.addFolder(RPdir.toFile());
            //do it with the RP
            MCAddon.renameFile(workspaceName, workspaceName + " - RP (v" + versionStringArray[versionStringArray.length-1] + ")");

            return MCAddon.getFile();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getMCAddonOfWorkspace(String name) {
        return getBaseDirectory("build", "MCA").toPath().resolve(name + ".mcaddon");
    }

}
