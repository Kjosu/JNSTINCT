# JNSTINCT

Java implementation of the Neat/Instinct algorithm.

Easy to use for nearly every purpose.

## USAGE

1. Create your Genome instance

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

3. Evolve your network

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
