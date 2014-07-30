function [ samples, likelihoods ] = chimplify(callback, args, burnin, samples, spacing)
    global chimplify_internal_program;
    global chimplify_internal_args;
    global chimplify_internal_results;
    global chimplify_internal_likelihoods;
    
    import com.gamelanlabs.chimple2.core.MatlabChimpleProgram;
    import com.gamelanlabs.chimple2.solvers.MatlabMetropolisHastingsSolver;
    
    chimplify_internal_args = args;
    chimplify_internal_program = MatlabChimpleProgram(func2str(callback));
    solver = MatlabMetropolisHastingsSolver(chimplify_internal_program);
    solver.solve(burnin, samples, spacing);
    
    samples = chimplify_internal_results;
    likelihoods = chimplify_internal_likelihoods;
end

