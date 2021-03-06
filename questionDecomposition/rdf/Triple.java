package rdf;

import nlp.tool.Word;

public class Triple implements Comparable<Triple>{
	public String subject = null;	// subject/object after disambiguation.
	public String object = null;
	
	static public int TYPE_ROLE_ID = -5;
	static public int VAR_ROLE_ID = -2;
	static public int CAT_ROLE_ID = -8;	// Category
	static public String VAR_NAME = "?xxx";
	
	// subjId/objId: entity id | TYPE_ROLE_ID | VAR_ROLE_ID
	public int subjId = -1;
	public int objId = -1;
	public int predicateID = -1;
	public Word subjWord = null;	// only be used when semRltn == null
	public Word objWord = null;
	
	public double score = 0;
	public boolean isSubjObjOrderSameWithSemRltn = true;
	public boolean isSubjObjOrderPrefered = false;
	
	public Word typeSubjectWord = null; // for "type" triples only
	
	public Triple (Triple t) {
		subject = t.subject;
		object = t.object;
		subjId = t.subjId;
		objId = t.objId;
		predicateID = t.predicateID;
		
		score = t.score;
		isSubjObjOrderSameWithSemRltn = t.isSubjObjOrderSameWithSemRltn;
		isSubjObjOrderPrefered = t.isSubjObjOrderPrefered;
	}

	// A triple translated from a semantic relation (subject/object order can be changed in later)

	
	// A final triple (subject/object order will not changed), does not rely on semantic relation (sr == null), from two word (implicit relations of modifier)

	public Triple copy() {
		Triple t = new Triple(this);
		return t;
	}
	
	public Triple copySwap() {
		Triple t = new Triple(this);
		String temp;
		int tmpId;

		tmpId = t.subjId;
		t.subjId = t.objId;
		t.objId = tmpId;
		
		temp = t.subject;
		t.subject = t.object;
		t.object = temp;
		
		t.isSubjObjOrderSameWithSemRltn = !this.isSubjObjOrderSameWithSemRltn;
		t.isSubjObjOrderPrefered = !this.isSubjObjOrderPrefered;
		
		return t;
	}
	
	public void addScore(double s) {
		score += s;
	}
	
	public double getScore() {
		return score;
	}
	
	@Override 
	public int hashCode() 
    { 
        return new Integer(subjId).hashCode() ^ new Integer(objId).hashCode() ^ new Integer(predicateID).hashCode(); 
    } 
		
	public void swapSubjObjOrder() {		
		String temp = subject;
		int tmpId = subjId;
		subject = object;
		subjId = objId;
		object = temp;
		objId = tmpId;
		isSubjObjOrderSameWithSemRltn = !isSubjObjOrderSameWithSemRltn;
	}

	@Override
	public int compareTo(Triple o) {
		// TODO Auto-generated method stub
		return 0;
	}
};