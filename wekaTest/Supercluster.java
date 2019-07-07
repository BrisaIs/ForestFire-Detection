package wekaTest;

import java.util.Arrays;

import weka.core.Instances;

public class Supercluster {
  private static final int FRONTIER = -1;
  private static final int NOISE = -2;

  private Instances dataSet;
  private int clustersAmount, attrAmount, dataSize;
  private Instances centroidInst, sdevInst;

  private int[] instClassification;//Aqui se guardan los resultados de la clasificacion en clusters de cada instancia.
  public Cluster[] cluster;

  Supercluster(String data_path, String centroids_path)
  {
    this.dataSet = IOHandler.load(data_path);
		this.centroidInst = IOHandler.load(centroids_path);
		this.sdevInst = null;

		this.clustersAmount = this.centroidInst.numInstances();
		this.attrAmount = this.dataSet.numAttributes();
		this.dataSize = this.dataSet.numInstances();

    this.cluster = null;
		this.instClassification = null;
  }

  Supercluster(String data_path, String centroids_path, String sdev_path)
	{
		this.dataSet = IOHandler.load(data_path);
		this.centroidInst = IOHandler.load(centroids_path);
		this.sdevInst = IOHandler.load(sdev_path);

		this.clustersAmount = this.centroidInst.numInstances();
		this.attrAmount = this.dataSet.numAttributes();
		this.dataSize = this.dataSet.numInstances();

    this.cluster = null;
		this.instClassification = null;
	}

  //Imprime los .arff segun el entero que se le pasa
  public void printFile(int index)
  {
    switch(index)
    {
      case 0:
        System.out.println("\nDataset:\n");
        System.out.println( this.dataSet );
        break;
      case 1:
        System.out.println("\nCentroids:\n");
        System.out.println( this.centroidInst );
        break;
      case 2:
        System.out.println("\nStandard Deviations:\n");
        System.out.println( this.sdevInst );
        break;
    }
  }

/*============================================================================*/
  /*
		Hace las comparaciones de las instancias con los datatos de cada cluster.
		Almacena los resultados de las comparaciones en comparissonResults.
		Llama a compare, para determinar en que cluster se encuentra, si es ruido o est� en frontera
	*/
	public Instances classifyInst(boolean consider_noise)
	{
    this.instClassification = new int[this.dataSize];
		int[] inst_membershipInfo = new int[ this.clustersAmount ];

		Instances wasteInst = new Instances(this.dataSet, 0);

		double centroid, sdev, leftmost, rightmost, attrVal;
		for(int k = 0; k < this.dataSize; k ++)
		{
			for(int i = 0; i < this.clustersAmount; i++)
			{
				for(int j = 0; j < this.attrAmount ; j++)
				{
					attrVal = this.dataSet.get(k).value(j);
					centroid = this.centroidInst.get(i).value(j);
					sdev = this.sdevInst.get(i).value(j);

					leftmost = centroid - sdev;
					rightmost = centroid + sdev;

					/*Para comprobar la Condici�n de pertenencia*/
					if( (attrVal >= leftmost) && (attrVal <=  rightmost) )//Condici�n de pertenencia
					     inst_membershipInfo[i]++;
				}
			}

			this.instClassification[k] = this.whatIsIt( inst_membershipInfo );
			Arrays.fill(inst_membershipInfo, 0);

			/*Si la instancia no se clasifica en algun cluster se imprime para ser consideradas en la
			siguiente corrida jerarquica.*/
			if( this.instClassification[k] == FRONTIER )
				wasteInst.add( this.dataSet.get(k) );
			else if( (this.instClassification[k] == NOISE) && consider_noise )
				wasteInst.add( this.dataSet.get(k) );
		}

		return wasteInst;
	}

	/*
		Lee los resultados que hay en comparissonResults (deber�a hacerlo una vez por cluster, se reutiliza la estructura)
		Se determina la clasificaci�n de la instancia
	*/
	private int whatIsIt( int[] instInCluster)
	{
		int frontier_coincidence = 0;
		boolean frontier_flag = true;
		for(int i = 0; i < this.clustersAmount-1; i++)
			if( frontier_flag = instInCluster[i] ==	this.attrAmount )
				for(int j = i+1; j < this.clustersAmount; j++)
					if(frontier_flag && (instInCluster[j] ==	this.attrAmount))
						frontier_coincidence++;

		int in_cluster = 0;
		boolean noisy_flag = true;
		if(frontier_coincidence == 0)
		{
			for(int i = 0; i < this.clustersAmount; i++)
				noisy_flag = noisy_flag && (instInCluster[i] < this.attrAmount);

			if( !noisy_flag )
				for(int i = 0; i < this.clustersAmount; i++)
					if(instInCluster[i] == this.attrAmount)
					{
						in_cluster = i;
						break;
					}
		}

		int result;
		if(frontier_coincidence > 0)
			result = FRONTIER;
		else if( noisy_flag )
			result = NOISE;
		else
			result = in_cluster; //cluster

		return result;
	}

	/*Cuenta las  instancias en cada cluster, frontera y ruido*/
	public void count()
	{
		int[] counter = new int[this.clustersAmount];
		int noise = 0;
		int infrontier = 0;

		for(int i = 0; i < this.dataSize; i++)
		{
			if( this.instClassification[i] >= 0 )
				counter[ this.instClassification[i] ]++;
			else
				switch(this.instClassification[i])
				{
					case NOISE:
						noise++;
						break;
					case FRONTIER:
						infrontier++;
						break;
				}
		}

		for(int i = 0; i < this.clustersAmount; i++)
			System.out.println("Cluster "+i+" = "+counter[i]);
		System.out.println("Noise = " + noise + "\nIn frontier = " + infrontier +  "\n");
	}

/*============================================================================*/

  public void divide()
  {
    //Creat & Instantiate
    Instances[] instPerCluster = new Instances[this.clustersAmount];
    for(int i = 0; i < this.clustersAmount; i++)
      instPerCluster[i] = new Instances(this.dataSet, 0);

    //Add instances corresponding to each cluster
    int clusterIdx;
    for(int i = 0; i < this.dataSize; i++)
    {
      clusterIdx = (int) this.dataSet.get(i).value(this.attrAmount-1);
      instPerCluster[ clusterIdx ].add( this.dataSet.get(i) );
    }

    this.cluster = new Cluster[this.clustersAmount];
    Purge purger = new Purge();
    for(int i = 0; i < this.clustersAmount; i++)
    {
      //Delet attr that indicates in which cluster is the instance
      purger.set_instSet(instPerCluster[i]);
      purger.remove_attr( new int[]{this.attrAmount-1} );
      //Assings instances to cluster
      cluster[i] = new Cluster( instPerCluster[i], centroidInst.get(i) );
    }
  }
}//0_Supercluster
