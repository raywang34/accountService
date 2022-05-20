package account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

/**
 * @author Ray
 */
@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    public static List<List<String>> groupAnagrams(String[] strs) {

        Map<String, List<String>> hashMap = new HashMap<>();

        List<String> tmpList;

        char[] strtoChar;

        String sortedStr;

        for (String str : strs) {
            strtoChar = str.toCharArray();
            Arrays.sort(strtoChar);
            sortedStr = String.valueOf(strtoChar);

            if (hashMap.get(sortedStr) != null) {
                tmpList = hashMap.get(sortedStr);
                tmpList.add(str);
                hashMap.put(sortedStr, tmpList);
            } else {
                tmpList = new ArrayList<>();
                tmpList.add(str);
                hashMap.put(sortedStr, tmpList);
            }
        }

        return new ArrayList<>(hashMap.values());
    }
}