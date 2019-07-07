package wekaTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Purge {

	private Instances inst_set;
	private double tolerance;

	Purge()
	{
		this.inst_set = null;
		this.tolerance = 0.0;
	}

	Purge(String dSPath)
	{
		this.inst_set = IOHandler.load( dSPath );
		this.tolerance = 5.0;
	}

	public void set_instSet(Instances inst){ this.inst_set = inst; }

	public Instances get_instSet(){ return this.inst_set; }

	public void set_tolerance(double tolerance){ this.tolerance = tolerance; }

	public Instances remove_attr(int[] attr_indexes)
	{
		//1_Setup filter
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = Arrays.toString( attr_indexes ).replace(" ", "")  //remove the spaces
			    		.replace("[", "")  //remove the right bracket
			    		.replace("]", "")  //remove the left bracket
			    		.trim();           //remove trailing spaces from partially initialized arrays
		Remove remove = new Remove(); //Filter
		try
		{
			remove.setOptions(options);
			remove.setInputFormat( this.inst_set );
		}
		catch(Exception e){System.out.println(e);}
		//0_Setup filter
		//--------------------------------------------
		//1_Apply filter
		Instances newInst_set = null;
		try{ newInst_set = Filter.useFilter( this.inst_set, remove ); }
		catch(Exception e){System.out.println(e);}
		//0_Apply filter

		return newInst_set;
	}

	@SuppressWarnings("unused")
	private void print_data()
	{
		System.out.println( this.inst_set );
	}

	public void remove_inst(int index){	this.inst_set.delete(index); }

	public Instances pop_subSet( int index )
	{
		Instances newInst_set =  new Instances(this.inst_set, 0);

		for(int i = 0; i < this.inst_set.numInstances(); i++)
			if( Double.isNaN(this.inst_set.get(i).value(index)) )
				newInst_set.add( this.inst_set.get(i) );

		return newInst_set;
	}
}
