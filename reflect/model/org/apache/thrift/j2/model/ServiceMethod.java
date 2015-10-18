package org.apache.thrift.j2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.descriptor.TDefaultValueProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.util.TTypeUtils;

/** (oneway)? <return_type> <name>'('<param>*')' (throws '(' <exception>+ ')')? */
public class ServiceMethod
        implements TMessage<ServiceMethod>, Serializable {
    private final static boolean kDefaultIsOneway = false;

    private final String mComment;
    private final Boolean mIsOneway;
    private final String mReturnType;
    private final String mName;
    private final List<ThriftField> mParams;
    private final List<ThriftField> mExceptions;

    private ServiceMethod(Builder builder) {
        mComment = builder.mComment;
        mIsOneway = builder.mIsOneway;
        mReturnType = builder.mReturnType;
        mName = builder.mName;
        mParams = Collections.unmodifiableList(new LinkedList<>(builder.mParams));
        mExceptions = Collections.unmodifiableList(new LinkedList<>(builder.mExceptions));
    }

    public boolean hasComment() {
        return mComment != null;
    }

    public String getComment() {
        return mComment;
    }

    public boolean hasIsOneway() {
        return mIsOneway != null;
    }

    public boolean getIsOneway() {
        return hasIsOneway() ? mIsOneway : kDefaultIsOneway;
    }

    public boolean hasReturnType() {
        return mReturnType != null;
    }

    public String getReturnType() {
        return mReturnType;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public int numParams() {
        return mParams.size();
    }

    public List<ThriftField> getParams() {
        return mParams;
    }

    public int numExceptions() {
        return mExceptions.size();
    }

    public List<ThriftField> getExceptions() {
        return mExceptions;
    }

    @Override
    public boolean has(int key) {
        switch(key) {
            case 1: return hasComment();
            case 2: return hasIsOneway();
            case 3: return hasReturnType();
            case 4: return hasName();
            case 5: return numParams() > 0;
            case 6: return numExceptions() > 0;
            default: return false;
        }
    }

    @Override
    public int num(int key) {
        switch(key) {
            case 1: return hasComment() ? 1 : 0;
            case 2: return hasIsOneway() ? 1 : 0;
            case 3: return hasReturnType() ? 1 : 0;
            case 4: return hasName() ? 1 : 0;
            case 5: return numParams();
            case 6: return numExceptions();
            default: return 0;
        }
    }

    @Override
    public Object get(int key) {
        switch(key) {
            case 1: return getComment();
            case 2: return getIsOneway();
            case 3: return getReturnType();
            case 4: return getName();
            case 5: return getParams();
            case 6: return getExceptions();
            default: return null;
        }
    }

    @Override
    public boolean compact() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServiceMethod)) return false;
        ServiceMethod other = (ServiceMethod) o;
        return TTypeUtils.equals(mComment, other.mComment) &&
               TTypeUtils.equals(mIsOneway, other.mIsOneway) &&
               TTypeUtils.equals(mReturnType, other.mReturnType) &&
               TTypeUtils.equals(mName, other.mName) &&
               TTypeUtils.equals(mParams, other.mParams) &&
               TTypeUtils.equals(mExceptions, other.mExceptions);
    }

    @Override
    public int hashCode() {
        return ServiceMethod.class.hashCode() +
               TTypeUtils.hashCode(mComment) +
               TTypeUtils.hashCode(mIsOneway) +
               TTypeUtils.hashCode(mReturnType) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(mParams) +
               TTypeUtils.hashCode(mExceptions);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + TTypeUtils.toString(this);
    }

    @Override
    public boolean isValid() {
        return mName != null;
    }

    @Override
    public TStructDescriptor<ServiceMethod> descriptor() {
        return DESCRIPTOR;
    }

    public static final TStructDescriptor<ServiceMethod> DESCRIPTOR = _createDescriptor();

    private final static class _Factory
            extends TMessageBuilderFactory<ServiceMethod> {
        @Override
        public ServiceMethod.Builder builder() {
            return new ServiceMethod.Builder();
        }
    }

    private static TStructDescriptor<ServiceMethod> _createDescriptor() {
        List<TField<?>> fieldList = new LinkedList<>();
        fieldList.add(new TField<>(null, 1, false, "comment", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 2, false, "is_oneway", TPrimitive.BOOL.provider(), new TDefaultValueProvider<>(kDefaultIsOneway)));
        fieldList.add(new TField<>(null, 3, false, "return_type", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 4, true, "name", TPrimitive.STRING.provider(), null));
        fieldList.add(new TField<>(null, 5, false, "params", TList.provider(ThriftField.provider()), null));
        fieldList.add(new TField<>(null, 6, false, "exceptions", TList.provider(ThriftField.provider()), null));
        return new TStructDescriptor<>(null, "model", "ServiceMethod", fieldList, new _Factory(), false);
    }

    public static TStructDescriptorProvider<ServiceMethod> provider() {
        return new TStructDescriptorProvider<ServiceMethod>() {
            @Override
            public TStructDescriptor<ServiceMethod> descriptor() {
                return DESCRIPTOR;
            }
        };
    }

    @Override
    public ServiceMethod.Builder mutate() {
        return new ServiceMethod.Builder(this);
    }

    public static ServiceMethod.Builder builder() {
        return new ServiceMethod.Builder();
    }

    public static class Builder
            extends TMessageBuilder<ServiceMethod> {
        private String mComment;
        private Boolean mIsOneway;
        private String mReturnType;
        private String mName;
        private List<ThriftField> mParams;
        private List<ThriftField> mExceptions;

        public Builder() {
            mParams = new LinkedList<>();
            mExceptions = new LinkedList<>();
        }

        public Builder(ServiceMethod base) {
            this();

            mComment = base.mComment;
            mIsOneway = base.mIsOneway;
            mReturnType = base.mReturnType;
            mName = base.mName;
            mParams.addAll(base.mParams);
            mExceptions.addAll(base.mExceptions);
        }

        public Builder setComment(String value) {
            mComment = value;
            return this;
        }

        public Builder clearComment() {
            mComment = null;
            return this;
        }

        public Builder setIsOneway(boolean value) {
            mIsOneway = value;
            return this;
        }

        public Builder clearIsOneway() {
            mIsOneway = null;
            return this;
        }

        public Builder setReturnType(String value) {
            mReturnType = value;
            return this;
        }

        public Builder clearReturnType() {
            mReturnType = null;
            return this;
        }

        public Builder setName(String value) {
            mName = value;
            return this;
        }

        public Builder clearName() {
            mName = null;
            return this;
        }

        public Builder setParams(Collection<ThriftField> value) {
            mParams.clear();
            mParams.addAll(value);
            return this;
        }

        public Builder addToParams(ThriftField... values) {
            for (ThriftField item : values) {
                mParams.add(item);
            }
            return this;
        }

        public Builder clearParams() {
            mParams.clear();
            return this;
        }

        public Builder setExceptions(Collection<ThriftField> value) {
            mExceptions.clear();
            mExceptions.addAll(value);
            return this;
        }

        public Builder addToExceptions(ThriftField... values) {
            for (ThriftField item : values) {
                mExceptions.add(item);
            }
            return this;
        }

        public Builder clearExceptions() {
            mExceptions.clear();
            return this;
        }

        @Override
        public Builder set(int key, Object value) {
            switch (key) {
                case 1: setComment((String) value); break;
                case 2: setIsOneway((boolean) value); break;
                case 3: setReturnType((String) value); break;
                case 4: setName((String) value); break;
                case 5: setParams((List<ThriftField>) value); break;
                case 6: setExceptions((List<ThriftField>) value); break;
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null;
        }

        @Override
        public ServiceMethod build() {
            return new ServiceMethod(this);
        }
    }
}