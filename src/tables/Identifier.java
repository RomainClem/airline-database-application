package tables;

import java.time.Instant;

public class Identifier {
    private static int counter = 10;
    public Identifier(){
        // this allow 89 request per second and avoid adding an additional character
        if(counter < 99){
            counter++;
        } else {
            counter = 0;
        }

    }

    public String createID(String s){
        long unixTime = Instant.now().getEpochSecond();
        int epochInt = (int) unixTime;
        String id = s + "_" + Integer.toHexString(epochInt) + counter;
        return id;
    }

}
