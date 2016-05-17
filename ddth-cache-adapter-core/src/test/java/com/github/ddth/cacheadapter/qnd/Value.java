package com.github.ddth.cacheadapter.qnd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Value {
    public String fullname = "My Fullname";
    public String email = "mydmain@domain.com";
    public Date dob = new Date();
    public NestedValue nested = new NestedValue();
    public List<NestedValue> nestedList = new ArrayList<>();
    public Set<NestedValue> nestedSet = new HashSet<>();
    public Map<String, NestedValue> nestedMap = new HashMap<>();

    public Value() {
        for (int i = 0; i < 2; i++) {
            NestedValue value = new NestedValue();
            value.code = "CODE" + String.valueOf(i);
            nestedList.add(value);
        }
        for (int i = 3; i < 5; i++) {
            NestedValue value = new NestedValue();
            value.code = "CODE" + String.valueOf(i);
            nestedSet.add(value);
        }
        for (int i = 7; i < 9; i++) {
            NestedValue value = new NestedValue();
            value.code = "CODE" + String.valueOf(i);
            nestedMap.put(value.code, value);
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof Value) {
            Value another = (Value) obj;
            EqualsBuilder eq = new EqualsBuilder().append(fullname, another.fullname)
                    .append(email, another.email).append(dob, another.dob)
                    .append(nested, another.nested).append(nestedList, another.nestedList)
                    .append(nestedMap, another.nestedMap).append(nestedSet, another.nestedSet);
            return eq.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 81).append(fullname).append(email).append(dob).append(nested)
                .append(nestedList).append(nestedMap).append(nestedSet).toHashCode();
    }

    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("fullname", fullname);
        tsb.append("email", email);
        tsb.append("dob", dob);
        tsb.append("nested", nested);
        tsb.append("nestedList", nestedList);
        tsb.append("nestedSet", nestedSet);
        tsb.append("nestedMap", nestedMap);
        return tsb.toString();
    }
}
