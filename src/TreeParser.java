
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import java.util.ArrayList;

/**
 *
 * @author Team 3 - Pooja Shah, Aarti Gorade, Shailesh Vajpayee
 * This file parses the sentence and groups the words into phrases
 */
public class TreeParser {

    // Verbs
    static List<String> vbList = new ArrayList<String>();
    
    // Clauses
    static List<String> sbarList = new ArrayList<String>();
    static List<String> sList = new ArrayList<String>();
    
    // Preposition phrases
    static List<String> ppList = new ArrayList<String>();
    
    // Noun phrases
    static List<String> npList = new ArrayList<String>();
    
    // Noun
    static List<String> nnList = new ArrayList<String>();
    public TreeParser() {

    }

    /**
     * This method parses the sentence and returns the noun phrases,
     * verbs and nouns
     * @param lparser
     * @param filename
     * @return 
     */
    public static Result parseTree(LexicalizedParser lparser, String filename) {
    	GrammaticalStructureFactory gram_struct_fact = null;
        TreebankLanguagePack tree_bank_lang = lparser.treebankLanguagePack();
        
        if (tree_bank_lang.supportsGrammaticalStructures()) {
            gram_struct_fact = tree_bank_lang.grammaticalStructureFactory();
        }
       
        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            Tree parsed_tree = lparser.apply(sentence);
//            parsed_tree.pennPrint();
//            System.out.println();

            if (gram_struct_fact != null) {
                GrammaticalStructure gram_struct = gram_struct_fact.newGrammaticalStructure(parsed_tree);
                Collection type_dependencies = gram_struct.typedDependenciesCCprocessed();
//                System.out.println(type_dependencies);
//                System.out.println();
            }

            vbList.addAll(getPhrases(parsed_tree, "VB"));
            
            sbarList.addAll(getPhrases(parsed_tree, "SBAR"));
            sList.addAll(getPhrases(parsed_tree, "S"));

            ppList.addAll(getPhrases(parsed_tree, "PP"));

            npList.addAll(getPhrases(parsed_tree, "NP"));

            nnList.addAll(getPhrases(parsed_tree, "NN"));
            
            if (sbarList.size() > 0) {
                for (int i = 0; i < sbarList.size(); i++) {
                    if (sbarList.get(i).split(" ").length == sentence.size()) {
                        sbarList.remove(i);
                        i--;
                    }
                }
            }

            if (sbarList.size() == 0) {
                if (sList != null && sList.size() > 0) {
                    for (int i = 0; i < sList.size(); i++) {
                        if (sList.get(i).split(" ").length == sentence.size()) {
                            sList.remove(i);
                            i--;
                        }
                    }
                }
            } else {
                sList = new ArrayList<String>();
            }

            List<String> temp = null;

            // pp -> sbar/s
            if (sbarList.size() > 0) {
                temp = sbarList;
            } else {
                temp = sList;
            }
            for (int j = 0; j < temp.size(); j++) {
                for (int i = 0; i < ppList.size(); i++) {
                    if (temp.get(j).contains(ppList.get(i))) {
                        ppList.remove(i);
                        i--;
                    }
                }
            }

            //np -> sbar/s
            for (int j = 0; j < temp.size(); j++) {
                for (int i = 0; i < npList.size(); i++) {
                    if (temp.get(j).contains(npList.get(i))) {
                        npList.remove(i);
                        i--;
                    }
                }
            }

            //np -> pp
            for (int j = 0; j < ppList.size(); j++) {
                for (int i = 0; i < npList.size(); i++) {
                    if (ppList.get(j).contains(npList.get(i))) {
                        npList.remove(i);
                        i--;
                    }
                }
            }

            // remove sentence containing 'and'
            for (int i = 0; i < npList.size(); i++) {
                if (npList.get(i).contains("and")) {
                    npList.remove(i);
                    i--;
                }
            }
        }
        System.out.println("Finished Natural Language Processing\nSuccessfully extracted nouns and verbs");
        Result result = new Result(npList, vbList, nnList);
        return result;
    }

    public static List<String> getPhrases(Tree parse, String tag) {
        List<String> result = new ArrayList<>();
        TregexPattern pattern = TregexPattern.compile("@" + tag);
        TregexMatcher matcher = pattern.matcher(parse);
        while (matcher.find()) {
            Tree match = matcher.getMatch();
            List<Tree> leaves = match.getLeaves();
            String phrase = Joiner.on(' ').join(Lists.transform(leaves, Functions.toStringFunction()));
            result.add(phrase);
        }
        return result;
    }
}

class Result{
    List<String> npList = null;
    List<String> vbList = null;
    List<String> nnList = null;
    
    public Result(List<String> npList, List<String> vbList, List<String> nnList){
        this.npList = npList;
        this.vbList = vbList;
        this.nnList = nnList;
    }
    
    public List<String> getNPList(){
        return npList;
    }
    
    public List<String> getVBList(){
        return vbList;
    }
    
    public List<String> getNNList(){
        return nnList;
    }
}
