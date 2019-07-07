package wekaTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;

public class IOHandler {

  public static void arff_writer( String filename, Instances inst )
  {
    BufferedWriter writer = null;
    try
    {
      writer = new BufferedWriter(
                    new FileWriter( filename ));
      writer.write( inst.toString() );
      writer.newLine();
      writer.flush();	//Ensure that all has been written
      writer.close();
    }catch(IOException e){ System.out.println(e); }
    finally{ try { writer.close(); } catch (Exception ex) {/*ignore*/} }
  }

  public static Instances load( String file_path )
	{
		BufferedReader reader = null;
		Instances data = null;

		try
		{
			reader = new BufferedReader( new FileReader( file_path ) ); //Creates an input buffer for the source file
			data = new Instances( reader  ); //Reads the data from the source file and convert it into Instances
			data.setClassIndex( data.numAttributes() - 1 ); // Make the last attribute be the class
			reader.close(); //Close buffer
		}
		catch(Exception e){System.out.println( e );}
		finally{ try { reader.close(); } catch (Exception ex) {/*ignore*/} }

		return data;
	}

  public static Instances merge_inst(Instances a, Instances b)
  {
    Instances newInst_set = new Instances(a);
    newInst_set.addAll(b);
    return newInst_set;
  }
}
