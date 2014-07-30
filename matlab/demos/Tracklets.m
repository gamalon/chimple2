%PPAML CP3: WAMI tracklets
%This generative model of ten tracks includes channel erasure and a
%gaussian channel


function result=Tracklets()

numTracks=10;
timeSteps=30;
coords=cell(numTracks);
sigma=20;    %Gaussian channel variance

for i=1:numTracks
    coords{i}=zeros(timeSteps,7);
    coords{i}(:,5)=1:timeSteps;     %time column
    coords{i}(:,6)=i;   %ground truth track membership
end

%initial conditions
clear(['x0','y0','xdot0','ydot0'])
for i=1:numTracks
    x0=chimpRand(['x0',num2str(i)])*10;
    y0=chimpRand(['y0',num2str(i)])*10;
    xdot0=chimpNormal(['xdot0',num2str(i)],0,0.1);
    ydot0=chimpNormal(['ydot0',num2str(i)],0,0.1);
    coords{i}(1,1:4)=[x0,y0,xdot0,ydot0];
end

%moving on to coordinate generation...
for i=2:timeSteps
    for j=1:numTracks        
        trackAlive=chimpFlip([['aliveNoise',num2str(i)],num2str(j)],0.8);  %track dies if car stops
        if trackAlive == 1
            coords{j}(i,1)=coords{j}(i-1,1)+coords{j}(i-1,3);
            coords{j}(i,2)=coords{j}(i-1,2)+coords{j}(i-1,4);
%             changeDirect=
%             if changeDirect == 1
%             
            coords{j}(i,3)= coords{j}(i-1,3) + chimpNormal([['xdotNoise',num2str(j)], num2str(i)],0,0.1);
            coords{j}(i,4)= coords{j}(i-1,4) + chimpNormal([['ydotNoise',num2str(j)], num2str(i)],0,0.1);
        else
            coords{j}(i,1)=coords{j}(i-1,1);
            coords{j}(i,2)=coords{j}(i-1,2);
            coords{j}(i,3)=0;
            coords{j}(i,4)=0;
        end
    end
    
    %Gaussian channel permutations
    
    energyStates=zeros(numTracks,numTracks);
    for j=1:numTracks   %generating possible energy states
        for k=1:numTracks
            energyStates(j,k)=exp(-((coords{j}(i,1)-coords{k}(i,1))^2 ...
                +(coords{j}(i,2)-coords{k}(i,2))^2)/(sigma^2));
        end
        energyStates(j,j)=0;    %since we only want cases j ~= k, this decreases the partition fctn 
    end
    
    partitionFctn=sum(sum(energyStates));   %generating partition function
    
    for j=1:numTracks
        for k=1:numTracks
            flipTracks=chimpFlip([[['flipTracks',num2str(i)],num2str(j)],num2str(k)],...
                energyStates(j,k)/partitionFctn);
            if flipTracks == 1
                coords{j}(i,7)=coords{k}(i,6);
                coords{k}(i,7)=coords{j}(i,6);
            else
                coords{j}(i,7)=coords{j}(i,6);
            end
        end
    end
    for j=1:numTracks
        coords{j}(1,7)=j;
    end
end
result=coords;            
end
