package nlp.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.GrammaticalStructure;
//import edu.stanford.nlp.semgraph.SemanticGraph;

public class DependencyTree {
	public DependencyTreeNode root = null;
	public ArrayList<DependencyTreeNode> nodesList = null;
	public String plainQuestion;
	HashMap<String,DependencyTreeNode> word2node=null;
	public StanfordCoreNLP pipline=null;
	public GrammaticalStructure gs = null;		// Method 2: Stanford Parser
	
	public HashMap<String, ArrayList<DependencyTreeNode>> wordBaseFormIndex = null;
	
	public DependencyTree () {	
		this.nodesList=new ArrayList<DependencyTreeNode>();
		this.word2node=new HashMap<String,DependencyTreeNode>();
	}
	
	
	public DependencyTreeNode setRoot(Word w) {
		root = new DependencyTreeNode(w, "root", null);			
		return root;
	}
	
	public DependencyTreeNode setRoot(DependencyTreeNode root) {
		this.root = root;
		return this.root;
	}
	
	public void buildWordBaseFormIndex () {
		wordBaseFormIndex = new HashMap<String, ArrayList<DependencyTreeNode>>();
		for (DependencyTreeNode dtn: nodesList) {
			String w = dtn.word.baseForm;
			if (!wordBaseFormIndex.keySet().contains(w))
				wordBaseFormIndex.put(w, new ArrayList<DependencyTreeNode>());
			wordBaseFormIndex.get(w).add(dtn);
		}
	}
	
	public DependencyTreeNode insert(DependencyTreeNode father, Word w, String dep_father2child) {
		if (father == null || w == null)
			return null;
		
		DependencyTreeNode newNode = new DependencyTreeNode(w, dep_father2child, father);
		father.childrenList.add(newNode);
		father.neighborList.add(newNode);
		return newNode;
	}
	
	public DependencyTreeNode getRoot() {
		return root;
	}
	
