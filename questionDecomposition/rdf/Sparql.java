package rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Sparql implements Comparable<Sparql> 
{
	public ArrayList<Triple> tripleList = new ArrayList<Triple>();
	public boolean countTarget = false;
	public String mostStr = null;
	public String moreThanStr = null;
	public double score = 0;
	
	public String questionFocus = null;	// The answer variable
	public HashSet<String> variables = new HashSet<String>();
	
	public enum QueryType {Select,Ask}
	public QueryType queryType = QueryType.Select;
	

	public void addTriple(Triple t) 
	{
		if(!tripleList.contains(t))
		{
			tripleList.add(t);
			score += t.score;
		}
	}
	
	public void delTriple(Triple t)
	{
		if(tripleList.contains(t))
		{
			tripleList.remove(t);
			score -= t.score;
		}
	}

	@Override
	public String toString() 
	{
		String ret = "";
		for (Triple t : tripleList) {
			ret += t.toString();
			ret += '\n';
		}
		return ret;
	}
	
	
	
	// Is it a Basic Graph Pattern without filter and aggregation?
	public boolean isBGP()
	{
		if(moreThanStr != null || mostStr != null || countTarget)
			return false;
		return true;
	}
	
	//Use to display (can not be executed)

	
	/**
	* @description:
	* 1. Select all variables for BGP queries to display specific information.
	* 2. DO NOT select all variables when Aggregation like "HAVING" "COUNT" ... 
	* (It may involves too many results, e.g. "which countries have more than 1000 caves?", caves is no need to display) 
	* @param: NULL.
	* @return: A SPARQL query can be executed by GStore (NO prefix of entities/predicates).
	*/
	
	

	public void adjustTriplesOrder() 
	{
		Collections.sort(this.tripleList);
	}

	public int compareTo(Sparql o) 
	{
		double diff = this.score - o.score;
		if (diff > 0) 
			return -1;
		else if (diff < 0)
			return 1;
		else
			return 0;
	}
	
	@Override 
	public int hashCode() 
    { 
		int key = 0;
		for(Triple t: this.tripleList)
			key ^= t.hashCode();
        return key; 
    } 
	
	
	public void removeLastTriple() 
	{
		int idx = tripleList.size()-1;
		score -= tripleList.get(idx).score;
		tripleList.remove(idx);
	}
	
	public Sparql removeAllTypeInfo () 
	{
		score = 0;
		ArrayList<Triple> newTripleList = new ArrayList<Triple>();
		for (Triple t : tripleList) 
		{	
			if (t.predicateID != 1) 
			{
				newTripleList.add(t);
				score += t.score;
			}
		}
		tripleList = newTripleList;
		return this;
	}

};
