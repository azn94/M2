package projet.util.Etude2;

import java.util.HashMap;
import java.util.List;

import projet.util.Etude2.Message2.*;

public class Historique {
	private static HashMap<Integer, List<Request>> historique = new HashMap<>();
	
	public Historique() {}
	
	public static void addHistorique(int id , List<Request> list) { historique.put(id, list); }
	public static List<Request> getHistorique(int id) { return historique.get(id); }
	
	public static void setHistorique(int id , List<Request> list) { historique.replace(id,list); }
	public static HashMap<Integer, List<Request>> getHistorique(){ return historique; }
}