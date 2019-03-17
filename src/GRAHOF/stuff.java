package GRAHOF;

import ProcessUnits.Case;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class stuff {


    //TODO: set this, all experiment files will be written here
    public static String base_dir = "";

    public static String short_case = "ShortCase";


    public static int minClassSize = 5;
    public static int kappa_sim = 2;
    public static String start_act_name = "StartEvent";

    static private List<String> some_list = Arrays.asList(short_case, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L");

    public static int n = 4;
    public static int nCasesMonthTopic = 250;

    public static int reps = 30;

    static public List<String> simulation_topics() {
        return some_list.subList(1, n + 1);
    }

    static public List<String> simulation_class_values() {
        return some_list.subList(0, n + 1);
    }

    static public HashSet<String> AEnd() {
        HashSet<String> a = new HashSet<>();
        a.add(some_list.get(n));
        return a;
    }


    static String deNominalise(Instance instance, boolean include_label) {
        StringBuilder retString = new StringBuilder();
        retString.append('[');
        double[] values = instance.toDoubleArray();
        String prefix = "";
        for (int i = 0; i < instance.numAttributes(); i++) {
            if (!include_label && instance.classIndex() == i) {
                continue;
            }
            retString.append(prefix);
            prefix = " ,";
            Attribute a = instance.attribute(i);
            if (a.isNominal()) {
                retString.append(a.value((int) values[i]));
            } else {
                retString.append(values[i]);
            }
        }
        retString.append(']');
        return retString.toString();
    }


    public static <T> T argmax(HashMap<T, Double> hm) {
        Optional<Map.Entry<T, Double>> a = hm.entrySet().stream().max((entry1, entry2) -> Double.compare(entry1.getValue(), entry2.getValue()));
        return a.map(Map.Entry::getKey).orElse(null);
    }


    public static int argmax(double[] values) {
        if (values.length == 1) {
            return 0;
        }
        if (values.length == 0) {
            return -1;
        }
        int index = -1;
        double max = -1.0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0) {
                throw new IllegalArgumentException("Input must be positive");
            }
            if (values[i] > max) {
                index = i;
                max = values[i];
            }
        }
        return index;
    }

    public static double mu = 1;
    public static double alpha = 0.2;

    public static double[] normalise(double[] votes) {
        double sum = sum(votes);
        if (sum == 0.0 || Double.isNaN(sum)) {
            return null;
        }
        for (int i = 0; i < votes.length; i++) {
            votes[i] /= sum;
        }
        return votes;
    }

    private static double sum(double[] dArray) {
        return Arrays.stream(dArray).sum();
    }

    static String getLabelFromVotes(double[] votesForInstance) {
        return simulation_class_values().get(argmax(votesForInstance));
    }

    static double eps = 1e-6;

    public static DecimalFormat df(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < ("" + (i - 1)).length(); j++) {
            sb.append("0");
        }
        return new DecimalFormat(sb.toString());
    }

    public static ArrayList<Collection<Case>> stratify(Collection<Case> d, double train_fraction, long seed) {
        Random rng = new Random(seed);
        HashSet<Case> train = new HashSet<>();
        HashSet<Case> test = new HashSet<>();

        HashMap<String, ArrayList<Case>> stratified = new HashMap<>();

        for (Case c : d) {
            stratified.putIfAbsent(c.getTrue_label(), new ArrayList<>());
            stratified.get(c.getTrue_label()).add(c);
        }
        for (String label : stratified.keySet()) {
            int count = stratified.get(label).size();
            int test_size = (int) ((1 - train_fraction) * count);
            while (stratified.get(label).size() > test_size) {
                train.add(stratified.get(label).remove(rng.nextInt(stratified.get(label).size())));
            }
            test.addAll(stratified.get(label));
        }

        ArrayList<Collection<Case>> ret = new ArrayList<>();
        ret.add(train);
        ret.add(test);
        return ret;

    }

    public static Double average(ArrayList<Double> correct_weight) {
        OptionalDouble ret = correct_weight.stream().mapToDouble(d -> d).average();
        if (ret.isPresent()) {
            return ret.getAsDouble();
        } else {
            return null;
        }
    }

    public static Double std(ArrayList<Double> correct_weight) {
        Double avg = average(correct_weight);
        if (avg == null) {
            return null;
        }
        OptionalDouble ex2 = correct_weight.stream().mapToDouble(d -> (d * d)).average();
        if (ex2.isPresent()) {
            return Math.sqrt(ex2.getAsDouble() - avg * avg);
        }
        return null;
    }

    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    public static String stringAvgStd(ArrayList<Double> arrayList) {

        if (arrayList.isEmpty()) {
            return "NaN;NaN";
        } else {
            double avg = arrayList.stream().mapToDouble(d -> d).average().getAsDouble();
            double std = arrayList.stream().mapToDouble(d -> (d - avg) * (d - avg)).average().getAsDouble();
            return avg + ";" + Math.sqrt(std);
        }

    }
}
