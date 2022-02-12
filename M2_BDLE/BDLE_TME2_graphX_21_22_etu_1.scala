// Databricks notebook source
// MAGIC %md
// MAGIC # BDLE : GraphX

// COMMAND ----------

// MAGIC %md
// MAGIC ### Description des données synthétiques modélisant un réseau social
// MAGIC - Fichier `facebook_edges_prop.csv` : contient sur chaque ligne les informations suivantes pour un arc:  
// MAGIC    `source, destination, type_relation, nombre_messages_échangés`
// MAGIC    
// MAGIC - Fichier `facebook_users_prop.csv` : contient les informations suivantes pour chaque utilisateur:
// MAGIC    `utilisateur, prénom, nom, âge`

// COMMAND ----------

// MAGIC %md
// MAGIC ## Lecture des données 
// MAGIC 
// MAGIC 
// MAGIC Télécharger les fichiers `facebook_edges_prop.csv` et `facebook_users_prop.csv` à partir de cette adresse: 
// MAGIC *  https://nuage.lip6.fr/s/wZrEiYR4YjQZ9is
// MAGIC 
// MAGIC puis charger ces deux fichiers dans le répertoire `/FileStore/tables/BDLE/TMEGraphes2/` de Databricks.

// COMMAND ----------

// MAGIC %md
// MAGIC ## Initialisation

// COMMAND ----------

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
sc.setLogLevel("ERROR")

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 1: Créer le graphe d'utilisateurs
// MAGIC Construire un graphe dirigé (voir la section “Graph Builders” dans la documentation GraphX) à partir des données de cet exercice tel que :
// MAGIC 
// MAGIC - chaque sommet est identifié par l'attribut utilisateur et ayant pour propriétés les attributs prénom, nom et âge de facebook_users_prop.csv.
// MAGIC - chaque arête reliant deux sommets a pour propriétés les attributs type_relation et nombre_messages_échanés de facebook_edges_prop.csv.
// MAGIC 
// MAGIC Le type de la structure Spark qui stocke les sommets est le suivant:
// MAGIC 
// MAGIC - org.apache.spark.rdd.RDD[(org.apache.spark.graphx.VertexId, (String, String, Int))]
// MAGIC 
// MAGIC Le type de la structure Spark qui stocke les arêtes est le suivant:
// MAGIC 
// MAGIC - org.apache.spark.rdd.RDD[org.apache.spark.graphx.Edge[(String, Int)]]
// MAGIC 
// MAGIC Le type de la structure Spark qui stocke le graphe est le suivant:
// MAGIC 
// MAGIC - org.apache.spark.graphx.Graph[(String, String, Int),(String, Int)]

// COMMAND ----------

val DATASET_DIR="/FileStore/tables/BDLE/TMEGraphes2/"
var file = "facebook_edges_prop.csv"
var lines = sc.textFile(DATASET_DIR+file, 4) 
 
val edgesList:RDD[Edge[(String, Int)]] = lines.map{s=>
    val parts = s.split(",")
    Edge(parts(0).toLong, parts(1).toLong, (parts(2), parts(3).toInt))
}.distinct()
 
file = "facebook_users_prop.csv"
lines = sc.textFile(DATASET_DIR+file, 4) 
 
val vertexList:RDD[(VertexId,(String, String, Int))] = lines.map{s=>
    val parts = s.split(",")
    (parts(0).toLong, (parts(1), parts(2), parts(3).toInt))
}.distinct()
val graph = Graph.apply(vertexList, edgesList)

// COMMAND ----------

graph.vertices.collect.foreach(x => println(x))

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 2
// MAGIC 
// MAGIC Afficher pour l'utilisateur dont le prénom est 'Kendall' son identifiant, son nom et son âge (utiliser la vue graph.vertices).

// COMMAND ----------

graph.vertices.filter{case(id, (prenom, nom, age)) => prenom == "Kendall"}.collect.foreach(x => println(x._1 + " " + x._2._2 + " " + x._2._3))


// COMMAND ----------

// MAGIC %md
// MAGIC Réponse attendue: 2058 Brewbaker 49

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 3
// MAGIC Afficher les prénoms des amis de “Kendall” (utiliser la vue graph.triplets). On considère le graphe non-dirigé.

// COMMAND ----------

