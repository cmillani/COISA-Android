package coisa.lmcad.unicamp.br.coisa_bot;

/**
 * Created by carlos on 6/9/16.
 */
public class ThingMonitor {

    private ThingMonitor() {

    }

    final public static ThingMonitor instance = new ThingMonitor();

    private int position = 0;
    String expected = "";
    String toSend = "";

    public void sendBinary(String binary) {
        String value = "RD";
        expected = "RD-OK";
        position = 0;
        int binsize = binary.length();
        value = value + (char)(binsize  & 0xFF) + (char)((binsize  >> 8) & 0xFF);
        toSend = value + binary;
        sendNext(2);
    }

    public void verifyExpected(String received) {
        if (received.equals(expected)) {
            if (expected.equals("RD-OK")) {
//                Toast.makeText(MainActivity.this, "Starting to Send!", Toast.LENGTH_LONG).show();
                System.out.println("Starting to Send!");
                expected = "k";
                sendNext(2); //Sends size
            }
            else if (expected.equals("k") & (position) == toSend.length()) { //End of buffer
//                Toast.makeText(MainActivity.this, "Sending Complete!", Toast.LENGTH_LONG).show();
                System.out.println("Sending Complete!");
                expected = "";
            }
            else {
                sendNext(Math.min(20, toSend.length() - position)); //Keeps sending code
            }
        } else {
//            Toast.makeText(MainActivity.this, "Error sending: expected<" + expected + "> received <" + received + ">", Toast.LENGTH_LONG).show();
            System.out.println("Error sending: expected<" + expected + "> received <" + received + ">");
        }
    }

    public void sendNext(int qtty) {
        System.out.println(qtty);
        String value = toSend.substring(position, position + qtty);
        byte[] array = new byte[qtty];

        for (int i = 0; i < qtty; i++) {
            array[i] = (byte)value.charAt(i);
        }

        System.out.println(array);
        CoisaBluetooth.getInstance().writeToCoisa(array);
//        coisaCharacteristic.setValue(array);
//        coisaGATT.writeCharacteristic(coisaCharacteristic);
        position += qtty;
    }
}
