% Harness for RandomCoin
% Author: BenL
burnin = 100;
samples = 1000;
spacing = 2;

% Run traceMH
out = cell2mat(chimplify(@RandomCoin, {}, burnin, samples, spacing));
disp(out);

% Make a nice plot
figure
hist(out, 0:20)
title('20 Flips of a Random Coin - Posterior Distribution')
xlabel('Number of heads')
ylabel('Frequency')