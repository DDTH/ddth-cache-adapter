package com.github.ddth.cacheadapter.qnd;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class NestedValue {
    public boolean active = true;
    public double balance = 123.45;
    public String code = "AB12CD";

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NestedValue) {
            NestedValue another = (NestedValue) obj;
            EqualsBuilder eq = new EqualsBuilder().append(active, another.active)
                    .append(balance, another.balance).append(code, another.code);
            return eq.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 81).append(active).append(balance).append(code).toHashCode();
    }

    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("active", active);
        tsb.append("balance", balance);
        tsb.append("code", code);
        return tsb.toString();
    }
}
