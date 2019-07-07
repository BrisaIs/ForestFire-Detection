package wekaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

public class Neighborhood{

  private static class Neighbor implements Comparator<Neighbor>, Comparable<Neighbor>
  {
    public Double distance;
    public int index;
    Neighbor(Double dist, int i){this.distance = dist; this.index = i;}

    @Override
    public int compareTo(Neighbor d) {
      return (this.index == d.index) ? 1 : 0;
    }

    @Override
    public int compare(Neighbor dA, Neighbor dB) {
      return (int)( dA.distance - dB.distance);
    }

    @Override
    public String toString() {
      String a = "Distance: "+Double.toString(this.distance);
      String b = "Index: "+Integer.toString(this.index);

      return a+"\n"+b;
    }
  }

  private List<Neighbor> neighbors;
  private Instances residents, to_allocate;
  private ManhattanDistance calculator;
  private int density;

  Neighborhood(Instances residents, Instances to_alloc, int density)
  {
    this.residents = residents;
    this.to_allocate = to_alloc;
    this.density = density;
    calculator = new ManhattanDistance( this.residents );
    this.neighbors = new ArrayList<Neighbor>();
  }

  /*
    a: instace to compare with all the other ones from b
  */
  private List<Neighbor> get_dist(Instance a)
  {
    List<Neighbor> cluster = new ArrayList<Neighbor>();

    Neighbor temp_neighbor = null;
    for(int i = 0; i < this.residents.numInstances(); i++)
    {
      temp_neighbor = new Neighbor( (Double)this.calculator.distance(a, this.residents.get(i)), i );
      cluster.add( temp_neighbor );
    }

    Collections.sort( cluster );
    return cluster;
  }

  private void get_neighbors( )
  {
    for(int i = 0; i < this.to_allocate.numInstances(); i++)
    {

      this.get_dist( this.to_allocate.get(i) );
      this.neighbors.addAll(
      this.get_dist( this.to_allocate.get(i) )
      .stream()
      .distinct()
      .collect(Collectors.toList())
      .subList(0, this.density-1)
      );
    }
  }

  private Double[] get_neighborsInfo( int attr_index)
  {
    Double[] info = new Double[this.neighbors.size()];
    int i = 0;
    for( Neighbor n : this.neighbors  )
    {
      info[i] = (Double) this.residents.get(n.index).value(attr_index);
      i++;
    }
    return info;
  }

  public Double[] get_description( int attr_index )
  {
    this.get_neighbors();
    Double[] neighbors_info = this.get_neighborsInfo( attr_index );

    Double[] description = new Double[2]; //[0]: media | [1]: standard deviation
    Statistics stats = new Statistics(neighbors_info);
    description[0] = stats.getMean();
    description[1] = stats.getStdDev();

    return description;
  }

  public Instances populate( Double[] values, int attr_index )
  {
    int MEAN_INDEX = 0; int STD_DEV_INDEX = 1;
    Double min = values[MEAN_INDEX] - values[STD_DEV_INDEX];
    Double max = values[MEAN_INDEX] + values[STD_DEV_INDEX];

    double rand_num = ThreadLocalRandom.current().nextDouble(min, max+1);;
    for(int i = 0; i < this.to_allocate.numInstances(); i++)
      this.to_allocate.get(i).setValue(attr_index, rand_num);

    return this.to_allocate;
  }
}
