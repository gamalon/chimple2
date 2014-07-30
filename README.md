chimple2
========

**Demos Quick Start**:
Clone the repository and find the releases/chimple-(version).jar file, or download the JAR from below, and run in (on many operating systems, this can be done simply by double-clicking).

**Java Developer Quick Start**:
Clone the repository and write a class that extends ChimpleProgram. You will be required to implement the `run(Object... args)` function. Write your probabilistic program here (for your first program, try a simple coin model). For help, see the demos.

Then, add a cost function, either inline using `addEnergy()` inside your probabilistic program, or by extending `CostFunction`. This cost function will represent data that you are conditioning on.

Finally, write a `public static void main(String[] args)` function that instantiates your program class and calls `MHQuery()`, and run the class.

**MATLAB Developer Quick Start**:
Clone the repository and run `startup.m` in the base directory. Write a MATLAB function that implements your probabilistic program, calling `addEnergy()` to condition on data. Then write a MATLAB script that calls `chimplify` on your program, passing in arguments, burn-in iterations, sample iterations, and spacing iterations, and run that script.

**Latest Release**:
https://github.com/gamelanlabs/chimple2/releases/latest
