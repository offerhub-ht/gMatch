package nlp.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utils.FileUtil;
public class GenerateSubquestions {
	public DependencyTree DT;
	public void generateQuestionStructure() {
//		for(DependencyTreeNode node : DT.nodesList) {
//			System.out.println(node.word.baseForm+" "+node.dep_father2child);
//		}
		DT.reConstructDependencyTree();
		DT.mergeDetRelation();//只适用于WP det NN
		DT.mergeNmodBetweenNN();
        DT.mergeCompoundRelation();
        //DT.mergeCaseRelation();
        DT.mergeAdjectivePos();//只适用于JJ mod NN
//        System.out.println("------------------------------------------------>");
//        for(DependencyTreeNode node : DT.nodesList) {
//			System.out.println(node.word.baseForm+" "+node.dep_father2child);
//		}
	}
	//now DT is a basic question tree structure, of course, we  will further develop it in the future. 
	//next, we detect all entity words in DT, and sort them accoding to the question.
	public ArrayList<String> getModifierEntitiesFromDT(ArrayList<String> topicEnt){
		ArrayList<String> result=new ArrayList<String>();//notice that, all String in result are delimited by '_'.
		for(DependencyTreeNode n:DT.nodesList) {
			if(n.word.posTag.startsWith("N")||n.word.posTag.startsWith("CD")){
				if(!topicEnt.contains(n.word.baseForm)) {
					result.add(n.word.baseForm);
				}
			}
		}
		return result;//sorted entities according to  original question
	}
	public String getSubquestionBetweenTwoEntityPhrases(String w1,String w2) {
		ArrayList<DependencyTreeNode> ds1=DT.getTreeNodesListContainsWords(w1);
        ArrayList<DependencyTreeNode> ds2=DT.getTreeNodesListContainsWords(w2);
//        System.out.println("all data in ds1");
//        for(DependencyTreeNode str:ds1) {
//        	 System.out.println(str.word.baseForm);
//        }
//        for(DependencyTreeNode str:ds2) {
//       	 System.out.println(str.word.baseForm);
//       }
        if(!ds1.isEmpty()&&!ds2.isEmpty()) {
        	DependencyTreeNode d1=ds1.get(0);
            DependencyTreeNode d2=ds2.get(0);
            String result=DT.getShortestPathBetween(d1, d2);
            return result;
        }
        return "";
	}
	public boolean FilterWrongSub(String question,String startEnt,String endEnt,String sub) {
		//加一个判断，如果StartEnt和endEnt存在问句中才做
		if(question.contains(startEnt)&&question.contains(endEnt)) {
			String originalPartSub=question.substring(question.indexOf(startEnt)+startEnt.length(),question.indexOf(endEnt)).trim();
			//加一个判断start和endEnt也可能不存在于sub中
			if(sub.contains(startEnt)&&sub.contains(endEnt)) {
			String curPartSub=sub.substring(sub.indexOf(startEnt)+startEnt.length(),sub.indexOf(endEnt)).trim();
			System.out.println("curPartSub="+curPartSub);
			System.out.println("originalPartSub="+originalPartSub);
			String[] curWords=curPartSub.split(" ");
			boolean flag=false;
			for(String cW:curWords)
			{
				if(!originalPartSub.contains(cW)) {
					flag=true;
					break;
				}
			}
			return flag;
		}
		}
		return false;
	}
	public String polishSubquestion(String question,String subquestion) {
		return question;
	}
	public static void main(String[] args) throws InterruptedException {
		generateDS gD=new generateDS();
		GenerateBasicParserTree gbp=new GenerateBasicParserTree ();
		FileUtil fu =new FileUtil();
		GenerateSubquestions gs=new GenerateSubquestions();
		Scanner scan = new Scanner(System.in);
		System.out.println("Please input the questions：");
		ArrayList<String >subquestions=new ArrayList<String >();
		int count=0;
		List<String> lines=fu.readFile("C:\\Users\\kg\\Desktop\\DBpedia问句数据集\\complexQuestions\\complex-questions.txt");
		for(String question:lines) {
			//Thread.sleep(5000);\
			//String question = scan.nextLine();
	        System.out.println("-----------------Question "+Integer.toString(count)+" ------------------");
	        System.out.println(question);
			//gD.generate(question);
            //gs.DT=gD.DT;
            DependencyTree basicDT=new DependencyTree();
			gbp.generateTriples(question, basicDT);
			gbp.constructBT();
			gs.DT=basicDT;
            gs.generateQuestionStructure();
            ArrayList<String> topicEnt=gs.DT.getTopicEntities();
            ArrayList<String >entities=gs.getModifierEntitiesFromDT(topicEnt);
           for(String topic:topicEnt) {
        	   System.out.print(topic+"|");
           }
           System.out.println();
           for(String modifier:entities) {
        	   System.out.print(modifier+"|");
           }      
           System.out.println();
           count++;
		}
		
		
//		while(scan.hasNextLine()) {
//	            String question = scan.nextLine();
//	            gD.generate(question);
//	            gs.DT=gD.DT;
//	            gs.generateQuestionStructure();
//	            ArrayList<String >entities=gs.getAllEntitiesFromDT();
//	            for(String entity:entities) {
//	            	String []words=entity.split("_");
//	            	System.out.println(words[0]+"_"+words[1]);
//	            	String result=gs.getSubquestionBetweenTwoEntityPhrases(words[0],words[1]);
//	            	if(!gs.FilterWrongSub(question,words[0],words[1],result)) {
//	            		subquestions.add(result);
//	            	}
//	            }
//	            System.out.println("Print all subquestions--------------------------------- :");
//            	for(String str:subquestions) {
//            		System.out.println(str);
//            	}
//	            subquestions.clear();
//	            System.out.println("Please input the questions：");                                                                                                                              
//	        }
       scan.close();
	}
}
