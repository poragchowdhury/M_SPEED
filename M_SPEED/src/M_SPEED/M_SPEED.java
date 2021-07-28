package M_SPEED;

import java.awt.*;
import java.util.*;
import java.lang.Character;

public class M_SPEED {
	public Tree tree;
	public ArrayList<String> EpisodeList = new ArrayList<String>();
	
	public M_SPEED () {
		tree = new Tree();
	}// end M_SPEED
	
	public static void main(String[] args) {
		M_SPEED m_speed = new M_SPEED();
		// AdCBb2D3a12B4c7C1A2d8a1b8c3B3A11D4a2b3 is a sequence
		m_speed.run("AdCBb2D3a12B4c7C1A2d8a1b8c3B3A11D4a2b3");
		int root_freq = 0;
		for(char c : m_speed.tree.root.children.keySet()) 
			root_freq += m_speed.tree.root.children.get(c).frequency;
		System.out.println(root_freq);
		
		//m_speed.highProb();
	} //end main

	public void run(String seq){
		int max_window_length = 0;
		String window = "";	// window acts like a history of the sequence
		HashMap<Character, Integer> timeStorage = new HashMap<>();
		HashMap<Character, Integer> storage = new HashMap<>();
		String episode = "Not found";
		char E;
		int num; // number in a sequence
		String str_num = "";
		
		System.out.println("M_Speed starts!");
		
		for(int i = 0; i < seq.length(); i++){
			char e = seq.charAt(i);
			window += e;
			if(e >= 'A' && e <= 'Z')
				E = Character.toLowerCase(e); //if it is true ,display upper case
			else
				E = Character.toUpperCase(e); //if it is true ,display lower case
			
			str_num = "";
			int digitCount = 0;
			while(i+1 < seq.length() && Character.isDigit(seq.charAt(i+1))) {
				// if there is any number?
				str_num += seq.charAt(i+1);
				i++;
				digitCount++;
			} //end while
			num = (str_num == "") ? 0 : Integer.parseInt(str_num);
				
			// Episode extraction: find the episode
			if(storage.containsKey(E)) {								
				episode = seq.substring(storage.get(E), i-digitCount+1);
				System.out.println("Episode: " + episode);
				
				if (episode.length() > max_window_length)
					max_window_length = episode.length();
				
				window = seq.substring(i - max_window_length, i);
				timeStorage.put(e, timeStorage.getOrDefault(e, 0)+num);
				
				Read(trimDigits(episode), 0);
				System.out.print("All possible contexts : ");
				for(String context: EpisodeList) {
					if(context.length() <= max_window_length) {
						System.out.print(context + ",");
						tree.addEvents(context);
					} //end if
				}// end for
				System.out.println();
				EpisodeList.clear();
				storage.remove(E);
			} // end if
			storage.put(e, i-digitCount);
			System.out.println("Window after episode extraction : " + window);
		} //end for		
	} //end run
	
	public String trimDigits(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c : s.toCharArray()) {
			if(Character.isDigit(c))
				continue;
			sb.append(c);
		} //end for
		return sb.toString();
	} //end trimDigits

	public float CalcProb(String window, char c){
		TreeNode cur_node = tree.root;
		char[] WindList = window.toCharArray();
		float generalp = tree.root.children.get(c).frequency;
		float Calc = generalp/38;
		float nullc = cur_node.children.get(WindList[0]).frequency;
		nullc = 1/nullc;
		Calc = nullc * Calc;
		cur_node = cur_node.children.get(WindList[0]);
		TreeNode p = cur_node.children.get(c);
		float pfreq;
		if (p == null){
			pfreq = 0;
		} 
		else {
			pfreq = p.frequency;
		}
		pfreq = pfreq*nullc;
		Calc += pfreq;
		for(int i = 1; i < window.length(); i++) {
			cur_node = cur_node.children.get(WindList[i]);
			nullc = cur_node.frequency;
			nullc = 1/nullc;
			Calc = nullc * Calc;
			p = cur_node.children.get(c);
			if (p == null){
				pfreq = 0;
			} 
			else{
				pfreq = p.frequency;
			}
			pfreq = pfreq/cur_node.frequency;
			Calc += pfreq;
		} //end for
		System.out.println(Calc);
		return Calc; 
	} //end CalcProb
	
	public char getHighProbabilityEvent(String window) {	
		float maxProb = -1;
		char maxEvent = ' ';
		for(char event : tree.root.children.keySet()) {
			float c_prob = CalcProb(window, event);
			if(c_prob > maxProb) {
				maxProb = c_prob;
				maxEvent = event;
			}
		}
		System.out.println (maxEvent + " max value is:"+ maxProb);
		return maxEvent;
	}

	public void Read(String Episode, int i){
		if (i == Episode.length())
			return;
		// consider each substring `S[i, j]`
		for (int j = Episode.length() - 1; j >= i; j--){

			EpisodeList.add(Episode.substring(i,j+1));
			// append the substring to the result and recur with an index of
			// the next character to be processed and the result string
			Read(Episode, j + 1);
		} //end for
		
		Set<String> set = new HashSet<>(EpisodeList);
		EpisodeList.clear();
		EpisodeList.addAll(set);
	} //end Read
	
	
	
} //end class

