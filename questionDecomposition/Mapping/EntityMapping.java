package Mapping;

import java.util.ArrayList;
import utils.StringSimilarity;

public class EntityMapping {
	public String keyWord;
	public int maxNum;
	public DBpediaLookup dbplook=null;
	public StringSimilarity stringSim=null;
	public EntityMapping() {
		dbplook = new DBpediaLookup();
		stringSim=new StringSimilarity();
	}
	public ArrayList<String> keyWordSearchByDbpediaLookup(String keyWord) {
		//String uri="http://lookup.dbpedia.org/api/search/KeywordSearch?MaxHits="+maxNum+"&QueryString="+keyWord;
		ArrayList<String > Entities=dbplook.lookForEntityNames(keyWord);
		for(String entity:Entities) {
			System.out.println(entity);
		}
		return Entities;
	}
	public boolean mayEntity(String keyWord) {
		ArrayList<String > Entities=dbplook.lookForEntityNames(keyWord);
		if(stringSim.minEditDistance(keyWord, Entities.get(0))<3)
			return true;
		return false;
	}
	public static void main(String []args) {
		EntityMapping  em=new EntityMapping();
		em.keyWordSearchByDbpediaLookup("river");
		System.out.println(em.mayEntity("river"));
	}
}
