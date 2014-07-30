function heads = RandomCoin()
    weight = chimpRand('weight');
    X = zeros(1, 20);
    for i=1:20
        X(i) = chimpFlip(sprintf('X%d', i), weight);
    end
    heads = sum(X);
    
    % Binomial log-likelihood for observing 15 heads and 5 tails
    addEnergy(-(15*log(weight)+5*log(1-weight)));
end

