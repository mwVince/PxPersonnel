import java.io.IOException;
import java.text.ParseException;

public class PxPersonnelStandalone {
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Welcome to PxPersonnel Standalone");
        PxPersonnelStandalone pxStandalone = new PxPersonnelStandalone();
        pxStandalone.start();
    }

    public void start() throws IOException, ParseException{
        LoginInterface.standaloneDriver();
    }
}
