package nlp.tool;
/*
 The purpose of this class is to generate an accurate dependency tree of a given question. We apply the newset standford corenlp tool version to generate the tree. Note that, we support three types of
 dependency tree, namely, BasicDependencies, CollapsedCCProcessedDependencies and EnhancedPlusPlusDependencies.
 @author shujun wang
 */
import java.io.*;
import java.util.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class generateDS {
	public Properties props;
	public StanfordCoreNLP pipeline;
	public HashMap<String, Integer> wordPos;
	public String[] allWords;
	public String EPPDTree;
	public String BDTree;
	public String tripleCPDTree;
	public String EDTree;
	public String plainQuestion;
	public DependencyTreeNode root;
	public DependencyTree DT;
	public generateDS() {
		props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
		pipeline = new StanfordCoreNLP(props);
	}
	public static  int getSpaceNum(String str){
		int count=0;
		for(int i=0 ; i<str.length();i++) {
			if(str.charAt(i)==' ') {
				count++;
			}
		}
		return count;
	}
	
	public HashMap<String, Integer> getTokens(String allTrees){
		wordPos= new HashMap<String, Integer>();
	      String tokenPattern="Tokens=\\[.*?\\]";
	      Pattern  rToken= Pattern.compile(tokenPattern);
	      Matcher mToken = rToken.matcher(allTrees);
	      if (mToken.find( )) {
	    	  String wordIndex= mToken.group(0);
	    	  wordIndex=wordIndex.substring(wordIndex.indexOf('[')+1,wordIndex.indexOf(']'));
	    	  String [] words=wordIndex.split(",");
	    	  for(String word : words)
	    	  {
	    		  String[] line=word.split("-");
	    		  wordPos.put(line[0].trim(), Integer.parseInt(line[1]));
	    		  //System.out.println(line[0].trim()+"---->"+line[1]);
	    	  }
	      }
	      return wordPos;
	}
	public void serializeWords() {
		int spaceNum=0;
        HashMap<Integer,Integer> spaceInWord=new  HashMap<Integer,Integer>();
        for(int i=0;i<allWords.length;i++)
        {
        	int key=getSpaceNum(allWords[i]);
        	if(!spaceInWord.containsKey(key)) {
        		spaceInWord.put(key, spaceNum);
        		spaceNum++;
        		}
        	String tempStr=allWords[i].trim();
        	String tempSpace="";
        	for(int s=0;s<spaceInWord.get(key);s++){
        		tempSpace=tempSpace+" ";
        		}
        	allWords[i]=tempSpace+tempStr.trim();
        	}
	}
	public DependencyTreeNode constructTree()
	{
		String rootStr=allWords[0].substring(0, allWords[0].indexOf('/'));
		String rootTag=allWords[0].substring(allWords[0].indexOf('/')+1, allWords[0].indexOf('('));
		Word w=new Word(rootStr.trim(),rootStr.trim(),rootStr.trim(),wordPos.get(rootStr.trim()));
		w.posTag=rootTag;
		DT.setRoot(w);
		root=DT.setRoot(w);
		DT.nodesList.add(root);
        BlockingQueue<DependencyTreeNode> qNode = new ArrayBlockingQueue<DependencyTreeNode>(100);
        BlockingQueue<String> qStr = new ArrayBlockingQueue<String>(100);
        qNode.add(root);
        //serialize all words
        int spaceNum=0;
        HashMap<Integer,Integer> spaceInWord=new  HashMap<Integer,Integer>();
        for(int i=0;i<allWords.length;i++)
        {
        	int key=getSpaceNum(allWords[i]);
        	if(!spaceInWord.containsKey(key)) {
        		spaceInWord.put(key, spaceNum);
        		spaceNum++;
        		}
        	String tempStr=allWords[i].trim();
        	String tempSpace="";
        	for(int s=0;s<spaceInWord.get(key);s++){
        		tempSpace=tempSpace+" ";
        		}
        	allWords[i]=tempSpace+tempStr.trim();
        	}
        String start=allWords[0];
        qStr.add(start);
        while(!qNode.isEmpty()) {
        	DependencyTreeNode curNode=qNode.poll();
        	String curStr=qStr.poll();
        	int startPos=0;
        	for(int i=0;i<allWords.length;i++){
        		if(allWords[i]==curStr){
        			startPos=i;
        			break;
        			}
        		}
        	for(int i=startPos+1;i<allWords.length;i++){
        		if(getSpaceNum(curStr)==getSpaceNum(allWords[i])) {
        			break;
        			}
        		else{
        			if(getSpaceNum(allWords[i])==getSpaceNum(curStr)+1)
        			{
        				String word=allWords[i];
        				String tempWord=word;
        				String tempStr=word.substring(0, word.indexOf('/'));
        				String tag=word.substring(word.indexOf('/')+1, word.indexOf('('));
        				String dep_father2child=word.substring(word.indexOf('(')+1, word.indexOf(')'));
        				Word tempW=new Word(tempStr.trim(),tempStr.trim(),tempStr.trim(),wordPos.get(tempStr.trim()));
        				tempW.posTag=tag.trim();
        				DependencyTreeNode tempNode=DT.insert(curNode, tempW, dep_father2child.trim());
        				DT.nodesList.add(tempNode);
        				qNode.add(tempNode);
        				qStr.add(tempWord);
        			  }
        		  }
        	  }
          }
          return root;
	}
	public void generateByEPPD(String allTrees) {
		String EPPDpattern = "EnhancedPlusPlusDependencies([\\S\\s]*)\\)";
		Pattern  rEPPD= Pattern.compile(EPPDpattern);
		Matcher mEPPD = rEPPD.matcher(allTrees);
		if (mEPPD.find( )) {
			//initilize a dependency tree
			DT=new DependencyTree ();
			//obatain all lines in allTrees
			EPPDTree=mEPPD.group(0);
	        String [] temp=EPPDTree.split("= ");
	        EPPDTree=temp[1];
	        allWords=EPPDTree.split("\n");
	        serializeWords();
	        constructTree();
	        System.out.println(DT.toString());
		}
	}
	public void addSuperRoot() {
		DT.addSuperRoot();
	}
		public void generateByBD(String allTrees) {
			String BDpattern = "BasicDependencies([\\S\\s]*)Collapsed";
			Pattern  rBD= Pattern.compile(BDpattern);
			Matcher mBD = rBD.matcher(allTrees);
			if (mBD.find( )) {
				//initilize a dependency tree
				DT=new DependencyTree ();
				//assign plainQuestion
				DT.plainQuestion=plainQuestion;
				//obatain all lines in allTrees
				BDTree=mBD.group(0);
				BDTree=BDTree.substring(0, BDTree.length()-10);
		        String [] temp=BDTree.split("= ");
		        BDTree=temp[1];
		        allWords=BDTree.split("\n");
		        serializeWords();
		        constructTree();
		        addSuperRoot();
			}
          
	}
		public void generateTripleCPD(String allTrees) {
			String tripleCPDpattern = "CollapsedCCProcessedDependencies([\\S\\s]*)EnhancedDependencies";
			Pattern  rTripleCPD= Pattern.compile(tripleCPDpattern);
			Matcher mTripleCPD = rTripleCPD.matcher(allTrees);
			if (mTripleCPD.find( )) {
				//initilize a dependency tree
				DT=new DependencyTree ();
				//obatain all lines in allTrees
				tripleCPDTree=mTripleCPD.group(0);
				System.out.println(tripleCPDTree);
				tripleCPDTree=tripleCPDTree.substring(0, tripleCPDTree.length()-21);
				System.out.println(tripleCPDTree);
		        String [] temp=tripleCPDTree.split("= ");
		        tripleCPDTree=temp[1];
		        allWords=tripleCPDTree.split("\n");
		        serializeWords();
		        for(String word : allWords) {
		        	System.out.println(word);
		        }
		        constructTree();
		        System.out.println(DT.toString());
			}
          
	}
	public void generate(String question) {
		plainQuestion=question;
		Annotation annotation;
		annotation = new Annotation(question);
		pipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class); 
	    if (sentences != null && ! sentences.isEmpty()) {
	      CoreMap sentence = sentences.get(0);
	      String allTrees=sentence.toShorterString();
	      //A simple treatment of the result of Standford parser
	      allTrees=allTrees.replaceAll("->"," ");
	      wordPos= getTokens(allTrees);
	      //generateByEPPD(allTrees);
	      //generateByBD(allTrees);
	      generateByBD(allTrees);
	    }
	}
	public void getPathBetweenTwoEntities(String w1,String w2) {
		List<DependencyTreeNode> ds1=DT.getTreeNodesListContainsWords(w1);
		List<DependencyTreeNode> ds2=DT.getTreeNodesListContainsWords(w2);
		if(!ds1.isEmpty()&&!ds2.isEmpty()) {
			DependencyTreeNode d1=ds1.get(0);
			DependencyTreeNode d2=ds2.get(0);
			ArrayList<DependencyTreeNode> path=DT.getShortestNodePathBetween(d1, d2);
			for(DependencyTreeNode n:path) {
				System.out.println(n.word.baseForm);
			}
		}
	}
	public static void main(String[] args) throws IOException {
		generateDS gD=new generateDS();
		Scanner scan = new Scanner(System.in);
		System.out.println("next方式接收：");
		while(scan.hasNextLine()) {
	            String str2 = scan.nextLine();
	            System.out.println("输入的数据为：" + str2);
	            gD.generate(str2);
	            System.out.println("next方式接收：");
	        }
        scan.close();
	  }
}