graph.triplets.filter{triplet => (triplet.attr._1 == "friend") && ((triplet.srcAttr._1 == "Kendall") || (triplet.dstAttr._1 == "Kendall"))}.map(triplet => if(triplet.srcAttr._1 == "Kendall") {triplet.dstAttr._1} else {triplet.srcAttr._1}).collect.foreach( x => println(x))


// COMMAND ----------

/*
Réponse attendue:
Veda
Hilbert
Darl
Toccara
Carroll
Tammi
Shenna
...
True
Wanita
Alfredo
Donnie
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 4
// MAGIC 
// MAGIC Afficher les identifiants des utilisateurs qui ont désigné “Kendall” comme collègue et qui ont échangé plus de 70 messages avec lui (utiliser la vue graph.triplets).

// COMMAND ----------

graph.triplets.filter{triplet => ((triplet.srcAttr._1 == "Kendall") || (triplet.dstAttr._1 == "Kendall"))}.collect.foreach( x => println(x))

// COMMAND ----------

graph.triplets.filter{triplet => if ((triplet.srcAttr._1 == "Kendall" || triplet.dstAttr._1 == "Kendall") && (triplet.attr._1 == "colleague") && (triplet.attr._2 > 70))  {true} else {false}}.map(triplet => if(triplet.srcAttr._1 == "Kendall") {triplet.dstId} else {triplet.srcId}).distinct().collect().take(10).foreach(x=>println(x))


// COMMAND ----------

/*Réponse attendue: 1966, 1983, 1941*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 5
// MAGIC Afficher l'identifiant de l'utilisateur qui a désigné le plus d'utilisateurs comme amis et avec lesquels il a échangé plus de 80 messages. Afficher également le nombre de ces amis (vous pouvez utiliser la méthode Array.maxBy).

// COMMAND ----------

graph.edges.collect.foreach(x => println(x))

// COMMAND ----------

graph.edges.filter{case Edge(src, dst, prop) => (prop._1 == "friend") && (prop._2 > 80)}.map(e => (e.srcId, 1)).reduceByKey(_ + _).collect.maxBy(_._2)


// COMMAND ----------

/*
Réponse attendue:
res4: (org.apache.spark.graphx.VertexId, Int) = (107,59)
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 6
// MAGIC 
// MAGIC Afficher le nom, le prénom et l'âge des voisins de “Kendall”, en considérant que le graphe est non-dirigé (utiliser la vue graph.triplets) suivant les trois manières suivantes :
// MAGIC 
// MAGIC - En utilisant graph.triplets seulement
// MAGIC - En utilisant collectNeighbors
// MAGIC - En utilisants aggregateMessages

// COMMAND ----------

/*
Résultat attendu: 76 résultats
Veda Kustra 40
Hilbert Coakley 25
Darl Borr 42
Toccara Fahlsing 45
Carroll Foronda 37
Tammi Kuske 30
Shenna Blasing 26
Vivian Ugolini 45
Norwood Kirwan 29
Izabella Macrostie 47
Effie Andujo 41
Kamren Gesualdo 43
...
True Ficke 42
Wanita Purslow 32
Alfredo Izard 20
Donnie Lengyel 41*/

// COMMAND ----------

//1. En utilisant graph.triplets seulement
graph.triplets.filter{triplet => ((triplet.srcAttr._1 == "Kendall") || (triplet.dstAttr._1 == "Kendall"))}.map(triplet => (triplet.srcAttr, triplet.dstAttr)).collect.foreach{case ((p,n,a), (p1,n1,a1)) => if(p=="Kendall") println(p1+" "+n1+" "+a1) else println(p+" "+n+" "+a)}


// COMMAND ----------

//2. En utilisant collectNeighbors
graph.collectNeighbors(EdgeDirection.Either).innerJoin(graph.vertices.filter{v => v._2._1 == "Kendall"})
{case (id, list, attrs) => list}.values.collect.foreach(x => x.foreach(x => println(x._2._1+" "+x._2._2+" "+x._2._3)))


// COMMAND ----------

//3. En utilisants aggregateMessages

