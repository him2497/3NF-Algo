import java.io.*;
import java.util.*;


class ThirdNormalForm{

    static HashSet<FD> FDS = new HashSet<>();
    static HashSet<String> attributes = new HashSet<>();
    static HashMap<String, String> FDSRelation = new HashMap<>();
    static ArrayList<String> candidateKey = new ArrayList<String>();
    public static void main(String[] args){
        try{
            File file = new File(args[0]);
            BufferedReader br = new BufferedReader(new FileReader(file)); 
            String st = ""; 
            ArrayList<String> FDSList = new ArrayList<>();
            while ((st = br.readLine()) != null){
                FDSList.add(st);

                String[] arr = st.split(",|;");
                for(int i = 0; i < arr.length; i++){
                    attributes.add(arr[i]);
                }
            }

        System.out.println("Attributes in Relation"); 
        System.out.println(attributes);
        //Do the main functions here like minimal basis, closure and 3NF decomp

        addFDS(FDSList);
        System.out.println("Original FD's");
        printFDS();

        System.out.println("Candidate Keys are");
        candidateKey = getCandidateKey(FDS);
        System.out.println(getCandidateKey(FDS));

        HashSet<FD> mb = minimalBasis(FDS);
        System.out.println("Minimal Basis");
        printFDS();

        System.out.println("The 3NF Decomposition equals: ");
        System.out.println(decomposition3NF(FDS));

        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void addFDS(ArrayList<String> st){
        for(String s: st){
            String[] splitted= s.split(";");
            FDSRelation.put(splitted[0],splitted[1]);
            FD fd = new FD(splitted[0],splitted[1]);
            FDS.add(fd);
        }
    }

    /**
     * A function that prints all the functional dependencies
     */
    public static void printFDS(){
        for(FD i: FDS){
            i.printout();
        }
    }

 
    /**
     * 
     *  Check Minimal Basis and remove values in the following order
     *  Step 1 Split the RHS if more than one FD on that side
     *  Step 2 No extranious attributes on the LHS if A --> B and AB --> C 
     *  then we can simplify to A --> B and A --> C
     *  Step 3 Remove redundant FD's
     * @return it outputs a fd which has been reduced
     */
    public static HashSet<FD> minimalBasis(HashSet<FD> fds) {
        // In this step we split the RHS of multiple dependencies to a relation with 
        splitRHS(fds);

        eliminateDependecies(fds);
        return fds; 
    }

    /**
     * THIS METHOD BREAKS THE RHS OF THE FD TO SEPERATE FD'S
     * @param functionalDependency the dependency that needs to be broken for RHS
     */
    public static HashSet<FD> splitRHS(HashSet<FD> fds){
        HashSet<FD> toAdd = new HashSet<>();
        HashSet<FD> toRemove = new HashSet<>();
        for(FD f: fds){
            FD temp = f;
            String[] rhsSplit = f.getRHS().split(",");
            if(rhsSplit.length > 1){
                for(String s: rhsSplit){
                    FD fd = new FD(f.getLHS(),s);
                    toAdd.add(fd);
                }
                toRemove.add(temp);
            }
        }
        fds.addAll(toAdd);
        fds.removeAll(toRemove);
        return fds;
    }

    /**
     * This function removes duplicate dependencies 
     * and also check if a depenency is redundant they removes 
     * those dependencies from the FD and adds non redundant ones 
     * to the FD's
     * @param fds
     * @return a new list of FD's
     */
    public static HashSet<FD> eliminateDependecies(HashSet<FD> fds){
        Set<FD> toRemove = new HashSet<>();
        Set<FD> toAdd = new HashSet<>();
        Set<FD> commonDep = new HashSet<>();
		for(FD fd : fds){
			if(fd.LHS.contains(fd.RHS)){
				toRemove.add(fd);
			}
			else{
                Set<String> toRemoveFromRight = new HashSet<>();
				for(String a : fd.getRHS().split(",")){
					if(fd.LHS.contains(a)){
						toRemoveFromRight.add(a);
					}
                }
				if(!toRemoveFromRight.isEmpty()){
                    Set<String> right = new HashSet<>();
                    right.add(fd.getRHS());
					right.removeAll(toRemoveFromRight);
					toRemove.add(fd);
					toAdd.add(new FD(fd.LHS, fd.RHS));
				}
			}
        }

        for(FD fd: fds){ 
            for(FD fd1: fds){
                if(fd.getRHS().equals(fd1.getRHS()) && hasSomeKeys(fd.getLHS(), fd1.getLHS())){
                    String common = getCommonKeys(fd.getLHS(), fd1.getLHS());
                    if(!fd.equals(fd1)){
                        FD f = new FD(common, fd.getRHS());
                        commonDep.add(f);
                        toRemove.add(fd);
                        
                    }
                }
            }
        }

        fds.addAll(toAdd);
        fds.addAll(commonDep);
        fds.removeAll(toRemove);
        return fds;
    }
    
    /**
     * Helper method for eliminate dependencies
     * @param s1 the left hand side of the FD
     * @param s2 the left hand side of the FD
     * @return a boolean value if the two LHS values have common values
     */
    public static boolean hasSomeKeys(String s1, String s2){
        Set<Character> set1 = new HashSet<>();
        Set<Character> set2 = new HashSet<>();
        for(char c : s1.toCharArray()) {
            set1.add(c);
        }
        for(char c : s2.toCharArray()) {
            set2.add(c);
        }

        return set1.retainAll(set2);
    }

    /**
     * Helper method for eliminate dependencies
     * @param s1 the left hand side of the FD
     * @param s2 the left hand side of the FD
     * @return the value that both LHS have which can help remove 
     * unecessary dependencies.
     */
    public static String getCommonKeys(String s1, String s2){
        Set<Character> set1 = new HashSet<>();
        Set<Character> set2 = new HashSet<>();
        for(char c : s1.toCharArray()) {
            set1.add(c);
        }
        for(char c : s2.toCharArray()) {
            set2.add(c);
        }
        if(set1.size() == set2.size()){
            set1.retainAll(set2);
        }else if(set1.size() < set2.size()){
            set2.retainAll(set1);
        }
        List<String> list = new ArrayList<String>();
        for(char c : set1) {
            if(!Character.toString(c).equals(",")){
                list.add(Character.toString(c));
            }
        }

        return String.join(",", list);
    }

    public static ArrayList<String> getCandidateKey(HashSet<FD> fds){
        ArrayList<String> candidate = new ArrayList<>();
        ArrayList<String> toRemove = new ArrayList<>();
        for(FD fd: fds){ 
            candidate.add(fd.getLHS());
            if(!fd.getLHS().equals(fd.getRHS())){
                toRemove.add(fd.getRHS());
            }
        }
        for(String c: candidate){
            for( int i = 0; i < candidate.size(); i++){
                if(c.contains(candidate.get(i)) && !c.equals(candidate.get(i))){
                    if(c.length() > candidate.get(i).length()){
                        toRemove.add(candidate.get(i));
                    }else{
                        toRemove.add(c);
                    }
                }
            }
        }
        candidate.removeAll(toRemove);
        return candidate;
    }

    /**
     * This method looks at the closure for a key and then returns the attr
     * @param mb
     * @return
     */
    public static ArrayList<String> closure(String attr, HashSet<FD> fds){
        ArrayList<String> result = new ArrayList<>();
        result.add(attr);
        result.add(FDSRelation.get(attr));			
        return result;
    }


    //Check code in Relation.java and then Algo.java
    public static ArrayList<ArrayList<String>> decomposition3NF(HashSet<FD> fds){
        HashSet<String> attr = attributes;
        ArrayList<ArrayList<String>> K = new ArrayList<>();
        ArrayList<ArrayList<String>> toRemove = new ArrayList<>();
        for(FD f: fds){
            K.add(closure(f.getLHS(), fds));
                
        }

        for (ArrayList<String> list : K){
            for (ArrayList<String> list1 : K){
                if(!list.equals(list1)){
                    list.removeAll(list1);
                    // list1.removeAll(list);
                }
                if(list.size() < 2){
                    toRemove.add(list);
                }
            }
        }
        K.removeAll(toRemove);
        K.add(candidateKey);
        return K;
    }
}

class FD{
    String LHS; 
    String RHS;
    public FD(String left, String right) { 
        LHS = left; 
        RHS = right; 
    }

    /**
     * Prints the individual FD with the LHS and RHS
     */
    public void printout(){
        System.out.print(LHS);
        System.out.print(" -> "); 
        System.out.print(RHS); 
        System.out.println();
    }

    /**
     * Getter method to get the RHS
     * @return outputs the RHS
     */
    public String getRHS(){
        return RHS;
    }

    /**
     * Getter method for the LHS
     * @return outputs the LHS
     */
    public String getLHS(){
        return LHS;
    }

    /**
     * Equals method for the custom FD class to check 
     * if one fd equals other fd
     */
    @Override
    public boolean equals(Object o){
        FD f = (FD) o;
        return f.LHS.equals(this.LHS) && f.RHS.equals(this.RHS);
    }

    /**
     * A custom hashcode method for the FD class
     */
    @Override
    public int hashCode(){
        return this.LHS.hashCode() + this.RHS.hashCode();
    }
};


