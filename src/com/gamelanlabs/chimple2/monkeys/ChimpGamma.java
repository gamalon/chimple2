package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.special.Gamma;

/**
 * Gamma distribution ERP
 *
 * @author dicai
 */
public class ChimpGamma extends Monkey<Double> {
    protected double alpha;
    protected double beta;
    // TODO: move this into ChimpleProgram
    protected double walk_sigma = 0.1;

    @Override
    public Double generate() {
        // TODO: confirm this is the correct parameterization
        value = getRandom().nextGamma(alpha, beta);
        return getValue();
    }

    @Override
    public Double propose() {
        value = getRandom().nextGaussian()*walk_sigma + getValue();
        return getValue();
    }

    /**
     * Negative log-likelihood of current value.
     *
     * @return	energy	Negative log-likelihood
     */
    @Override
    public double energy() {
        double energy = 0;
        double x = getValue();
        energy += alpha* Math.log(beta) - Gamma.logGamma(alpha);
        energy += (alpha-1) * Math.log(x) - beta * x;
        return -1 * energy;
    }

    /**
     * Negative log-likelihood of proposing current value
     * from previous value.
     *
     * @param	fromvalue	Previous value
     */

    @Override
    public double transitionEnergy(Double fromvalue) {
        // Proposal kernel is deterministic, so likelihood is
        // always 1.
        return 0;
    }

    /**
     * Sets parameters
     *
     * @param	pars	The pair of parameters
     */
    @Override
    public void setParams(Object... pars) {
        //params = ArrayUtils.toPrimitive((double[]) pars);
        alpha = (Double)pars[0];
        beta = (Double)pars[1];
    }

    /**
     * Returns an unsafe safe copy of the parameters of this monkey.
     *
     * @return	params
     */
    @Override
    protected Object[] getParams() {
        return new Object[] {alpha,beta};
    }


    /**
     * Compares parameters
     *
     * @param	newparams
     * @return	changed
     */
    @Override
    public boolean paramsChanged(Object... newparams) {
        return alpha != (double) newparams[0] ||
                beta != (double) newparams[1];
    }
}
