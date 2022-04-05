package account.util;

import account.model.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UserUtil {
    public static List<String> getRoles(Set<Group> groups) {
        List<String> result = new ArrayList<>();

        for (Group group : groups) {
            result.add(group.getCode());
        }

        Collections.sort(result);

        return result;
    }
}