graph.aggregateMessages[Array[String]] (
triplet => {
if(triplet.dstAttr._1 == "Kendall")
triplet.sendToDst(Array(triplet.srcAttr._1+" "+triplet.srcAttr._2+" "+triplet.srcAttr._3))
if(triplet.srcAttr._1 == "Kendall")
triplet.sendToSrc(Array(triplet.dstAttr._1+" "+triplet.dstAttr._2+" "+triplet.dstAttr._3))
},
(a,b) => (a ++ b)
).values.collect.foreach(x =>x.foreach(println(_)))


// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 7
// MAGIC Afficher le nombre de liens entrants minimum du graphe. Afficher les noms et les id des utilisateurs qui ont le nombre minimum de liens entrants, les utilisateurs sans liens entrants ne seront pas affichés (utiliser graph.inDegrees).

// COMMAND ----------

def min(a: Int, b: Int): Int = {
  if (a < b) a else b
}

// COMMAND ----------

var minDeg = graph.inDegrees.values.reduce(min)
graph.inDegrees.filter(x => x._2 == minDeg).innerJoin(graph.vertices){case (id, d, u) => (u)}.
collect.foreach(x => println(x._1+" Name "+x._2._1))

// COMMAND ----------

/*
Résultat attendu:
956 Name  Geri
160 Name  Adelle
2664 Name  Chantal
2712 Name  Leandra
1176 Name  Courtney
2732 Name  Felix
......
3459 Name  Hetty
951 Name  Donnell
687 Name  Bunk
47 Name  Arline
3503 Name  Annamae
minDeg: Int = 1*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 8
// MAGIC Afficher les noms des utilisateurs qui n'ont pas de liens entrants (utiliser inDegrees et outerJoinVertices).

// COMMAND ----------

graph.outerJoinVertices(graph.inDegrees){
case (id, d, u) => (d, u.getOrElse(0))
}.vertices.filter(x => x._2._2 == 0).collect.foreach(x=>println(x._2._1._1+" indegree "+x._2._2))


// COMMAND ----------

/*
Résultat attendu:
Dalvin indegree  0
Floy indegree  0
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 9
// MAGIC Afficher les noms des utilisateurs dont le nombre de liens entrants, ainsi que le nombre de de liens sortants est 5. Affichez également le nombre de liens entrants pour chaque utilisateur obtenu.

// COMMAND ----------

graph.outerJoinVertices(graph.inDegrees){
case (id, prop, opt) =>  (prop, opt.getOrElse(0))
}.outerJoinVertices(graph.outDegrees){
case (id, prop, opt) => (prop, opt.getOrElse(0))
}.vertices.filter(x => (x._2._1._2 == 5) && (x._2._2 == 5)).collect.foreach(x=>println(x._2._1._1._1+" "+x._2._2))


// COMMAND ----------

/*
Résultat attendu:
Pauline 5
Rae 5
Lewis 5
Cyril 5
Billie 5
Arely 5
Krish 5
Hessie 5
Masao 5
Archibald 5
Rosemary 5
Harlow 5
General 5
Letitia 5
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 10
// MAGIC Pour les utilisateurs Dalvin, Kendall et Elena affichez les prénoms et l'âge des utilisateurs les plus âgés parmi les utilisateurs qui les ont désigné comme amis. Si un utilisateur parmi les trois n'a pas de liens entrants le programme doit afficher un message.

// COMMAND ----------

val oldest = graph.aggregateMessages[(String, Int)](
triplet => { // Map Function
  if (triplet.attr._1 == "friend"){
  // Send message to destination vertex containing counter and age
  triplet.sendToDst(...)
  }
},
(a,b) => ...
)//.collect.foreach(println)
graph.vertices.filter{...}.leftJoin(oldest)
{ (id, user, optOldestFollower) =>
optOldestFollower match {
case None => 
case Some((name, age)) =>
}
}.collect.foreach { }

// COMMAND ----------

/*
Résultat attendu:
Dalvin does not have any followers.
Jonas aged 41 is the oldest follower of Kendall.
River aged 22 is the oldest follower of Elena. */

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 11
// MAGIC Afficher les identifiants des utilisateurs qui ont désigné Kendall et Lilia comme étant leurs amis (amis communs de Kendall et de Lilia).

// COMMAND ----------

val com = graph.aggregateMessages[Int] (
triplet =>{
if(...)
...
},
(a,b) =>...
)
com.filter{...}.collect.foreach(u=>println(u._1))


// COMMAND ----------

