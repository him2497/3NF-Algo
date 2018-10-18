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
        minimalBasis();
        // printFDS();
        
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void addFDS(ArrayList<String> st){
        for(String s: st){
            String[] splitted= s.split(";");
            String lhs = splitted[0];
            // lhs.add(splitted[0]);
            FD fd = new FD(lhs,splitted[1]);
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
        for(FD f: FDS){
            splitRHS(f);
        }

        eliminateDependecies(FDS);
        
    }
    /**
     * 
     * @param functionalDependency the dependency that needs to be broken for RHS
     */

    public static void splitRHS(FD functionalDependency){
        FD temp = functionalDependency;
        String[] rhsSplit = functionalDependency.getRHS().split(",");
        if(rhsSplit.length > 1){
            for(String s: rhsSplit){
                FD fd = new FD(functionalDependency.getLHS(),s);
                FDS.add(fd);
            }
            FDS.remove(temp);
        }
    }

    public static void eliminateDependecies(HashSet<FD> fds){
        for(FD f: fds){
            // System.out.println(f.getLHS().equals(f.getRHS()));
            // System.out.println(f.getRHS() instanceof String);
            // System.out.println(f.getLHS());
            f.getLHS();

        }
    }

    // Check Closure and then do the 3NF algo
    

    //Check code in Relation.java and then Algo.java
}

class FD{
    String LHS; 
    String RHS;
    public FD(String left, String right) { 
        LHS = left; 
        RHS = right; 
    }

    public void printout(){
        for (String c: LHS) System.out.print(c);
        System.out.print(" "); System.out.print(RHS); System.out.println();
    }

    public String getRHS(){
        return RHS;
    }

    public String getLHS(){
        return LHS;
    }

};