package datacloud.hadoop.noodle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Noodle {

	public static class NoodleMapper extends Mapper<LongWritable, Text, Text, Text> {
		
		public void map(LongWritable keyin , Text valuein, Context context) throws IOException, InterruptedException {

			String[] line = valuein.toString().split(" ");
			
			/*
	    	 * line[0] = date de connexion au format JJ_MM_AAAA_HH_MM_SS
	    	 * line[1] = adresse IP du client ayant fait la requête
	    	 * line[2] = mots-clés de la requête
	    	 * */
			
			String keyout;					// Tranche horaire avec le mois
			String valueout = line[2];		// mots-clés de la requête

			String[] date_connexion = line[0].split("_");
			/*
	    	 * date_connexion[0] = JJ
	    	 * date_connexion[1] = MM (mois)
	    	 * date_connexion[2] = AAAA
	    	 * date_connexion[3] = HH
	    	 * date_connexion[4] = MM (minutes)
	    	 * date_connexion[5] = SS
	    	 * */
			
			int minutes = Integer.parseInt(date_connexion[4]);
	    	
	    	if (minutes < 30) {
	    		keyout = "entre "+ date_connexion[3] + "h00 et " + date_connexion[3] + "h29";
	    	}else {
	    		keyout = "entre "+ date_connexion[3] + "h30 et " + date_connexion[3] + "h59";
		    }
			keyout += ", mois " + date_connexion[1];
	    	
	    	context.write(new Text(keyout), new Text(valueout));
		}
	}

	public static class NoodleReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text keyin, Iterable<Text> valuesin, Context context) throws IOException, InterruptedException {

			Map<String, Integer> counters = new HashMap<>();		// Compte tous les mots clés au cours d'un mois
			
			int total_request = 0;									// Nombre total de requêtes
			String most_used_word = "";								// Mot le plus employé
			int word_count = -1;									// Nombre de fois que ce mots ait été employé

			// Pour chaque requête
			for (Text request : valuesin) {
				
				String[] keywords = request.toString().split("\\+");// Liste des mots-clés
				
				
				total_request++;
				// Pour chaque mot-clé
				for (String keyword : keywords) {
					
					// Si le mot est déjà dans la map
					if (counters.containsKey(keyword)) {
						counters.put(keyword, counters.get(keyword) + 1);
						
					// Sinon, on l'ajoute
					} else {
						counters.put(keyword, 1);
					}
				}
			}

			// On cherche le mot le plus employé ce mois-ci
			for (String keyword : counters.keySet()) {
				
				if (counters.get(keyword) > word_count) {
					word_count = counters.get(keyword);
					most_used_word = keyword;
				}
			}

			context.write(keyin, new Text(" <Le mot le plus recherché dans cette tranche horaire : " + most_used_word + " > <Le nombre total de requêtes reçues dans cette tranche horaire : " + total_request));
		}
	}

	public static class NoodlePartitioner extends Partitioner<Text, Text> {

		public int getPartition(Text keyin, Text valuein, int nbPartitions) {
			String[] res = keyin.toString().split(" ");
			// On récupère le dernier élément de la clé : le mois
			return Integer.parseInt(res[res.length-1])%nbPartitions;
		}

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.setBoolean("mapreduce.map.speculative", false);
		conf.setBoolean("mapreduce.reduce.speculative", false);
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(Noodle.class);// permet d'indiquer le jar qui contient l'ensemble des .class du job à partir d'un nom de classe
		job.setMapperClass(NoodleMapper.class);// indique la classe du Mapper
		job.setReducerClass(NoodleReducer.class);// indique la classe du Reducer
		job.setMapOutputKeyClass(Text.class);// indique la classe de la clé sortie map
		job.setMapOutputValueClass(Text.class);// indique la classe de la valeur sortie map
		job.setOutputKeyClass(Text.class);// indique la classe de la clé de sortie reduce
		job.setOutputValueClass(Text.class);// indique la classe de la valeur de sortie reduce
		job.setInputFormatClass(TextInputFormat.class);// indique la classe du format des données d'entrée
		job.setOutputFormatClass(TextOutputFormat.class);// indique la classe du format des données de sortie
		job.setPartitionerClass(NoodlePartitioner.class);// indique la classe du partitionneur
		job.setNumReduceTasks(12);// nombre de tâche de reduce : il est bien sur possible de changer cette valeur (1 par défaut)

		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));// indique le ou les chemins HDFS d'entrée
		final Path outDir = new Path(otherArgs[1]);// indique le chemin du dossier de sortie
		FileOutputFormat.setOutputPath(job, outDir);
		final FileSystem fs = FileSystem.get(conf);// récupération d'une référence sur le système de fichier HDFS
		if (fs.exists(outDir)) {// test si le dossier de sortie existe
			fs.delete(outDir, true);// on efface le dossier existant, sinon le job ne se lance pas
		}
		System.exit(job.waitForCompletion(true) ? 0 : 1);// soumission de l'application à Yarn
	}
}