package ProcessUnits;


import java.text.DecimalFormat;
import java.util.*;

import ConceptDrift.Drifter.ConceptDrifter;
import GRAHOF.stuff;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

/**
 * Simulation of event stream.
 */

public class EventStreamGenerator extends EventStream {

    /**
     * This variable is not even supposed to exist. It sets the number of activities, and hence the number
     * of Bottlenecks. This allows experimenting on a different number of bottlenecks, primarily implemented for future work
     */

    private final int n = stuff.n;

    /**
     * Maximum number of pages and publications
     */
    private final int dim_size;

    /**
     * Number of simulated months
     */
    private final int nMonths;

    /**
     * Gradual concept drifter function
     */
    private final ConceptDrifter conceptdrifter;

    /**
     * transition duration from one recurrent concept to the next
     */
    private final double W;


    /**
     * Number of cases per month
     */
    private final int nCasesMonth;

    /**
     * Duration of a concept, for Future work
     */
    private final int t_concept = 12 / this.n;


    /**
     * Formatting options
     */
    private final DecimalFormat df_month;
    private final DecimalFormat df_nCasesMonth;

    //Quasi-constants (they are supposed to be constant, but so is {@code n}
    private final List<String> topics = stuff.simulation_topics();
    private final List<String> activities = stuff.simulation_topics();

    /**
     * This is for MOA
     */
    private final Instances dataset;

    private ArrayList<Attribute> attributes;

    /**
     * Uniform generators
     */
    private final RandomGenerator time_generator;
    private final RandomGenerator pages_generator;
    private final RandomGenerator publications_generator;
    private final RandomGenerator transition_generator;

    /**
     * Distributions for activity duration
     */
    private final GammaDistribution GD_short;
    private final GammaDistribution GD_long;

    /**
     * The addition time added to bottleneck case activities (all, not just the bottleneck activity)
     */
    private final double b_constant;

    /**
     * This variable keeps track of the months that are not generated, such that new events are generated in time
     */
    private int not_generated_month;


    /**
     * Constructor
     *
     * @param dim_size         Number of pages/publications
     * @param nYears           Number of years simulated
     * @param nCasesMonthTopic Number of cases per topic per month
     * @param shape            shape of gamma dist
     * @param scale            scale of gamma dist
     * @param conceptdrifter   Gradual concept drifter
     * @param w                Duration of transition between two recurrent concepts
     * @param seed             Random generators are increased with this seed, for reproducability
     * @param nb_shape_factor  factor before the shape in non-bottleneck events
     * @param b_shape_factor   factor before the shape in bottleneck events
     * @param b_constant       constant addition to events in bottleneck cases
     */
    EventStreamGenerator(int dim_size, int nYears, int nCasesMonthTopic, double shape, double scale, ConceptDrifter conceptdrifter, double w, int seed, double nb_shape_factor, double b_shape_factor, double b_constant) {
        super();
        this.dim_size = dim_size;
        this.conceptdrifter = conceptdrifter;
        W = w;

        /*
         * Set up generators from seeds. Each generator contains a base seed value
         */
        time_generator = RandomGeneratorFactory.createRandomGenerator(new Random(21091993 + seed));
        pages_generator = RandomGeneratorFactory.createRandomGenerator(new Random(14031963 + seed));
        publications_generator = RandomGeneratorFactory.createRandomGenerator(new Random(21032019 + seed));
        transition_generator = RandomGeneratorFactory.createRandomGenerator(new Random(25032018 + seed));
        RandomGenerator rng = RandomGeneratorFactory.createRandomGenerator(new Random(12051992 + seed));
        this.GD_short = new GammaDistribution(rng, shape * nb_shape_factor, scale);
        this.GD_long = new GammaDistribution(rng, shape * b_shape_factor, scale);
        this.b_constant = b_constant;

        /*
         * Derived values
         */
        this.nCasesMonth = n * nCasesMonthTopic;
        this.nMonths = nYears * 12;
        this.df_month = stuff.df(nMonths);
        this.df_nCasesMonth = stuff.df(nCasesMonth);

        /*
         * Create stuff for MOA
         */

        this.attributes = new ArrayList<>();
        attributes.add(new Attribute("Topic", stuff.simulation_topics()));
        attributes.add(new Attribute("Pages"));
        attributes.add(new Attribute("Publications"));
        int class_index = attributes.size();
        attributes.add(new Attribute("class", stuff.simulation_class_values()));
        this.dataset = new Instances(null, attributes, 0);
        this.dataset.setClassIndex(class_index);

        /*
         * Generate first two months
         */

        this.not_generated_month = 0;
        generate_cases(); //Cases for month 0
        generate_cases(); //Cases for month 1

    }

