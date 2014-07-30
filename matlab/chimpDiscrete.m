function output = chimpDiscrete(name, probabilities, domain)
    global chimplify_internal_program;
    
    if nargin == 2
        domain = 1:length(probabilities);
    end

    if length(probabilities) ~= length(domain)
        error('Both inputs must have the same length!')
    else
        output = domain(chimplify_internal_program.chimpDiscrete(name, probabilities)+1);
    end

end

