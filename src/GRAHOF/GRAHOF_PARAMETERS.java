package GRAHOF;

/**
 * All parameters used by GRAHOF (alpha is currently hard-coded though)
 */

public class GRAHOF_PARAMETERS {

    public final int rho;
    final int S;
    final int kappa;
    public final double phi;
    public final double d;
    final int E;

    public GRAHOF_PARAMETERS(int S, int rho, int kappa, double phi, double d, int E) {

        if (rho <= 0) {
            throw new IllegalArgumentException("RHO <= 0");
        }
        this.rho = rho;

        if (S <= 0) {
            throw new IllegalArgumentException("S <= 0");
        }
        this.S = S;

        if (kappa == -1 || kappa > 1) {
            this.kappa = kappa;
        } else {
            throw new IllegalArgumentException("KAPPA<=1");
        }
        if (phi > 1 || phi < 0) {
            throw new IllegalArgumentException("PHI not in [0,1]");
        }
        this.phi = phi;

        if (d <= 0) {
            throw new IllegalArgumentException("d <= 0");
        }
        this.d = d;

        /*
         * This is currently in development
         */
        this.E = E;
    }

    /**
     * Create csv-saveable string with ';' as separator, no trailing or leading separators
     *
     * @return CSV-string value
     */
    public String toCSVString() {
        return this.S +
                ";" + this.rho +
                ";" + this.kappa +
                ";" + this.phi +
                ";" + this.d +
                ";" + this.E;
    }
}
