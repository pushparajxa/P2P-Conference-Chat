/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import com.sun.org.apache.bcel.internal.generic.GOTO;
import de.uniba.wiai.lspi.chord.console.command.entry.Key;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ChordCallback;
import de.uniba.wiai.lspi.chord.service.ChordFuture;
import de.uniba.wiai.lspi.chord.service.ChordRetrievalFuture;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import java.io.*;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dipesh
 */
public class test {

    static Chord chord1;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, MalformedURLException, ServiceException, IOException {
        PropertiesLoader.loadPropertyFile();
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        URL localURL = null;
        URL bootstrapURL = null;
//localURL.getProtocol()
        InetAddress inetAddress = InetAddress.getLocalHost();
        String localhostname = java.net.InetAddress.getLocalHost().getHostName();
        try {
            System.out.println(localhostname);
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println(addr);
            System.out.println(protocol);
            System.out.println(protocol + "://" + addr);
            bootstrapURL = new URL("ocsocket://192.168.1.9:4111/");

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
//Chord chord = new de.uniba.wiai.lspi.chord.service.impl.ChordImpl();
//try {
//chord.create ( bootstrapURL ) ;
//} catch (ServiceException e ) {
//throw new RuntimeException ( " Could not create DHT ! " , e ) ;
//}

        chord1 = new de.uniba.wiai.lspi.chord.service.impl.ChordImpl();
//URL url = new URL("http", host, port + i, "");
        localURL = new URL("ocsocket://192.168.1.9:4119/");
        try {
            //chord1.create ( localURL );
            chord1.join(localURL, bootstrapURL);
            System.out.println("node 1 jooined");
            System.out.println(chord1.getID());
        } catch (ServiceException e) {
            throw new RuntimeException(" Could not join DHT ! ", e);
        }
    }
}


/**
 *
 * @author dipesh
 */