	public ArrayList<DependencyTreeNode> getNodesList(){
		return nodesList;
	}
	public void adjustNodeLevel(DependencyTreeNode node) {
		node.levelInTree=node.levelInTree-1;
//		for(DependencyTreeNode childOfNode:node.childrenList) {
//			adjustNodeLevel(childOfNode);
//		}
	}
	public void reConstructDependencyTree()//mainly 'conj' relation
	{
		for(DependencyTreeNode node : this.nodesList) {
			if(node.dep_father2child.equals("conj")) {
				adjustNodeLevel(node);
				DependencyTreeNode father=node.father;
				DependencyTreeNode grandFather=father.father;
					grandFather.childrenList.add(node);
					grandFather.neighborList.add(node);
					node.dep_father2child=father.dep_father2child;
					node.father=grandFather;
					father.neighborList.remove(node);
					father.childrenList.remove(node);
			}
		}
			
	}
	public void mergeCompoundRelation() {
		//merge之后要把一些结点删除掉
		ArrayList<DependencyTreeNode> tempList=new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode node : this.nodesList) {
			if(node.dep_father2child.equals("compound")) {
				DependencyTreeNode father=node.father;
				father.childrenList.remove(node);
				tempList.add(node);
				father.word.baseForm=node.word.baseForm+" "+father.word.baseForm;
			}
			}
		for(DependencyTreeNode node : tempList) {
			this.nodesList.remove(node);
		}
	}
	public void mergeDetRelation() {
		ArrayList<DependencyTreeNode> tempList=new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode node : this.nodesList) {
			if(node.dep_father2child.equals("det")&&node.word.posTag.startsWith("W")) {
				DependencyTreeNode father=node.father;
				father.word.posTag=node.word.posTag;
				father.childrenList.remove(node);
				tempList.add(node);
				father.word.baseForm=node.word.baseForm+" "+father.word.baseForm;
			}
			}
		for(DependencyTreeNode node : tempList) {
			this.nodesList.remove(node);
		}
	}
	public void mergeNmodBetweenNN() {
		ArrayList<DependencyTreeNode> tempList=new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode node : this.nodesList) {
			if((node.dep_father2child.contains("nmod")&&node.word.posTag.startsWith("N")&&node.father.word.posTag.startsWith("N"))||(node.dep_father2child.contains("nmod")&&node.word.posTag.startsWith("P")&&node.father.word.posTag.startsWith("N"))) {
				
				DependencyTreeNode father=node.father;
				father.childrenList.remove(node);
				tempList.add(node);
				if(!node.childrenList.isEmpty()) {
					node.word.baseForm=node.childrenList.get(0).word.baseForm+" "+node.word.baseForm;
					tempList.add(node.childrenList.get(0));
				}
				father.word.baseForm=father.word.baseForm+" "+node.word.baseForm;
			}
			}
		for(DependencyTreeNode node : tempList) {
			this.nodesList.remove(node);
		}
		
		
	}
	
	public void mergeCaseRelation() {
		ArrayList<DependencyTreeNode> tempList=new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode node : this.nodesList) {
			if(node.dep_father2child.equals("case")) {
				DependencyTreeNode father=node.father;
				father.childrenList.remove(node);
				tempList.add(node);
				father.word.baseForm=node.word.baseForm+" "+father.word.baseForm;
			}
			}
		for(DependencyTreeNode node : tempList) {
			this.nodesList.remove(node);
		}
	}
	public ArrayList<String> getTopicEntities(){
		ArrayList<String> topicEnt=new ArrayList<String>();
		for(DependencyTreeNode node : this.nodesList) {
			if(node.word.posTag.startsWith("W")) {
				topicEnt.add(node.word.baseForm);
				continue;
			}
			if(node.dep_father2child.contains("subj")||node.dep_father2child.equals("poss")||node.dep_father2child.equals("partmod")) {
				topicEnt.add(node.word.baseForm);
			}
		}
		
		return topicEnt;
		
	}
	
	
	public void mergeAdjectivePos() {
		ArrayList<DependencyTreeNode> tempList=new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode node : this.nodesList) {
			if(node.word.posTag.contains("NN") ) {
				for (DependencyTreeNode childNode : node.childrenList) {
					if(childNode.word.posTag.contains("JJ")&&childNode.father.word.posTag.startsWith("N")){
						tempList.add(childNode);
						node.word.baseForm=childNode.word.baseForm+" "+node.word.baseForm;	
					}
				}
			}
		}
		for(DependencyTreeNode node : tempList) {
			this.nodesList.remove(node);
		}
	}
	
	
	
	public void addSuperRoot() {
		Word tempW=new Word("","","",-1);
		tempW.posTag="";
		DependencyTreeNode superNode = new DependencyTreeNode(tempW);
		superNode.dep_father2child="super";
		for(DependencyTreeNode node : this.nodesList) {
			if(node.dep_father2child.equals("root")) {
				superNode.childrenList.add(node);
				node.father=superNode;
			}
		}
		this.nodesList.add(superNode);
	}
	public ArrayList<DependencyTreeNode> getShortestNodePathBetween(DependencyTreeNode n1, DependencyTreeNode n2) 
	{
		if(n1 == n2) {
			return new ArrayList<DependencyTreeNode>();
		}
		
		ArrayList<DependencyTreeNode> path1 = getPath2Root(n1);
		ArrayList<DependencyTreeNode> path2 = getPath2Root(n2);
		
		int idx1 = path1.size()-1;
		int idx2 = path2.size()-1;
		DependencyTreeNode curNode1 = path1.get(idx1);
		DependencyTreeNode curNode2 = path2.get(idx2);
		
		while (curNode1 == curNode2) {
			idx1 --;
			idx2 --;
			if(idx1 < 0 || idx2 < 0) break;
			curNode1 = path1.get(idx1);
			curNode2 = path2.get(idx2);			
		}
		
		ArrayList<DependencyTreeNode> shortestPath = new ArrayList<DependencyTreeNode>();
		for (int i = 0; i <= idx1; i ++) {
			shortestPath.add(path1.get(i));
		}
		for (int i = idx2+1; i >= 0; i --) {
			shortestPath.add(path2.get(i));
		}
		
		System.out.println("Shortest Path between <" + n1 + "> and <" + n2 + ">:");
		System.out.print("\t-");
		String result="";
		for (DependencyTreeNode dtn : shortestPath) {
			result=result+dtn.word.baseForm+" ";
			System.out.print("<" + dtn.word.baseForm + ">-");
		}
		System.out.println();
		
		return shortestPath;
	}
	public String getShortestPathBetween(DependencyTreeNode n1, DependencyTreeNode n2) 
	{
		if(n1 == n2) {
			return "";
		}
		
		ArrayList<DependencyTreeNode> path1 = getPath2Root(n1);
		ArrayList<DependencyTreeNode> path2 = getPath2Root(n2);
		
		int idx1 = path1.size()-1;
		int idx2 = path2.size()-1;
		DependencyTreeNode curNode1 = path1.get(idx1);
		DependencyTreeNode curNode2 = path2.get(idx2);
		
		while (curNode1 == curNode2) {
			idx1 --;
			idx2 --;
			if(idx1 < 0 || idx2 < 0) break;
			curNode1 = path1.get(idx1);
			curNode2 = path2.get(idx2);			
		}
		
		ArrayList<DependencyTreeNode> shortestPath = new ArrayList<DependencyTreeNode>();
		for (int i = 0; i <= idx1; i ++) {
			shortestPath.add(path1.get(i));
		}
		for (int i = idx2+1; i >= 0; i --) {
			shortestPath.add(path2.get(i));
		}
		String result="";
		for (DependencyTreeNode dtn : shortestPath) {
			result=result+dtn.word.baseForm+" ";
			//System.out.print("<" + dtn.word.baseForm + ">-");
		}
		System.out.println();
		
		return result;
	}
	
	public ArrayList<DependencyTreeNode> getPath2Root(DependencyTreeNode n1) {
		ArrayList<DependencyTreeNode> path = new ArrayList<DependencyTreeNode>();
		DependencyTreeNode curNode = n1;
		path.add(curNode);
		while (curNode.father != null) {
			curNode = curNode.father;
			path.add(curNode);
		}
		return path;
	}
	
	public ArrayList<DependencyTreeNode> getTreeNodesListContainsWords(String words) {
		ArrayList<DependencyTreeNode> ret = new ArrayList<DependencyTreeNode>();
		for (DependencyTreeNode dtn : nodesList) {
			if(dtn.word.originalForm.contains(words.trim())||words.contains(dtn.word.originalForm)) {
				ret.add(dtn);
			}
			/*if (dtn.word.originalForm.equalsIgnoreCase(words)
				|| dtn.word.baseForm.equalsIgnoreCase(words)
				|| words.contains(dtn.word.originalForm)
				|| words.contains(dtn.word.baseForm))*/
			//ret.add(dtn);
		}
		return ret;
	}
	
	public DependencyTreeNode getNodeByIndex (int posi) {
		for (DependencyTreeNode dt : nodesList) {
			if (dt.word.position == posi) {
				return dt;
			}
		}
		return null;
	}
	
	public DependencyTreeNode getFirstPositionNodeInList(ArrayList<DependencyTreeNode> list) {
		int firstPosi = Integer.MAX_VALUE;
		DependencyTreeNode firstNode = null;
		for (DependencyTreeNode dtn : list) {
			if (dtn.word.position < firstPosi) {
				firstPosi = dtn.word.position;
				firstNode = dtn;
			}
		}
		return firstNode;
	}
	
	@Override
	public String toString() {
		String ret = "";

		Stack<DependencyTreeNode> stack = new Stack<DependencyTreeNode>();
		stack.push(root);
		while(!stack.empty()) {
			DependencyTreeNode curNode = stack.pop();
			for (int i = 0; i <= curNode.levelInTree; i ++)
				ret += " ";
			ret += "-> ";
			ret += curNode.word.baseForm;
			ret += "-";
			ret += curNode.word.posTag;
			ret += " (";
			ret += curNode.dep_father2child;
			ret += ")";
			ret += "[" + curNode.word.position + "]\n";
			
			for (DependencyTreeNode child : curNode.childrenList) {
				stack.push(child);
			}
		}		
		return ret;
	}	
}
