chimple2
========

**Java Developer Quick Start**:
Clone the repository and write a class that extends ChimpleProgram. You will be required to implement the `run(Object... args)` function. Write your probabilistic program here (for your first program, try a simple coin model). For help, see the demos.

Then, add a cost function, either inline using `addEnergy()` inside your probabilistic program, or by extending `CostFunction`. This cost function will represent data that you are conditioning on.

Finally, write a `public static void main(String[] args)` function that instantiates your program class and calls `MHQuery()`, and run the class.

**Latest Release**:
https://github.com/gamelanlabs/chimple2/releases/latest

**Packaging**
```$bash
mvn -U clean package
```

**Running**
```$bash
java -jar target/chimple2-1.0.0-jar-with-dependencies.jar "/the/path/to/input/image.jpg"
```
