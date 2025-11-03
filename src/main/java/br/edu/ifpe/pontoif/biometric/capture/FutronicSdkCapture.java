package br.edu.ifpe.pontoif.biometric.capture;

import com.futronic.SDKHelper.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

public class FutronicSdkCapture {

    private static final long CAPTURE_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(20);

    public byte[] captureImageBytes() {
        final Object lock = new Object();
        final boolean[] finished = new boolean[1];
        final BufferedImage[] lastImageHolder = new BufferedImage[1];
        final boolean[] successHolder = new boolean[1];

        try {
            FutronicEnrollment enrollment = new FutronicEnrollment();
            enrollment.setFakeDetection(true);
            enrollment.setMaxModels(1);

            IEnrollmentCallBack callback = new IEnrollmentCallBack() {
                @Override public void OnPutOn(FTR_PROGRESS p) {}
                @Override public void OnTakeOff(FTR_PROGRESS p) {}
                @Override public boolean OnFakeSource(FTR_PROGRESS p) { return false; }
                @Override public void UpdateScreenImage(BufferedImage image) {
                    synchronized (lock) { lastImageHolder[0] = image; }
                }
                @Override public void OnEnrollmentComplete(boolean success, int code) {
                    synchronized (lock) {
                        successHolder[0] = success;
                        finished[0] = true;
                        lock.notifyAll();
                    }
                }
            };

            Thread t = new Thread(() -> {
                try { enrollment.Enrollment(callback); }
                catch (Throwable e) {
                    synchronized (lock) { finished[0] = true; lock.notifyAll(); }
                }
            });
            t.start();

            long start = System.currentTimeMillis();
            synchronized (lock) {
                while (!finished[0]) {
                    long elapsed = System.currentTimeMillis() - start;
                    long remain = CAPTURE_TIMEOUT_MS - elapsed;
                    if (remain <= 0) break;
                    lock.wait(remain);
                }
            }

            if (!successHolder[0]) {
                System.err.println("❌ Captura não finalizou com sucesso.");
                return null;
            }

            BufferedImage original = lastImageHolder[0];
            if (original == null) {
                System.err.println("❌ Nenhuma imagem final recebida do SDK.");
                return null;
            }

            BufferedImage grayscale = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY
            );
            Graphics2D g = grayscale.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(grayscale, "bmp", baos);
            byte[] imageBytes = baos.toByteArray();

            System.out.println("✅ Imagem capturada: " + imageBytes.length + " bytes");
            return imageBytes;

        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}