/*
Résultat attendu:
2020
1943
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 12
// MAGIC Pour chaque utilisateur, trouver l'âge moyen de ses amis (on considère que le graphe est non-dirigé). Utiliser la méthode aggregateMessages.
// MAGIC 
// MAGIC Rappel Le graphe étant dirigé, il faudra s'assurer que tous les sommets envoient des messages à leurs successeurs ainsi qu'à leurs prédécesseurs.

// COMMAND ----------

val ages = graph.aggregateMessages[(Int, Double)]( //on obtient un nouveau graphe (ages)
triplet => { // Map Function
....
},
(a, b) => ...
)
ages.mapValues(...).collect.foreach(println(_))


// COMMAND ----------

/*
Résultat attendu:
(1084,29.466666666666665)
(3764,36.55172413793103)
(3456,34.627450980392155)
(772,33.77777777777778)
(3272,34.56818181818182)
(752,36.13157894736842)
(1724,36.23728813559322)
(428,35.208695652173915)
(1900,36.65384615384615)
(1328,32.9)
(464,35.8)
....
(3203,36.0)
(2967,35.13636363636363)
(155,37.666666666666664)
(147,34.5)
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 13
// MAGIC Calculer pour chaque noeud la liste des différentes types de relations sur ses arcs sortants. L'attribut d'un noeud contenant le type de ses liens sortants sera de type Set[String]. Construire ensuite un nouveau graphe où chaque noeud a comme attributs son prénom et la liste des types de ses arcs sortants, ou une liste vide s'il n'a pas d'arcs sortants (utiliser outerJoinVertices). Afficher la liste des types des arcs sortants pour l'utilisateur Elena dans ce nouveau graphe.

// COMMAND ----------

val tarcs = graph.aggregateMessages[Set[String]](
triplet => {
...
},
(a,b) => ...
)
graph.outerJoinVertices(tarcs){... =>
... match{
case Some(typeOpt) => ...
case None => ...
}
}.vertices.filter{case ...}.collect.foreach(println(_))


// COMMAND ----------

/*
Résultat attendu:
(3647,(Elena,Set(acquaintance, friend)))
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 14
// MAGIC Affichez les prénoms des amis des amis de Deana (à distance 2 de Deana), pour un graphe dirigé sans utiliser la fonction pregel (suggestion: utilisez graph.aggregateMessages et graph.outerJoinVertices).

// COMMAND ----------

val neighs = graph.aggregateMessages[Boolean] (
triplet => {
if(...)
...
},
(a,b) =>...
)
graph.outerJoinVertices(neighs){...}.triplets.filter{...}.
map(...).collect.foreach(println(_))


// COMMAND ----------

/*
Résultat attendu:
Sharita
Darrick
Idabelle
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 15
// MAGIC Affichez les prénoms des amis des amis de Deana (à distance 2 de Deana), pour un graphe dirigé en utilisant la fonction pregel. Vous pouvez prendre comme point de départ l'implantation de l'algorithme des plus courts chemins dans la section “Pregel API”.

// COMMAND ----------

val initialGraph = graph.mapVertices( (id, attr) => if (attr._1 == "Deana") (0.0, attr._1) else
(Double.NegativeInfinity, attr._1))
val nn = initialGraph.pregel(...)(
... => (Math.max(...), ...),
triplet => {
if(...)
Iterator(...)
else Iterator.empty
},
(a,b) => ...
)
nn.vertices.filter{... }.collect.foreach(println(_))


// COMMAND ----------

/*
Résultat attendu:
(2646,(2.0,Idabelle))
(2631,(2.0,Darrick))
(2655,(2.0,Sharita))
*/

// COMMAND ----------

// MAGIC %md
// MAGIC #### Exercice 16
// MAGIC Calculez le sous-graphe contenant seulement l'utilisateur Deana et ses amis à une distance inférieure ou égale à 2. Affichez chaque noeud et chaque arc du graphe.

// COMMAND ----------

nn.subgraph(epred = ..., vpred=... => ...).vertices.collect.foreach(println(_)) 

// COMMAND ----------

/*
Résultat attendu:
(2624,(0.0,Deana))
(2625,(1.0,Ethel))
(2646,(2.0,Idabelle))
(2631,(2.0,Darrick))
(2655,(2.0,Sharita))
*/
