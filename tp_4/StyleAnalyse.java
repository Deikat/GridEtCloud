import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class StyleAnalyse {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    
    // Creation des deux cles resultats
    private Text Max = new Text();
    private Text Mean = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {

	  // Separation du texte en phrases
	  StringTokenizer itr = new StringTokenizer(value.toString(),".");
	  
	  while (itr.hasMoreTokens()) {
		// Phrase courante
		String phrase = itr.nextToken();
		
		// Initialisation des textes des clés
		Max.set("Longueur Max");
		Mean.set("Longueur Moyenne");
		
		// Comptage du nombre de mots en coupant la phrase courante par les espaces
		IntWritable nbWords = new IntWritable(phrase.toString().trim().split(" ").length);
		
		// Ecriture des valeurs et des cles
		context.write(Max, nbWords);
		context.write(Mean, nbWords);
	  }
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();
    

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
						   
	  // Cas ou la cle est celle du max
	  if(key.toString().equals("Longueur Max")){
		  int max = 0;
		  
		  // Recherche du max parmi toutes les valeurs
		  for (IntWritable val : values) {
			  max = Math.max(val.get(),max);
		  }
		  // Ecriture du max dans le contexte
		  result.set(max);
		  context.write(key, result);
	  }
	  // Cas ou la cle est celle de la moyenne
	  else{
		  int nbSentences= 0;
		  int nbMots = 0;
		  
		  // Calcul du nombre de mots et du nombre de phrases
		  for (IntWritable val : values) {
			  nbMots += val.get();
			  nbSentences++;
		  }
		  
		  // Calcul de la moyenne et son ecriture dans le contexte
		  result.set(nbMots/nbSentences);
		  context.write(key, result);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "style analyse");
    job.setJarByClass(StyleAnalyse.class);
    job.setMapperClass(TokenizerMapper.class);
//    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

