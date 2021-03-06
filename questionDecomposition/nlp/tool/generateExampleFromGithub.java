package nlp.tool;
import edu.stanford.nlp.pipeline.*;
import java.util.*;
import java.util.stream.Collectors;
public class generateExampleFromGithub {
	public static String text = "who is the tallest actor ";
	public static void main(String[] args) {
//		Properties props = new Properties();
//		props.setProperty("annotators", "tokenize,ssplit,pos");
//		props.setProperty("coref.algorithm", "neural");
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//		CoreDocument document = new CoreDocument(text);
//		pipeline.annotate(document);
//		
//		System.out.println("---");
//	    System.out.println("entities found");
//	    for (CoreEntityMention em : document.entityMentions())
//	      System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
//	    System.out.println("---");
//	    System.out.println("tokens and ner tags");
//	    String tokensAndNERTags = document.tokens().stream().map(token -> "("+token.word()+","+token.ner()+")").collect(
//	        Collectors.joining(" "));
//	    System.out.println(tokensAndNERTags);
//	    
//		//10th token of the document
//		CoreLabel token = document.tokens().get(0);
//	    System.out.println("Example: token");
//	    System.out.println(token);
//	    System.out.println();
//	    
//	    // text of the first sentence
//	    String sentenceText = document.sentences().get(0).text();
//	    System.out.println("Example: sentence");
//	    System.out.println(sentenceText);
//	    System.out.println();
//	    
//	  // list of the part-of-speech tags for the second sentence
//	    CoreSentence sentence = document.sentences().get(0);
//	    List<String> posTags = sentence.posTags();
//	    System.out.println("Example: pos tags");
//	    System.out.println(posTags);
//	    System.out.println();
	    
	}
}
