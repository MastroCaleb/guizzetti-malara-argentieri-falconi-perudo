package utils;

public class Serializer {

    public static Object serialize(String value){
        int separate = countCharacter(value, ';');

        return null;
    }

    private static int countCharacter(String value, char c){
        int count = 0;

        for(char t : value.toCharArray()){
            if(t == c){
                count++;
            }
        }
        return count;
    }

}
