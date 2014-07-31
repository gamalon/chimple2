%Harness for running genModelTracklets3.m

burnin = 0;
samples = 1;
spacing = 0;

tic

groundTruth3 = chimplify(@Tracklets,{},burnin,samples,spacing);
groundTruth3 = groundTruth3{1};

plot(groundTruth3{1}(:,1),groundTruth3{1}(:,2),groundTruth3{2}(:,1),groundTruth3{2}(:,2),...
    groundTruth3{3}(:,1),groundTruth3{3}(:,2),groundTruth3{4}(:,1),groundTruth3{4}(:,2),...
    groundTruth3{5}(:,1),groundTruth3{5}(:,2),groundTruth3{6}(:,1),groundTruth3{6}(:,2),...
    groundTruth3{7}(:,1),groundTruth3{7}(:,2),groundTruth3{8}(:,1),groundTruth3{8}(:,2),...
    groundTruth3{9}(:,1),groundTruth3{9}(:,2),groundTruth3{10}(:,1),groundTruth3{10}(:,2))

%data1=groundTruth3{1};
%data2=groundTruth3{2};
%data3=groundTruth3{3};
%data4=groundTruth3{4};
%data5=groundTruth3{5};
%data6=groundTruth3{6};
%data7=groundTruth3{7};
%data8=groundTruth3{8};
%data9=groundTruth3{9};
%data10=groundTruth3{10};

%dataPreSort=cat(1,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10);
%data=sortrows(dataPreSort,5);
%save('groundTruth3.mat','data')
%savefig('groundTruth3.fig')


toc