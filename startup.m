fileparts(mfilename('fullpath'));
chimple_base = fileparts(mfilename('fullpath'));

% Add MATLAB functions to MATLAB path
chimple_matlab = [chimple_base '/matlab'];
addpath(chimple_matlab);
addpath([chimple_matlab, '/demos'])

% Add Java classes to Java path
chimple_classes = fullfile(chimple_base, 'bin');
%chimple_jar = fullfile(chimple_base, 'lib', ...
%   ['chimple-' chimpleVersionNumber() '.jar']);
if exist(chimple_classes, 'dir')
    javaaddpath(chimple_classes);
%elseif exist(chimple_jar)
%    javaaddpath(chimple_jar,'-end');
else
    error('Chimple has not been built');
end

% Add external dependencies to Java path
chimple_extlibs = fullfile(chimple_base, 'external-libs');
chimple_extlibs_glob = fullfile(chimple_extlibs, '*.jar');
files = dir(chimple_extlibs_glob);
for file = files'
    javaaddpath(fullfile(chimple_extlibs, file.name));
end

% Set up internal global variables
global chimplify_internal_data;
global chimplify_internal_program;
global chimplify_internal_results;
global chimplify_internal_likelihoods;