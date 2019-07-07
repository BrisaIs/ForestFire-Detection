package wekaTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;

class Cluster
{
	private Instances instInCluster;
	private int attrPerInst;
	public Instance centroid;
	public int size;

	Cluster(Instances inst, Instance centroid)
	{
		this.instInCluster = inst;
		this.attrPerInst = this.instInCluster.numAttributes();
		this.size = this.instInCluster.numInstances();
		this.centroid = centroid;
	}

	public void set_centroid(Instance centroid) {this.centroid = centroid;}
/*============================================================================*/

	private List<Double> get_dist()
	/*Calculates the distances betwen the centroid and all the instances in the cluster*/
	{
		ManhattanDistance calculator = new ManhattanDistance(this.instInCluster);
		List<Double> centroidToInst = new ArrayList<Double>();

		Double distance;
		for(int i = 0; i < this.size; i++)
		{
			distance = (Double)calculator.distance(this.centroid, this.instInCluster.get(i));
			centroidToInst.add( distance );
		}

		return centroidToInst;
	}

	private List<Instances> get_layers(int amountOfLayers, List<Double> centroidToInst)
	{
		Double diameter = Collections.max( centroidToInst ); System.out.println( "Cluster diameter:" + diameter );
		Double layerWidth = diameter/amountOfLayers; System.out.println( "Layer's width:" + layerWidth );
		List<Instances> layer = new ArrayList<Instances>();
		for(int i = 0; i < amountOfLayers; i++)
			layer.add(new Instances(this.instInCluster, 0));

		class LayerComparer
		{
			public boolean compare(int index, Double dist, Double start, Double end)
			{
				if( index == 0 )
					return innerLayer(dist, start, end);
				else
					return otherLayer(dist, start, end);
			}
			private boolean innerLayer(Double dist, Double start, Double end)
			{return (dist >= start) && (dist <= end);}
			private boolean otherLayer(Double dist, Double start, Double end)
			{return (dist > start) && (dist <= end);}
		}
		Double startOfLayer, endOfLayer;
		LayerComparer comparer = new LayerComparer();
		for(int layerIdx = 0; layerIdx < amountOfLayers; layerIdx++)
		{
			startOfLayer = layerIdx*layerWidth;
			endOfLayer = startOfLayer+layerWidth;
			for(int instIdx = 0; instIdx < this.size; instIdx++)
			{
				if( comparer.compare(layerIdx, centroidToInst.get(instIdx), startOfLayer, endOfLayer) )
					layer.get(layerIdx).add( this.instInCluster.get(instIdx) );
			}
			System.out.println("Num of instances in layer "+ layerIdx +": "+layer.get(layerIdx).numInstances() );
		}

		return layer;
	}

	private List<Instances> shrink(List<Instances> layer, double[] toShrink)
	{
		Random randomGen = new Random();
		int instToRemove, tempLayerSize;
		Instances tempLayer = null;

		for(int layerIdx = 0; layerIdx < layer.size(); layerIdx++)
		{
			tempLayer = layer.get(layerIdx);
			tempLayerSize = tempLayer.numInstances();

			instToRemove = (int) (tempLayerSize*(toShrink[layerIdx]/100));
			for(int instIdx = 0; instIdx < instToRemove; instIdx++)
			{
				tempLayer.remove( randomGen.nextInt(tempLayerSize) );
				tempLayerSize--;
			}
			System.out.println("New layer "+ layerIdx +" size: "+ tempLayerSize );
		}

		return layer;
	}

	private Instances fuse(List<Instances> layer)
	{
		Instances inst = new Instances(instInCluster, 0);
		int layerSize = layer.get(0).numInstances();
		for(int layerIdx = 0; layerIdx < layer.size(); layerIdx++)
		{
			inst.addAll(layer.get(layerIdx));
			layerSize = layer.get(0).numInstances();
		}

		return inst;
	}

/*
	toShrink contiene las cantidades que se quieren quitar de cada capa, i.e., se conservará el complemento de la capa
*/
	public Instances shrinkCluster(double[] toShrink)
	{
		List<Double> centroidToInst = this.get_dist();
		List<Instances> layer = this.get_layers(toShrink.length, centroidToInst);
		layer = this.shrink(layer, toShrink);
		Instances new_dataSet = this.fuse(layer);

		return new_dataSet;
	}
}
