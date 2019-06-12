# JNSTINCT

Java implementation of the Neat/Instinct algorithm.

Easy to use for nearly every purpose.

## FEATURES

- Custom evolve function
- Custom mutations
- Custom selection algorithms

## USAGE

1. Create your Genome instance (You need both constructors)

```java
class XORGenome extends Genome<XORGenome> {

  public XORGenome(final Neat<XORGenome> neat, final int inputSize,
                    final int outputSize, final boolean createGenes) {
    super(neat, inputSize, outputSize, createGenes);
  }
  
  public XORGenome(final Neat<XORGenome> neat, final XORGenome father,
                    final XORGenome mother, final boolean createGenes) {
    super(neat, father, mother, createGenes);
  }
}
```

2. Create your Neat/DefaultNeat/SpeciationNeat instance

```java
class XORNeat extends SpeciationNeat<XORGenome> {

  public XORNeat(int populationSize) {
    super(2, 1, populationSize);
  }
  
  @Override
  public XORGenome createGenome(final Neat<XORGenome> neat, final int inputSize,
                                 final int outputSize, final boolean createGenes) {
    return new XORGenome(neat, inputSize, outputSize, createGenes);
  }
  
  @Override
  public XORGenome createGenome(final Neat<XORGenome> neat, final XORGenome father,
                                 final XORGenome mother, final boolean equal) {
    return new XORGenome(neat, father, mother, equal);
  }
  
  @Override
  public double evaluateFitness(XORGenome genome) {
    double fitness = 4;
    
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        double expected = i ^ j;
        double output = genome.activate(new double[] { i, j })[0];
        double result = output - expected;
        
        fitness -= result * result;
      }
    }
    
    return fitness * 10;
  }
}
```

4. Setup your network and evolve it

```java
private static final int generations = 1000;

public static void main(String[] args) {
  XORNeat neat = new XORNeat(100);
  
  for (int i = 0; i < generations; i++) {
    neat.evolve();
  }
  
  XORGenome fittest = neat.getFittestGenome();
  ...
}
```

## Network settings

```java
Neat.mutations.addAll(      // Allowed Mutations
  neat.addNode,
  neat.subNode,
  neat.addConnection,
  neat.subConnection,
  neat.modifyWeight,
  neat.modifyBias,
  neat.modifySquash,
  ...
);

Neat.selection = new FitnessProportionateSelection(); // default

Neat.equal = false; // default
Neat.clear = false; // default
Neat.elitism = 0;   // default
Neat.mutationRate = .3; // default
Neat.mutationAmount = 1; // default
Neat.growth = 1; // default
Neat.maxNodes = 0; // infinite // default
Neat.maxConnections = 0; // infinite // default
Neat.maxGates = 0; // infinite // default

SpeciationNeat.excessCoefficient = 1D; // default
SpeciationNeat.disjointCoefficient = 1D; // default
SpeciationNeat.weightCoefficient = 1D; // default
SpeciationNeat.compatibilityThreshold = 3D; // default
SpeciationNeat.weakEliminatePercentage = .2D; // default
SpeciationNeat.maxStaleness = 20; // default

```

## CUSTOM MUTATIONS

Every mutation i already implemented is part of a network.

```java
Neat.addNode
Neat.subNode
Neat.addConnection
Neat.subConnection
Neat.addGate
Neat.subGate
Neat.addSelfConnection
Neat.subSelfConnection
Neat.modifyWeight
Neat.modifyBias
Neat.modifySquash
Neat.swapNodes
```

Create your own mutation

```java
class CustomMutation implements Mutation {

  @Override
  public <T extends Genome<T>> void mutate(Neat<T> neat, T genome) {
    ...
  }
}


// Add allowed mutations
Neat.mutations.addAll(
  new CustomMutation(),
  Neat.addNode,
  Neat.subNode
);
```

## CUSTOM SELECTION

```java
class CustomSelection implements Selection {

  @Override
  public <T extends Genome<T>> T select(Neat<T> neat) {
    return ...
  }
}


Neat.selection = new CustomSelection();
```
