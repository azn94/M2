package datacloud.hadoop.lastfm;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class LastFm3 {
	
	public static class LastFm3Mapper extends Mapper<LongWritable, Text, LongWritable, Text> {

		public void map(LongWritable keyin, Text valuein, Context context) throws IOException, InterruptedException {
			// keyin : offset
			// valuein peut avoir 2 patterns: 
			//			- "trackid nb_listener"
			//			- "trackid nb_listening nb_skip"
			
			String[] line = valuein.toString().split("\\s+");
			int nb_arg = line.length;
			String tmp;
			
			if(nb_arg == 2) {
				tmp = line[1] + " 0 0";
			}else {
				tmp = "0 "+ line[1] + " " + line[2];
			}
			

			Text valueout = new Text(tmp);
			
			context.write(new LongWritable(Integer.parseInt(line[0])), valueout); // keyout = L'id de la musique
			
		}
	}
	
	public static class LastFm3Reducer extends Reducer<LongWritable, Text, LongWritable, Text> {

		public void reduce(LongWritable keyin, Iterable<Text> valuesin, Context context) throws IOException, InterruptedException {
			// keyin : Id de la musique
			// valuesin : Liste des "nb_listener nb_listening nb_skips" 
			
			Integer nb_listener = 0;
			Integer nb_listening = 0;
			Integer nb_skip = 0;
			
			for(Text value : valuesin) {
				String[] cpt = value.toString().split(" ");
				nb_listener += Integer.parseInt(cpt[0]);
				nb_listening += Integer.parseInt(cpt[1]);
				nb_skip += Integer.parseInt(cpt[2]);
			}
			
			context.write(keyin, new Text(nb_listener.toString() + " " + nb_listening.toString() + " " + nb_skip.toString()));
		
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.setBoolean("mapreduce.map.speculative", false);
		conf.setBoolean("mapreduce.reduce.speculative", false);
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(LastFm3.class);// permet d'indiquer le jar qui contient l'ensemble des .class du job à partir d'un nom de classe
		job.setMapperClass(LastFm3Mapper.class);// indique la classe du Mapper
		job.setReducerClass(LastFm3Reducer.class);// indique la classe du Reducer
		job.setMapOutputKeyClass(LongWritable.class);// indique la classe de la clé sortie map
		job.setMapOutputValueClass(Text.class);// indique la classe de la valeur sortie map
		job.setOutputKeyClass(LongWritable.class);// indique la classe de la clé de sortie reduce
		job.setOutputValueClass(Text.class);// indique la classe de la valeur de sortie reduce
		job.setInputFormatClass(TextInputFormat.class);// indique la classe du format des données d'entrée
		job.setOutputFormatClass(TextOutputFormat.class);// indique la classe du format des données de sortie
		//job.setPartitionerClass(LastFm3Partitioner.class);// indique la classe du partitionneur
		job.setNumReduceTasks(1);// nombre de tâche de reduce : il est bien sur possible de changer cette valeur (1 par défaut)

		//FileInputFormat.addInputPath(job, new Path(otherArgs[0]));// indique le ou les chemins HDFS d'entrée
		MultipleInputs.addInputPath(job, new Path(otherArgs[0]), TextInputFormat.class, LastFm3Mapper.class);
		MultipleInputs.addInputPath(job, new Path(otherArgs[1]), TextInputFormat.class, LastFm3Mapper.class);
		final Path outDir = new Path(otherArgs[2]);// indique le chemin du dossier de sortie
		FileOutputFormat.setOutputPath(job, outDir);
		final FileSystem fs = FileSystem.get(conf);// récupération d'une référence sur le système de fichier HDFS
		if (fs.exists(outDir)) {// test si le dossier de sortie existe
			fs.delete(outDir, true);// on efface le dossier existant, sinon le job ne se lance pas
		}
		System.exit(job.waitForCompletion(true) ? 0 : 1);// soumission de l'application à Yarn
	}
}
