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

    public static CoisaBluetooth instance = new CoisaBluetooth();

    private CoisaBluetooth() {

    }

    private BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket connection;

    InputStream coisaIn;
    OutputStream coisaOut;

    byte[] coisaBuffer = new byte[1024];

    public void connect() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println("Ta indo");
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            System.out.println("Innn");
//                BluetoothDevice coisa = mBluetoothAdapter.getRemoteDevice("20:C3:8F:F6:48:C3");
            BluetoothDevice coisa = mBluetoothAdapter.getRemoteDevice("20:14:10:15:09:68");
            System.out.println(coisa);
            try {
                connection = coisa.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                connection.connect();
                coisaIn = connection.getInputStream();
                coisaOut = connection.getOutputStream();
//                    byte[] oute = {'A','L','O'};
//                    coisaOut.write(oute);
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                if (coisaIn.available() > 0) {
                                    Thread.sleep(100);//Wait to get all the message - gambs
                                    int size = coisaIn.read(coisaBuffer);
                                    String read = new String(coisaBuffer, "UTF-8");


                                    StringBuilder buffer = new StringBuilder();
                                    for(int i = 0;i < size;i++){
                                        buffer.append((char)coisaBuffer[i]);
                                    }

//get char array
                                    String ch = buffer.toString();


                                    ThingMonitor.instance.verifyExpected(ch);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                coisaGATT = coisa.connectGatt(this, false, mGattCallback);
//                List<BluetoothGattService> list = coisaGATT.getServices();
//                System.out.println(list.size());
//                for (BluetoothGattService serv : list) {
//                    System.out.println(serv.toString());
//                }
//                BluetoothGattService coisaService = coisaGATT.getService(UUID.fromString("00002020-0000-1000-8000-00805F9B34FB"));
//                System.out.println(coisaService);
        }
    }

    public void writeToCoisa(byte[] buffer) {
        try {
            coisaOut.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