    /**
     * Generate new month worth of cases
     */
    private void generate_cases() {
        //Check if stream should end
        if (this.not_generated_month >= this.nMonths) {
            return;
        }

        //Generate cases
        for (int i = 0; i < this.nCasesMonth; i++) {
            generate_case(i);
        }

        //Advance not generated month to next month
        this.not_generated_month += 1;
    }

    /**
     * Probability of next concept during transition
     *
     * @param time time for simulation
     * @param t_0  time of actual transition
     * @return probability on new concept
     */

    private double P(double time, double t_0) {
        return 1 / (1 + Math.exp(-16.0 / this.W) * (time - t_0));
    }

    /**
     * Get the current concept (this is the sigmoid formula from the report/paper)
     *
     * @param time time of the simulation
     * @return the concept during that time (random between two if during transition)
     */
    private int get_concept(double time) {
        int concept = (((int) time) % 12) / (this.t_concept);

        if (time % 3 < 3 - this.W / 2 && time % 3 > this.W / 2) {
            return concept;
        }


        if (time % 3 >= 3 - this.W / 2) {
            //End of a concept
            double t_0 = ((int) (time / this.t_concept) + 1) * this.t_concept;
            if (this.transition_generator.nextDouble() > P(time, t_0)) {
                return concept;
            } else {
                return (concept + 1) % this.n;
            }
        } else {
            //Start of a concept
            double t_0 = ((int) (time / this.t_concept)) * this.t_concept;
            if (this.transition_generator.nextDouble() <= P(time, t_0)) {
                return concept;
            } else {
                return (concept - 1) % this.n;
            }
        }

    }

    /**
     * Generate a single case
     *
     * @param case_id_in_month index of the case in a month (for ID and for topic)
     */
    private void generate_case(int case_id_in_month) {

        //This index determines the activity
        int index = case_id_in_month % this.n;


        //Start Time
        double time = this.not_generated_month + time_generator.nextDouble();

        //This index determines the recurrent concept drift
        int concept = get_concept(time);

        //Data
        String topic = this.topics.get(index);
        int pages = this.pages_generator.nextInt(this.dim_size);
        int publications = this.publications_generator.nextInt(this.dim_size);
        double[] values = new double[]{this.attributes.get(0).indexOfValue(topic), pages, publications, -1};
        Instance instance = new DenseInstance(1, values);
        instance.setDataset(this.dataset);

        //Determine if it should be a short case
        boolean short_case = this.conceptdrifter.is_short(time, pages, publications);

        //Create identifier (which, for debugging purposes, contains all relevant info)
        String case_id = this.df_month.format(this.not_generated_month) +
                "_" +
                this.df_nCasesMonth.format(case_id_in_month) +
                "_" + (short_case ? "T" : "F") + "_" + concept;

        //Create new Start Event with data
        this.events.add(new Start_Event(case_id, stuff.start_act_name, time, instance));

        //Create rest of the Events
        if (short_case) {
            //Short cases have similar activity times
            for (int j = 0; j < this.n; j++) {
                time += (this.activity_duration_days(true)) / 30.0;
                this.events.add(new Event(case_id, this.activities.get(j), time));
            }
        } else {
            //Long cases have a specific activity that takes longer, this depends on index of the topic
            for (int j = 0; j < this.n; j++) {
                time += (this.activity_duration_days(((index + concept) % n) != j) + b_constant) / 30.0;
                this.events.add(new Event(case_id, this.activities.get(j), time));
            }
        }

    }

    /**
     * Duration for event (with constant factor)
     * @param isNotBottleneck Whether the event is a bottleneck
     * @return rng duration of the event
     */
    private double activity_duration_days(boolean isNotBottleneck) {
        if (isNotBottleneck) {
            return this.GD_short.sample();
        } else {
            return this.GD_long.sample();
        }
    }

    /**
     * Get the concept drifter
     * @return the concept drift of this simulation
     */
    public ConceptDrifter get_cd() {
        return this.conceptdrifter;
    }

    @Override
    public boolean End(Event e) {
        return (stuff.AEnd().contains(e.act));
    }

    @Override
    public Event next() {
        if (events.isEmpty()) {
            return null;
        }
        Event e = events.poll();

        //Check if we should generate a new month worth of cases
        if (e.time > not_generated_month - 1) {
            generate_cases();
        }
        return e;
    }

    @Override
    public int total_cases() {
        return this.nMonths * this.nCasesMonth;
    }
}