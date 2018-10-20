import java.io.*;
import java.util.*;


class ThirdNormalForm{

    static HashSet<FD> FDS = new HashSet<>();
    public static void main(String[] args){
        try{
            File file = new File(args[0]);
            BufferedReader br = new BufferedReader(new FileReader(file)); 
            String st = ""; 
            ArrayList<String> FDSList = new ArrayList<>();
            while ((st = br.readLine()) != null){
                FDSList.add(st);
            }
        //Check Minmal Basis
        addFDS(FDSList);
        // printFDS();
        // System.out.println("Sperator");
        // System.out.println("--------------");
        minimalBasis();
        // System.out.println("--------------->>>---");
        printFDS();
        
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void addFDS(ArrayList<String> st){
        for(String s: st){
            String[] splitted= s.split(";");
            FD fd = new FD(splitted[0],splitted[1]);
            FDS.add(fd);
        }
    }

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
     */
    public static void minimalBasis() {
        // In this step we split the RHS of multiple dependencies to a relation with 
        FDS = splitRHS(FDS);
        // printFDS();
        // System.out.println("Sperator");
        // System.out.println("--------------");
        FDS = eliminateDependecies(FDS);
        // printFDS();
        
    }
    /**
     * 
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
                        toRemove.add(fd);
                        
                        FD f = new FD(common, fd.getRHS());
                        commonDep.add(f);
                    }
                }
            }
        }

        fds.addAll(toAdd);
        fds.addAll(commonDep);
        fds.removeAll(toRemove);
        return fds;
    }
    
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

    // Check Closure and then do the 3NF algo
    

    //Check code in Relation.java and then Algo.java
    public Set<Relation> decomposeTo3NF(){
		Set<Relation> result = new HashSet<>();
		Set<FuncDep> mb = Algos.minimalBasis(this.fds);
		for(FuncDep fd : mb){
			Set<Attribute> attrsNow = new HashSet<>(fd.getLeft());
			attrsNow.addAll(fd.getRight());
			Set<FuncDep> proj = Algos.projection(attrsNow, mb);
			result.add(new Relation(attrsNow, proj));
		}
		Set<Relation> toRemove = new HashSet<>();
		for(Relation a : result){
			for(Relation b : result){
				if(a != b && a.attrs.containsAll(b.attrs)){
					toRemove.add(b);
				}
			}
		}
		result.removeAll(toRemove);
		Set<Set<Attribute>> keys = Algos.keys(this.attrs, mb);
		boolean contains = false;
		for(Relation r : result){
			for(Set<Attribute> k : keys){
				if(r.attrs.containsAll(k)){
					contains = true;
					break;
				}
			}
			if(contains){
				break;
			}
		}
		if(!contains){
			Set<Attribute> key = null;
			for(Set<Attribute> k : keys){
				key = k;
				break;
			}
			Set<FuncDep> proj = Algos.projection(key, mb);
			result.add(new Relation(key, proj));
		}
		return result;
	}
}

class FD{
    String LHS; 
    String RHS;
    public FD(String left, String right) { 
        LHS = left; 
        RHS = right; 
    }

    public void printout(){
        System.out.print(LHS);
        System.out.print(" -> "); 
        System.out.print(RHS); 
        System.out.println();
    }

    public String getRHS(){
        return RHS;
    }

    public String getLHS(){
        return LHS;
    }

    public boolean equals(FD f){
        if(this.getLHS().equals(f.getLHS()) && this.getRHS().equals(f.getRHS())){
            return true;
        }
        return false;
    }

    public int hashCode(){
        return this.LHS.hashCode() + this.RHS.hashCode();
    }
};