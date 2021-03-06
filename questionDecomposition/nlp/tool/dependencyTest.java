package nlp.tool;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import java.util.*;


public class dependencyTest {

  public static String text = "who won nopel prize for physics and was born in ulm?";

  public static void main(String[] args) {
    // set up pipeline properties
    // examples
	 Properties props = new Properties();
     props.setProperty("annotators", "tokenize, ssplit, pos, parse,depparse");
     StanfordCoreNLP pipeline = new StanfordCoreNLP(props); 
     Annotation document = new Annotation(text);
     pipeline.annotate(document);
     CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
     SemanticGraph dependency_graph = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
     System.out.println("\n\nDependency Graph: " + dependency_graph.toString(SemanticGraph.OutputFormat.LIST));
     
     
     
//    LexicalizedParser lp=LexicalizedParser.loadModel();
//    Tree t=lp.parse(text);
//    EnglishGrammaticalStructure gs=new EnglishGrammaticalStructure(t);
//    Collection<TypedDependency> tdl=gs.typedDependenciesCollapsed();
//    System.out.println(tdl.toString());
//    
     

  }

}