package com.github.ddth.cacheadapter.test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.github.ddth.commons.serialization.DeserializationException;
import com.github.ddth.commons.serialization.ISerializationSupport;
import com.github.ddth.commons.serialization.SerializationException;

public class TestValue {

    private final static ToStringStyle STYLE = ToStringStyle.SHORT_PREFIX_STYLE;

    public static class Value {
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
            return new HashCodeBuilder(19, 81).append(fullname).append(email).append(dob)
                    .append(nested).append(nestedList).append(nestedMap).append(nestedSet)
                    .toHashCode();
        }

        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, STYLE);
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

    public static class NestedValue {
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
            return new HashCodeBuilder(19, 81).append(active).append(balance).append(code)
                    .toHashCode();
        }

        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, STYLE);
            tsb.append("active", active);
            tsb.append("balance", balance);
            tsb.append("code", code);
            return tsb.toString();
        }
    }

    public static class BaseClass {
        public int version = 0;

        public String name = "base";

        /**
         * {@inheritDoc}
         */
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, STYLE);
            tsb.append("version", version);
            tsb.append("name", name);
            return tsb.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof BaseClass) {
                BaseClass another = (BaseClass) obj;
                EqualsBuilder eq = new EqualsBuilder().append(version, another.version).append(name,
                        another.name);
                return eq.isEquals();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(19, 81).append(version).append(name).toHashCode();
        }
    }

    public static class AClass extends BaseClass {
        public String pname = "A";

        public Value value = new Value();

        /**
         * {@inheritDoc}
         */
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, STYLE);
            tsb.appendSuper(super.toString());
            tsb.append("pname", pname).append("value", value);
            return tsb.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof AClass) {
                AClass another = (AClass) obj;
                EqualsBuilder eq = new EqualsBuilder().appendSuper(super.equals(obj))
                        .append(pname, another.pname).append(value, another.value);
                return eq.isEquals();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(19, 81).appendSuper(super.hashCode()).append(pname)
                    .append(value).hashCode();
        }
    }

    public static class BClass {
        public BaseClass obj;

        /**
         * {@inheritDoc}
         */
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, STYLE);
            tsb.append("obj", obj);
            return tsb.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof BClass) {
                BClass another = (BClass) obj;
                EqualsBuilder eq = new EqualsBuilder().append(this.obj, another.obj);
                return eq.isEquals();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(19, 81).append(obj).hashCode();
        }
    }

    public static class CClass implements ISerializationSupport {
        public int version = 0;

        public String name = "base";

        /**
         * {@inheritDoc}
         */
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, STYLE);
            tsb.append("version", version);
            tsb.append("name", name);
            return tsb.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof CClass) {
                CClass another = (CClass) obj;
                EqualsBuilder eq = new EqualsBuilder().append(version, another.version).append(name,
                        another.name);
                return eq.isEquals();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(19, 81).append(version).append(name).toHashCode();
        }

        private final static char DELIM = '\u0001';
        private final static Charset UTF8 = Charset.forName("UTF-8");

        @Override
        public byte[] toBytes() throws SerializationException {
            String s = "" + version + DELIM + name;
            byte[] data = s.getBytes(UTF8);
            return data;
        }

        @Override
        public ISerializationSupport fromBytes(byte[] data) throws DeserializationException {
            String[] tokens = StringUtils.split(new String(data, UTF8), DELIM);
            version = Integer.parseInt(tokens[0]);
            name = tokens[1];
            return this;
        }
    }
}