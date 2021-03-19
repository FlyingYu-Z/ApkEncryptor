package maobyte.zip;

public interface ZipCallback {
    void onProgress(long current, long total);
}
