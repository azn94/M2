package datagenerator.twit

import datagenerator.Generator
import scala.collection.mutable.ArrayBuffer
import java.util.Scanner
import scala.util.Random
import datagenerator.Configuration


class TwitGenerator(config:Configuration) extends Generator {
  
  
  
  val sizetop =config.getMandatoryProperty("twit.sizetop", "entier qui defini la taille du top").toInt
   
  val nbhashtag=config.getMandatoryProperty("twit.nbhashtag", "nombre de hashtage existants").toInt
  
  
  val nbword=config.getMandatoryProperty("twit.nbword", "nombre de mot existants").toInt
  
    
  var seed = config.getMandatoryProperty("twit.seed", "valeur de graine initiale").toInt

  val facteur_ecart = config.getMandatoryProperty("twit.facteur_ecart",  "plus ce nombre est elevé, plus on aura des chance de respecter le top x attendu").toInt
     
  val periode_pause_max = config.getMandatoryProperty("twit.periode_pause_max", "temps en ms max a attendre entre chaque twit généré, plus ce nombre est faible , plus la fréquence de génération est élevée").toInt
  
  val taille_max_twit = config.getMandatoryProperty("twit.taille_max_twit", "nombre d'éléments max (word + hashtag) dans un twit").toInt
  
  val pourcentage_max_hashtag_dans_twit = config.getMandatoryProperty("twit.pourcentage_max_hashtag_dans_twit", "pourcentage max de hastag dans un twit").toInt
    
  val stringword="word"
  val stringhashtag="#twit"
  
  val id_top_ten=new Array[Int](sizetop)
   
  
  def generateTwit = {
    val r= new Random(seed)
    seed+=1
    System.err.println("Top "+sizetop+" potentiel :")
    for(i<-0 until sizetop by 1){
      var tmp = r.nextInt(nbhashtag)
   
      while(id_top_ten.contains(tmp)){
        tmp+=1
      }
      id_top_ten(i)=tmp; 
      System.err.println(i+" : "+stringhashtag+id_top_ten(i))
    }
    var hashtags = r.shuffle(makeListHashtags(r))
    var words = r.shuffle(makeListWords(r))
    
    while(true){
      var size_sentence = r.nextInt(taille_max_twit)+1
    
      var nb_hashtag_max = (size_sentence*pourcentage_max_hashtag_dans_twit)/100
      var nb_hashtag=0;
      if(nb_hashtag_max != 0){
        nb_hashtag =r.nextInt(nb_hashtag_max)
      }
      
      var nb_word = size_sentence - nb_hashtag;
      
      var words_sentences = new ArrayBuffer[String] 
      for(i<- 0 until nb_hashtag){
        var id_case_twit = r.nextInt(hashtags.length)
        words_sentences.+=(hashtags(id_case_twit))
      }
      for(i<- 0 until nb_word){
        var id_case_word = r.nextInt(words.length)
        words_sentences.+=(words(id_case_word))
      }
      words_sentences=r.shuffle(words_sentences)
      
      val sb = new StringBuffer;
      for(s<- words_sentences){
        sb.append(s+" ")
      }
      println(sb.toString())
      try{
        Thread.sleep(r.nextInt(periode_pause_max))
      }catch{
        case e:Exception => {}
      } 
     
    }
  }
  
  
  
  
  def makeListHashtags(r:Random):ArrayBuffer[String] = {
    var listhashtag = new ArrayBuffer[String]
    for(i<- 0 until nbhashtag){
      listhashtag +=(stringhashtag+i)
    }
    var listhashtagsuite = new ArrayBuffer[String]
    for(i <- 0 until sizetop){
      var limit = ((sizetop - i) * nbhashtag*facteur_ecart )/100
      for(cpt <- 0 until limit ){
        listhashtagsuite.+=(stringhashtag+id_top_ten(i))
      }
    }
    listhashtag.++=(listhashtagsuite)
    listhashtag=r.shuffle(listhashtag)
    return listhashtag
  }
    
  def makeListWords(r:Random):ArrayBuffer[String] = {
    var listword = new ArrayBuffer[String]
    for(i<- 0 until nbword){
      listword +=(stringword+i)
    }
    return listword
  }
  
  def generate={
    val sc = new Scanner(System.in);
    val task = new Runnable {
        def run() {
          generateTwit
        }
      }
    
    while(true){
      var thread = new Thread(task)
      System.err.println("Appuyer sur entrée pour générer un nouveau top "+sizetop+" potentiel")
      thread.start()
      sc.nextLine()
      System.err.println("Génération d'un top "+sizetop+" potentiel")
      thread.interrupt()
    }
  }
  
}