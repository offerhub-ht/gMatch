package Extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import Mapping.EntityMapping;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
public class EntityExtract {
	public StanfordCoreNLP pipeline=null;
	public EntityMapping EM=null;
	public EntityExtract() {
		Properties props = new Properties();
		props.setProperty("annotators","tokenize, ssplit, pos");
		pipeline = new StanfordCoreNLP(props);
		EM=new EntityMapping();
	}
	
	public ArrayList<String> getCandiditeEntity(String question){
		//词性
		ArrayList<String> words=new ArrayList<String>();
		HashMap<String, String> map = new HashMap<String, String>();
		Annotation annotation = new Annotation(question);
		pipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
		    for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
		        String word = token.get(CoreAnnotations.TextAnnotation.class);
		        // this is the POS tag of the token
		        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		        words.add(word);
		        map.put(word, pos);
		    }
		}
		ArrayList<String> res=new ArrayList<String>();
		String specialWord=null;
		for(String word:words) {
			String pos=map.get(word);
			if(pos.startsWith("N") || pos.startsWith("W")|| pos.startsWith("P")) {
				if(pos.startsWith("W")) {
					specialWord=word;
				}
				res.add(word);
			}
			else {
				res.add("@");
			}
		}
		//
		ArrayList<String> result=new ArrayList<String>();
		for(int i=0;i<res.size();i++) {
			String temp="";
			if(res.get(i)!="@") {
				if(i+1<res.size()&&res.get(i+1)!="@") {
					temp=res.get(i)+" "+res.get(i+1);
					//i++;
				}
				else {
					temp=res.get(i);
				}
			}
			if(temp.equals("")) {
				temp="@";
			}
			result.add(temp);
		}
		
		ArrayList<String> CandiditeEntities=new ArrayList<String>();
		for(String word:result) {
			if(!word.equals("@")) {
				if(specialWord!=null&&word.contains(specialWord)&&word.contains("_")){
					word=word.replace(specialWord, "");
					word=word.replace("_", "");
				}
				CandiditeEntities.add(word);
				System.out.println(word);
			}
		}
		return CandiditeEntities;
	}
	public static void main(String []args) throws IOException {
		EntityExtract ee=new EntityExtract();
        // 从键盘接收数据
 
        // next方式接收字符串
        System.out.println("next方式接收：");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // 判断是否还有输入
        while(true) {
        	String str1 = br.readLine();
            ArrayList<String> candidite=ee.getCandiditeEntity(str1);
            for(String word:candidite) {
            	ArrayList<String> entity=ee.EM.keyWordSearchByDbpediaLookup(word);
            	System.out.println(word);
            	for(String w:entity) {
            		System.out.println(w);
            	}
            }
        }
	}
}
