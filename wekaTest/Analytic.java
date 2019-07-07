package wekaTest;

import weka.core.Instances;

/*
 Ref: https://weka.wikispaces.com/Use+WEKA+in+your+Java+code
 API Doc: http://weka.sourceforge.net/doc.stable/
*/

public class Analytic
{
	public static void main(String[] args) throws Exception
	{
		int action = 1;
		// int[] nums = {21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,40};//Atributos a eliminar
		int[] nums = {1,23};
		//data.get( 0 ).value(0) -- Return attribute value
		//data.get( 0 ).attribute(0) -- Return attribute name and type

/*Reducción de attributos*/
				// Purge purifier = new Purge("C:/Users/Unicorn/Documents/Fac/DataMining/PF/fire.arff");
				// IOHandler.arff_writer("C:/Users/Unicorn/Documents/Fac/DataMining/PF/BD/fire-attr.arff", purifier.remove_attr( nums ));
				// System.out.println("Done");

/*Reducción de instancias*/
			// String data = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/clustered_3.arff";
			// String model = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/3Skm.model";
			// String centroids = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/centroids.arff";
			// String stdevs = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/stdevs.arff";
			//
			// Converter converter = new Converter(model, centroids, stdevs);
			// converter.model_to_arff();
			//
			// 	Supercluster superC = new Supercluster(data, centroids);
			// 	//superC.printFile(0);
			// 	superC.divide();
			// 	System.out.println( "===" + superC.cluster.length + "===" );
			// 	for(int i = 0; i < superC.cluster.length; i++)
			// 		System.out.println( superC.cluster[i].size );
			//
			// 		Instances newCluster0 = superC.cluster[0].shrinkCluster(new double[]{99,90,90,90,90,90});
			// 	Instances newCluster1 = superC.cluster[1].shrinkCluster(new double[]{99,90,90,90,90,90});
			// 	Instances newCluster2 = superC.cluster[2].shrinkCluster(new double[]{99,90,90,90,90,90});
			//
			// 	IOHandler.arff_writer("C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/subSet0.arff", newCluster0);
			// 	IOHandler.arff_writer("C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/subSet1.arff", newCluster1);
			// 	IOHandler.arff_writer("C:/Users/Unicorn/Documents/Fac/DataMining/PF/InstanceSelection/subSet2.arff", newCluster2);
			//
			// 	System.out.println("Done");

/*Clustering*/
		String data = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/Clustering/Complements/Skm_2_2.arff";
		String model = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/Clustering/Models/Skm_2_2_2.model";
		String centroids = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/Clustering/Centroids/Skm_2_2_2.arff";
		String stdevs = "C:/Users/Unicorn/Documents/Fac/DataMining/PF/Clustering/Stdevs/Skm_2_2_2.arff";

		Converter converter = new Converter(model, centroids, stdevs);
		converter.model_to_arff();

		Supercluster cluster = new Supercluster(data, centroids, stdevs);
		Instances complement = cluster.classifyInst(true);

		IOHandler.arff_writer("C:/Users/Unicorn/Documents/Fac/DataMining/PF/Clustering/Complements/Skm_2_2_2.arff", complement);
		cluster.count();
		System.out.println("Done");

	}

}
