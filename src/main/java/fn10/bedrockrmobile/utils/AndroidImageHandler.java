package fn10.bedrockrmobile.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.ImageHandler;

public class AndroidImageHandler extends ImageHandler<Bitmap> {
    @Override
    public Bitmap getImageFromBytes(byte[] data) throws IOException {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public byte[] getBytesFromImage(Bitmap img) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            img.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
    }

    @Override
    public Bitmap resizeImage(int w, int h, Bitmap img) {
        return Bitmap.createScaledBitmap(img, w,h, true);
    }

    @Override
    public Bitmap resizeImageSharp(int w, int h, Bitmap img) {
        return Bitmap.createScaledBitmap(img, w,h, false);
    }

    @Override
    public Bitmap renderBlock(String name, BlockTexture btex, WorkspaceResources res) throws IOException {
        return null;
    }
}
