function output = chimpNormal(name, mean, variance, varargin)
    global chimplify_internal_program;
    if nargin == 4
        output = chimplify_internal_program.chimpNormal(name, mean, variance, varargin{1});
    else
        output = chimplify_internal_program.chimpNormal(name, mean, variance);
    end
end

