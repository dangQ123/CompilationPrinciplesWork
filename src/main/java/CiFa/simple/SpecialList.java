package CiFa.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecialList {
    public static List<String> specialList = new ArrayList<>(Arrays.asList(
            "alphabet",
            "digit",
            "int", "double", "char", "float", "break", "continue",
            "do", "while",
            "if", "else", "for", "void", "return",
            "scanf", "print", "function"
    ));

    public static List<String> Limiter = new ArrayList<>(Arrays.asList(
            ",", ";", "[", "]", "(", ")", "{", "}"
    ));

    public static List<String> Operator = new ArrayList<>(Arrays.asList(
            "+", "-", "*",
            "/", "%", "^", "&", "=", "<", ">","+=","-="
    ));

    public static List<String> KeyWords = new ArrayList<>(Arrays.asList(
            "int", "double", "char", "float", "break", "continue",
            "do", "while",
            "if", "else", "for", "void", "return",
            "scanf", "print", "function"
    ));

    public static List<String> VT = new ArrayList<>(Arrays.asList(
            ",", ";", "[", "]", "(", ")", "{", "}", "+", "-", "*",
            "/", "%", "^", "&", "=", "<", ">",".",
            "alphabet",
            "digit",
            "_","e","i"
    ));

    public static boolean isVT(String test){
        if (test.length()==1) return true;
        for (String s : VT){
            if (s.equals(test)){
                return true;
            }
        }
        return false;
    }


}
