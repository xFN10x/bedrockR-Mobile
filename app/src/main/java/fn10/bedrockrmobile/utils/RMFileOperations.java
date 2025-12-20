package fn10.bedrockrmobile.utils;

import static fn10.bedrockr.utils.RFileOperations.getBaseDirectory;

import android.os.Environment;
import android.util.Log;

import net.lingala.zip4j.ZipFile;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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
     */
    public static void buildMCAddon(String workspaceName) throws IOException {
        Path BPdir = getBaseDirectory("build", "BP", workspaceName).toPath();
        Path RPdir = getBaseDirectory("build", "RP", workspaceName).toPath();
        Path MCAPublicDir = BEDROCKR_PUBLIC_PATH.resolve("Addons");
        Path MCADir = getBaseDirectory("build", "MCA").toPath();


    }

}
