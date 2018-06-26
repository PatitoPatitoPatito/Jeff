package xyz.misterkozo.rcjeff;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class GifGenerator {

    ByteArrayOutputStream bos;
    AnimatedGifEncoder encoder;
    boolean record = true;

    public GifGenerator() {
    }

    private void resetInstance() {
        bos = new ByteArrayOutputStream();
        encoder = new AnimatedGifEncoder();
    }

    public void start() {
        record = true;
        resetInstance();
        encoder.start(bos);
    }

    public void addFrame(Bitmap bitmap) {
        encoder.addFrame(bitmap);
    }

    public byte[] generateGIF() {
        record = false;
        encoder.finish();
        return bos.toByteArray();
    }

}
