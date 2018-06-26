package xyz.misterkozo.rcjeff;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.util.TimeUnit;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    public final static String TAG = "Server";

    private Bitmap resultBitmap;
    private Bitmap mResult;

    private ServerSocket welcomeSocket;
    private Socket connectionSocket;

    private Mat mat;
    public Boolean connected;
    private int recordedFrames=0;
    private Boolean tryToRun = true;
    private int port;
    private boolean[] buttons;
    public boolean done, buttonsChanged, found, colorsChanged, picChanged;
    private String colors;

    public Server(int port) throws IOException {
        welcomeSocket = new ServerSocket(port);
        mat = null;
        connected = false;
        this.port = port;

        buttons = new boolean[]{false, false, false, false};
        found = false;
    }

    public void run() {
        while (tryToRun) { //as long as enabled
            try {
                Log.i(String.valueOf(port), "Waiting for connection...");
                connectionSocket = welcomeSocket.accept(); //freezes thread until accepted
                connectionSocket.setSoTimeout(2000);
                Log.i(String.valueOf(port), "Connected!");
                connected = true;
                DataOutputStream dOut = new DataOutputStream(connectionSocket.getOutputStream());
                InputStream in = connectionSocket.getInputStream();
                DataInputStream dIn = new DataInputStream(connectionSocket.getInputStream());
                // determine whether if it's commands or picture
                if (this.port == 42069) { //if command
                    while (!connectionSocket.isClosed()) { //as long as it's alive
                        if (buttonsChanged) {
                            String tmp = "";
                            for (int i = 0; i < buttons.length; i++) {
                                if (buttons[i]) {
                                    tmp += "p"; //pressed
                                } else {
                                    tmp += "u"; //unpressed
                                }
                            }
                            dOut.writeBytes(tmp + String.valueOf((char) 0));
                            dOut.flush(); //send commands
                            buttonsChanged = false;
                            //send acknowledged
                            if (dIn.read() != 253) {
                                connectionSocket.close();
                                break;
                            }
                            if (dIn.read() != 254) {
                                connectionSocket.close();
                                break;
                            }
                            if (dIn.read() != 255) {
                                connectionSocket.close();
                                break;
                            }
                        }
                    }
                } else if (this.port == 6969) { //else if picture
                    while (!connectionSocket.isClosed()) { //as long as it's alive
                        boolean okayToAdd = true;
                        byte[] b = new byte[4];
                        byte[] lb = new byte[4];
                        int ret = in.read(lb);
                        b[0] = lb[3];
                        b[1] = lb[2];
                        b[2] = lb[1];
                        b[3] = lb[0];
                        int l = ByteBuffer.wrap(b).getInt(); //fetch length
                        if (l <= 0 || l >= 65535) { //if length is weird
                            connectionSocket.close(); //commit suicide
                            break;
                        }

                        byte[] data = new byte[l];
                        int remain = l;
                        int pos = 0;
                        while (remain > 0) {
                            ret = in.read(data, pos, remain);
                            if (ret > 0) {
                                remain -= ret;
                                pos += ret;
                            } else {
                                okayToAdd = false;
                                break;
                            }
                        } //read the actual image
                        dOut.writeUTF("ack"); //send ack
                        dOut.flush();

                        while (true) { //as long as not killed
                            //get secondary ack which states whether or not detected
                            while (in.read() != 254);
                            int count = 0;
                            while (count < 3) {
                                int inread = in.read()*1;
                                if (inread == 254) {
                                    count++;
                                } else {
                                    break;
                                }
                            }
                            if (count == 3) {
                                int inread = in.read()*1;
                                if (inread == 255) {
                                    this.found = true;
                                    break;
                                } else if (inread == 254) {
                                    this.found = false;
                                    break;
                                } else {
                                    okayToAdd = false;
                                }
                            } else {
                                okayToAdd = false;
                            }
                        }
                        //all done
                        recordedFrames++;
                        if (okayToAdd) { //if nothing went wrong
                            //convert mat to bitmap and put in accessible variable
                            MatOfByte bMat = new MatOfByte(data);
                            this.mat = Imgcodecs.imdecode(bMat, 1);
                            Imgproc.cvtColor(this.mat, this.mat, Imgproc.COLOR_BGR2RGB);
                            this.resultBitmap = Bitmap.createBitmap(this.mat.cols(), this.mat.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(this.mat, resultBitmap);
                            this.mResult = resultBitmap;
                            this.picChanged = true;
                        } else { //if something went wrong
                            Log.i("RCJeff", "I died!");
                            boolean restart = true;
                            while (restart) { //dump all until next series of message end bytes
                                restart = false;
                                while (in.read() != 254);
                                int count = 0;
                                while (count < 3) {
                                    int inread = in.read()*1;
                                    if (inread == 254) {
                                        count++;
                                    } else {
                                        break;
                                    }
                                }
                                if (count == 3) {
                                    int inread = in.read()*1;
                                    if (inread == 255) {
                                        this.found = true;
                                        break;
                                    } else if (inread == 254) {
                                        this.found = false;
                                        break;
                                    } else {
                                        restart = true;
                                    }
                                } else {
                                    restart = true;
                                }
                            }
                            Log.i("RCJeff", "But I recovered");
                        }
                    }
                    Log.i(String.valueOf(port), "Disconnected!");
                    connected = false;

                } else if (this.port == 6666) { //color port
                    while (!connectionSocket.isClosed()) { //as long as it's alive
                        if (colorsChanged) { //if changed colors
                            dOut.writeBytes("  "+colors); //send colors
                            dOut.flush();
                            colorsChanged = false;
                        } //should've used listener here
                    }
                }
                //kill();
            } catch(IOException exception){
                Log.i(String.valueOf(port), exception.toString());
            }
        }
    }

    public Mat getMat() {

        return this.mat;

    }

    public Bitmap getBmp() {
        return this.mResult;
    }

    public Boolean isConnected() { return this.connected; }

    public void kill() { //kill the connection properly
        this.tryToRun = false;
        try {
            if (connectionSocket != null)
                connectionSocket.close();
            if (welcomeSocket != null)
                welcomeSocket.close();
            connected = false;
            this.interrupt();
        } catch (Exception e) {
            Log.e(TAG, "Could not kill: " + e.toString());
        }
    }

    public void softKill() {
        try {
            connectionSocket.close();
            connected = false;
        } catch (Exception e) {

        }
    }

    public void setButtonsNow(boolean[] buttons) {
        this.buttons = buttons;
        buttonsChanged = true;
    }

    public void setColors(String colors) {
        this.colors = colors;
        colorsChanged = true;
    }

}