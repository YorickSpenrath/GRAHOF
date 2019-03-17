package GRAHOF;


import ProcessUnits.Event;
import ProcessUnits.EventStreamGenerator;
import ProcessUnits.Start_Event;

import com.yahoo.labs.samoa.instances.Instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * Class that takes a stream and saves all events in separate files for Start and non-Start events, saved per 1 time unit
 */
public class StreamSaver {

    public static void run(EventStreamGenerator s, String dir) throws FileNotFoundException {
        new File(dir).mkdirs();

        String fd_start = dir + "/start_events";
        String fd_event = dir + "/events";

        new File(fd_start).mkdir();
        new File(fd_event).mkdir();

        int log = 0;
        PrintWriter start_writer = new PrintWriter(fd_start + "/" + log + ".csv");
        PrintWriter event_writer = new PrintWriter(fd_event + "/" + log + ".csv");

        while (s.hasnext()) {
            Event e = s.next();
            if (e.time >= log + 1) {
                log++;
                start_writer.close();
                event_writer.close();
                start_writer = new PrintWriter(fd_start + "/" + log + ".csv");
                event_writer = new PrintWriter(fd_event + "/" + log + ".csv");
            }
            if (e instanceof Start_Event) {

                StringBuilder line = new StringBuilder(e.cid + ";" + e.act + ";" + e.time + ";");
                Instance i = ((Start_Event) e).data;
                double[] values = i.toDoubleArray();
                for (int topic = 0; topic < stuff.n; topic++) {
                    if ((int) values[0] == topic) {
                        line.append(1);
                    } else {
                        line.append(0);
                    }
                    line.append(";");
                }
                line.append((int) values[1]).append(";");
                line.append((int) values[2]);
                start_writer.println(line);
            } else {
                event_writer.println(e.cid + ";" + e.act + ";" + e.time);
            }
        }
        start_writer.close();
        event_writer.close();

    }
}
