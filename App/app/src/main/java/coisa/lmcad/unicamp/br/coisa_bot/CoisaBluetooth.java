package coisa.lmcad.unicamp.br.coisa_bot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by carlos on 6/9/16.
 */
public class CoisaBluetooth {

    private BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket connection;

    InputStream coisaIn;
    OutputStream coisaOut;

    private Thread monitor;
    private CoisaBluetooth() {
        monitor = new Thread(new Runnable() {
            public void run() {
                try {
                    BluetoothDevice coisa = mBluetoothAdapter.getRemoteDevice("20:14:10:15:09:68");
                    connection = coisa.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    connection.connect();
                    coisaIn = connection.getInputStream();
                    coisaOut = connection.getOutputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                while (true) {
                    try {
                        if (coisaIn.available() > 0) {
                            Thread.sleep(100);//Wait to get all the message
                            int size = coisaIn.read(coisaBuffer);

                            StringBuilder buffer = new StringBuilder();
                            for(int i = 0;i < size;i++){
                                buffer.append((char)coisaBuffer[i]);
                            } //get char array

                            String ch = buffer.toString();

                            ThingMonitor.instance.verifyExpected(ch);
                        } else {
                            Thread.sleep(100);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static CoisaBluetooth instance = null;

    public static CoisaBluetooth getInstance() {
        if(instance == null) {
            instance = new CoisaBluetooth();
        }
        return instance;
    }


    byte[] coisaBuffer = new byte[1024];

    public boolean isConnected() {
        if (connection != null) return connection.isConnected();
        return false;
    }

    public void connect() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if (!monitor.isAlive()) monitor.start();
        }
    }

    public void writeToCoisa(byte[] buffer) {
        try {
            coisaOut.write(buffer);
        } catch (IOException e) {
            instance = new CoisaBluetooth();
            e.printStackTrace();
        }
    }

}
