package wekaTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class Converter {

	String MODEL_PATH;
	String CENTROID_PATH;
	String STD_DEV_PATH;

	Converter()
	{
		MODEL_PATH = "C:/Users/Yishaq/Desktop/2oP_MD/test.model";
		CENTROID_PATH = "C:/Users/Yishaq/Desktop/2oP_MD/centroid.arff";
		STD_DEV_PATH = "C:/Users/Yishaq/Desktop/2oP_MD/centroid.arff";
	}

	Converter(String mPath, String cPath, String sDPath)
	{
		MODEL_PATH = mPath;
		CENTROID_PATH = cPath;
		STD_DEV_PATH = sDPath;
	}

	public void model_to_arff() throws Exception
	{
		SimpleKMeans skm = (SimpleKMeans) weka.core.SerializationHelper.read( MODEL_PATH ); //Deserialize model

		/* =======================================================
		 	CENTROIDS
		 */
		if( skm.getClusterCentroids() != null )
			IOHandler.arff_writer( CENTROID_PATH, skm.getClusterCentroids() );
		else
		{
			System.out.println( "No centroids found." );
			System.out.println( ">>" );
			System.out.println( skm.getClusterCentroids() );
		}

		/* =======================================================
			STANDAR DEVS
		 */
		if( skm.getClusterStandardDevs() != null )
			IOHandler.arff_writer( STD_DEV_PATH, skm.getClusterStandardDevs() );
		else
		{
			System.out.println( "No standar devs found." );
			System.out.println( ">>" );
			System.out.println( skm.getClusterStandardDevs() );
		}
	}
}
