package nlp.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import utils.FileUtil;

public class GenerateBasicParserTree {
	public Properties props;
	public StanfordCoreNLP pipeline;
	public DependencyTreeNode root;
	public DependencyTree basicDT;
	HashMap<String,String> word2pos;
	ArrayList<ArrayList<String>> triples;
	HashMap<String,Integer> word2index;
	public GenerateBasicParserTree() {
		props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,  pos,depparse");
	    //props.setProperty("coref.algorithm", "neural");
	    //pipeline = new StanfordCoreNLP(props);
	    pipeline = new StanfordCoreNLP(
	    		PropertiesUtils.asProperties(
	    			"annotators", "tokenize,ssplit,pos,parse",
	    			"ssplit.isOneSentence", "true",
	    			"coref.algorithm", "neural",
	    			"parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz",
	    			"tokenize.language", "en"));
	}
	public int generateTriples(String question,DependencyTree basicDT){
		this.basicDT=basicDT;
		question=question.toLowerCase();
		question=question.replace("?","");
		question=question.replace("'s","");
		triples=new ArrayList<ArrayList<String>>();
		Annotation document = new Annotation(question);
		CoreDocument doc = new CoreDocument(question);
		pipeline.annotate(document);//to generate parser
		pipeline.annotate(doc);//to generate posTag
	    
		CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
	    CoreSentence sentencePos = doc.sentences().get(0);
	    
	    //Map a word to posTag
	    List<String> posTags = sentencePos.posTags();
	    String[] words=sentencePos.text().split(" ");
	    word2index=new HashMap<String,Integer>();
	    word2pos=new HashMap<String,String>();
	    for(int i=0;i<words.length;i++) {
	    	word2pos.put(words[i].trim(), posTags.get(i));
	    	word2index.put(words[i].trim(), i);
	    }
	    //generate dependency triples
	    SemanticGraph dependency_graph = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
	    String allTriples= dependency_graph.toString(SemanticGraph.OutputFormat.LIST);
	    System.out.println(allTriples);
	    String[] tripleList=allTriples.split("\n");
	    for(String triple : tripleList) {
	    	triple=triple.replace("(", ",");
	    	triple=triple.replace(")", "");
	    	String [] threeEle=triple.split(",");
	    	ArrayList<String> eles=new ArrayList<String>();
	    	for(String ele:threeEle) {
	    		if(!ele.contains("-")) {
	    			eles.add(ele);
	    		}
	    		else {
	    			String [] tempEles=ele.split("-");
	    			eles.add(tempEles[0]);
	    		}
	    	}
	    	triples.add(eles);
	    }
	    return words.length;
	}
	public void constructBTError() {//错误的原因是只想通过一次遍历就获得所有结点的父子关系，但不太可能，双层遍历吧。
		for(ArrayList<String> triple:triples) {
			if(triple.size()==3) {
				String dep=triple.get(0).trim();
				String father=triple.get(1).trim();
				String son=triple.get(2).trim();
				System.out.println(dep+" "+father+" "+son);
				if(dep.contains("root")) {
					Word w=new Word(son,son,son,word2index.get(son));
					w.posTag=father;
					basicDT.setRoot(w);
					root=basicDT.setRoot(w);
					basicDT.word2node.put(son, root);
					basicDT.nodesList.add(root);
				}
				else {//非root结点
					if(basicDT.word2node.containsKey(father)) {//father存在
						DependencyTreeNode curNode= basicDT.word2node.get(father);
						if(basicDT.word2node.containsKey(son)) {//father存在并且son存在，这时候只构造父子关系
							basicDT.word2node.get(son).dep_father2child=dep;
							basicDT.word2node.get(son).father=curNode;
							curNode.childrenList.add(basicDT.word2node.get(son));
						}
						else {//father存在但是son不存在
							Word tempW=new Word(son,son,son,word2index.get(son));
	        				tempW.posTag=word2pos.get(son);
	        				DependencyTreeNode tempNode=basicDT.insert(curNode, tempW, dep);
            				basicDT.word2node.put(son,tempNode);
            				basicDT.nodesList.add(tempNode);
						}
						
					}
					else {//father不存在
						Word tempW=new Word(father,father,father,word2index.get(father));
        				tempW.posTag=word2pos.get(father);
        				DependencyTreeNode curNode=new DependencyTreeNode(tempW);
        				if(basicDT.word2node.containsKey(son)) {
        					curNode.childrenList.add(basicDT.word2node.get(son));
        					basicDT.word2node.get(son).dep_father2child=dep;
        					basicDT.word2node.get(son).father=curNode;
        					basicDT.word2node.put(father,curNode);
        					basicDT.nodesList.add(basicDT.word2node.get(father));
        				}
        				else {
        					Word sonW=new Word(son,son,son,word2index.get(son));
            				sonW.posTag=word2pos.get(son);
            				DependencyTreeNode tempNode=basicDT.insert(curNode, sonW, dep);
            				basicDT.word2node.put(son,tempNode);
            				basicDT.nodesList.add(tempNode);
        				}	
						
					}	
				}
				
			}
		}
		
	}
	public void constructBT() {
		//construct root node
		for(int i=0;i<triples.size();i++) {//构造好所有的结点和他们的儿子结点
			//先构建son为一个结点
			ArrayList<String> triple=triples.get(i);
			String cur=triple.get(2).trim();
			Word w=new Word(cur,cur,cur,word2index.get(cur));
			w.posTag=word2pos.get(cur);
			if(triple.get(1).equals("ROOT")) {		
				basicDT.setRoot(w);
				root=basicDT.setRoot(w);
				basicDT.nodesList.add(root);
				basicDT.word2node.put(cur, root);
				for(ArrayList<String> iterTriple:triples) {//check一轮,与之相关的结点
					String iterFather= iterTriple.get(1).trim();
					if(cur.equals(iterFather)) {//当前结点在某个三元组中充当Father
						String iterDep= iterTriple.get(0).trim();
						String iterSon= iterTriple.get(2).trim();
	    				if(basicDT.word2node.containsKey(iterSon)) {
	    					root.childrenList.add(basicDT.word2node.get(iterSon));
	    					basicDT.word2node.get(iterSon).father=root;
	    					basicDT.word2node.get(iterSon).dep_father2child=iterDep;
	    				}
	    				else {
	    					Word sonW=new Word(iterSon,iterSon,iterSon,word2index.get(iterSon));
		    				sonW.posTag=word2pos.get(iterSon);
	    					DependencyTreeNode tempNode=basicDT.insert(root, sonW, iterDep);
		    				basicDT.nodesList.add(tempNode);
							basicDT.word2node.put(iterSon, tempNode);
	    				}
					}
				}
			}
			else
			{
				DependencyTreeNode curNode;
				if(!basicDT.word2node.containsKey(cur)) {
					curNode=new DependencyTreeNode(w);
					basicDT.nodesList.add(curNode);
					basicDT.word2node.put(cur, curNode);
				}
				else {
					curNode=basicDT.word2node.get(cur);
				}
				for(ArrayList<String> iterTriple:triples) {//check一轮,与之相关的结点
					String iterFather= iterTriple.get(1).trim();
					if(cur.equals(iterFather)) {//当前结点在某个三元组中充当Father
						String iterDep= iterTriple.get(0).trim();
						String iterSon= iterTriple.get(2).trim();
						if(basicDT.word2node.containsKey(iterSon)) {
	    					curNode.childrenList.add(basicDT.word2node.get(iterSon));
	    					basicDT.word2node.get(iterSon).father=curNode;
	    					basicDT.word2node.get(iterSon).dep_father2child=iterDep;
	    				}
	    				else {
	    					Word sonW=new Word(iterSon,iterSon,iterSon,word2index.get(iterSon));
		    				sonW.posTag=word2pos.get(iterSon);
		    				DependencyTreeNode tempNode=basicDT.insert(curNode, sonW, iterDep);
							basicDT.nodesList.add(tempNode);
							basicDT.word2node.put(iterSon, tempNode);
	    				}
					}
				}
			}
		}
		basicDT.addSuperRoot();
	}
	public int checkTree() {
		System.out.println();
		
		for(DependencyTreeNode node:basicDT.nodesList) {
			if(node.father!=null) {
				System.out.println(node.word.baseForm+" "+node.father.word.baseForm+" "+node.dep_father2child+" "+node.word.posTag);
				System.out.println(node.word.baseForm+"的子节点如下：");
				System.out.print("     ");
				for(DependencyTreeNode child:node.childrenList) {
					System.out.print(child.word.baseForm+" ");
				}
				System.out.println();
			}
			else {
				System.out.println(node.word.baseForm+" "+node.dep_father2child+" "+node.word.posTag);
				System.out.println(node.word.baseForm+"的子节点如下：");
				System.out.print("     ");
				for(DependencyTreeNode child:node.childrenList) {
					System.out.print(child.word.baseForm+" ");
				}
				System.out.println();
			}
			
		}
		return basicDT.nodesList.size();
	}
	public static void main(String []args) {
		GenerateBasicParserTree gbp=new GenerateBasicParserTree ();
		Scanner scan = new Scanner(System.in);
		System.out.println("Please input the questions："); 
		while(scan.hasNextLine()) {
		    String question = scan.nextLine();
			DependencyTree basicDT=new DependencyTree();
			gbp.generateTriples(question, basicDT);
			gbp.constructBT();
			gbp.checkTree();
			System.out.println("Please input the questions："); 
		}
		scan.close();
		
		
//		FileUtil fu =new FileUtil();
//		List<String> lines=fu.readFile("C:\\Users\\kg\\Desktop\\DBpedia问句数据集\\complexQuestions\\complex-questions.txt");
//		for(String question:lines) {
//			DependencyTree basicDT=new DependencyTree();
//			int i=gbp.generateTriples(question,basicDT);
//			gbp.constructBT();
//			System.out.println(question);
//			System.out.println(i==gbp.chechTree());
//		}
		
	}
}
