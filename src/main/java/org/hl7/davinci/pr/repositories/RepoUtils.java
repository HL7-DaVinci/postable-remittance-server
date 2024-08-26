package org.hl7.davinci.pr.repositories;

import jakarta.persistence.criteria.*;

import java.util.List;

public class RepoUtils {
    public static void addEqualFilter(String attribName, Object filterValue,
                                      Root<?> root,
                                      Join<Object, Object> joinObject, CriteriaBuilder cb, List<Predicate> predicates,
                                      boolean isRequired) {

        //if required always add it, if not required only add if there is a value passed
        if (isRequired || filterValue != null) {
            Path<?> predPath = (root != null) ? root.get(attribName) : joinObject.get(attribName);
            Predicate predicate = cb.equal(predPath, filterValue);
            predicates.add(predicate);
        }
    }
}
