package ProcessUnits;

import GRAHOF.stuff;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is a start to read real-life event logs.
 */
public class _csv_stream extends EventStream {

    private final HashMap<String, Event> last_events = new HashMap<>();

    public _csv_stream(String fn_log, String fn_info) {
        super();
        Instances dataset;
        ArrayList<Attribute> atts = new ArrayList<>();
        HashMap<String, String> case_info = new HashMap<>();

        HashSet<String> precompute_activities = new HashSet<>();
        HashSet<String> precompute_cases = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fn_log))) {
            br.readLine(); // Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if(precompute_cases.contains(values[0])){
                    precompute_activities.add(values[1]);
                } else {
                    precompute_cases.add(values[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Info file does not exist");
        }
        ArrayList<String> all_labels = new ArrayList<>(precompute_activities);
        all_labels.add(stuff.short_case);

        try (BufferedReader br = new BufferedReader(new FileReader(fn_info))) {
            String line = br.readLine();
            ArrayList<HashSet<String>> attribute_values = new ArrayList<>();
            String[] attribute_names = line.split(";", 2)[1].split(";");
            for (String a_n : attribute_names) {
                attribute_values.add(new HashSet<>());
            }

            while ((line = br.readLine()) != null) {
                //Save case info in the map
                String[] id_val = line.split(";", 2);
                case_info.put(id_val[0], id_val[1]);

                //Add values to corresponding set of available values
                String[] values = id_val[1].split(";");
                for (int i = 0; i < values.length; i++) {
                    attribute_values.get(i).add(values[i]);
                }
            }

            for (int i = 0; i < attribute_names.length; i++) {
                ArrayList<String> att_values = new ArrayList<>(attribute_values.get(i));
                atts.add(new Attribute(attribute_names[i], att_values));
            }

            atts.add(new Attribute("Class", all_labels));
            dataset = new Instances(null, atts, 0);
            dataset.setClassIndex(atts.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Info file does not exist");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fn_log))) {
            String line;
            double current_time = -1;
            br.readLine(); //#Header
            while ((line = br.readLine()) != null) {
                //Get event values
                String[] evt_values = line.split(";");
                String case_id = evt_values[0];
                String act = evt_values[1];
                double time = Double.parseDouble(evt_values[2]);
                if (current_time > time) {
                    throw new IllegalStateException("Events are not chronological");
                } else {
                    current_time = time;
                }

                //Check if this is the first event of the Case
                if (case_info.containsKey(case_id)) {
                    //Start event
                    String[] str_values = case_info.remove(case_id).split(";");
                    if (str_values.length != atts.size() - 1) {
                        throw new IllegalStateException("Attribute length mismatch!");
                    }
                    double[] values = new double[atts.size()];
                    for (int i = 0; i < str_values.length; i++) {
                        values[i] = atts.get(i).indexOfValue(str_values[i]);
                    }
                    values[atts.size() - 1] = -1;
                    Instance instance = new DenseInstance(1, values);
                    instance.setDataset(dataset);

                    Start_Event e_s = new Start_Event(case_id, stuff.start_act_name, time, instance);
                    this.events.add(e_s);
                    this.last_events.put(case_id, e_s);
                } else {

                    //Check that we already had an event of this Case
                    if (!this.last_events.containsKey(case_id)) {
                        throw new IllegalStateException("Received Event for Case without info");
                    }
                    Event e = new Event(case_id, act, time);
                    this.last_events.put(case_id, e);
                    this.events.add(e);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Event log not found");
        }
    }

    @Override
    public boolean End(Event e) {
        return this.last_events.get(e.cid) == e;
    }

    @Override
    public Event next() {
        return this.events.poll();
    }

    @Override
    public int total_cases() {
        return this.last_events.size();
    }


}
