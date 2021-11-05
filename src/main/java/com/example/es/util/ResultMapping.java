////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package com.example.es.util;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//public class ResultMapping {
//    private String property;
//    private String column;
//    private Class<?> javaType;
//    private JdbcType jdbcType;
//    private String nestedResultMapId;
//    private String nestedQueryId;
//    private Set<String> notNullColumns;
//    private String columnPrefix;
//    private List<ResultFlag> flags;
//    private List<ResultMapping> composites;
//    private String resultSet;
//    private String foreignColumn;
//    private boolean lazy;
//
//    ResultMapping() {
//    }
//
//    public String getProperty() {
//        return this.property;
//    }
//
//    public String getColumn() {
//        return this.column;
//    }
//
//    public Class<?> getJavaType() {
//        return this.javaType;
//    }
//
//    public String getNestedResultMapId() {
//        return this.nestedResultMapId;
//    }
//
//    public String getNestedQueryId() {
//        return this.nestedQueryId;
//    }
//
//    public Set<String> getNotNullColumns() {
//        return this.notNullColumns;
//    }
//
//    public String getColumnPrefix() {
//        return this.columnPrefix;
//    }
//
//    public List<ResultFlag> getFlags() {
//        return this.flags;
//    }
//
//    public List<ResultMapping> getComposites() {
//        return this.composites;
//    }
//
//    public boolean isCompositeResult() {
//        return this.composites != null && !this.composites.isEmpty();
//    }
//
//    public String getResultSet() {
//        return this.resultSet;
//    }
//
//    public String getForeignColumn() {
//        return this.foreignColumn;
//    }
//
//    public void setForeignColumn(String foreignColumn) {
//        this.foreignColumn = foreignColumn;
//    }
//
//    public boolean isLazy() {
//        return this.lazy;
//    }
//
//    public void setLazy(boolean lazy) {
//        this.lazy = lazy;
//    }
//
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        } else if (o != null && this.getClass() == o.getClass()) {
//            ResultMapping that = (ResultMapping)o;
//            return this.property != null && this.property.equals(that.property);
//        } else {
//            return false;
//        }
//    }
//
//    public int hashCode() {
//        if (this.property != null) {
//            return this.property.hashCode();
//        } else {
//            return this.column != null ? this.column.hashCode() : 0;
//        }
//    }
//
//    public String toString() {
//        StringBuilder sb = new StringBuilder("ResultMapping{");
//        sb.append("property='").append(this.property).append('\'');
//        sb.append(", column='").append(this.column).append('\'');
//        sb.append(", javaType=").append(this.javaType);
//        sb.append(", nestedResultMapId='").append(this.nestedResultMapId).append('\'');
//        sb.append(", nestedQueryId='").append(this.nestedQueryId).append('\'');
//        sb.append(", notNullColumns=").append(this.notNullColumns);
//        sb.append(", columnPrefix='").append(this.columnPrefix).append('\'');
//        sb.append(", flags=").append(this.flags);
//        sb.append(", composites=").append(this.composites);
//        sb.append(", resultSet='").append(this.resultSet).append('\'');
//        sb.append(", foreignColumn='").append(this.foreignColumn).append('\'');
//        sb.append(", lazy=").append(this.lazy);
//        sb.append('}');
//        return sb.toString();
//    }
//
//    public static class Builder {
//        private ResultMapping resultMapping;
//
//        public Builder(Configuration configuration, String property, String column, TypeHandler<?> typeHandler) {
//            this(configuration, property);
//            this.resultMapping.column = column;
//            this.resultMapping.typeHandler = typeHandler;
//        }
//
//        public Builder(Configuration configuration, String property, String column, Class<?> javaType) {
//            this(configuration, property);
//            this.resultMapping.column = column;
//            this.resultMapping.javaType = javaType;
//        }
//
//        public Builder(Configuration configuration, String property) {
//            this.resultMapping = new ResultMapping();
//            this.resultMapping.configuration = configuration;
//            this.resultMapping.property = property;
//            this.resultMapping.flags = new ArrayList();
//            this.resultMapping.composites = new ArrayList();
//            this.resultMapping.lazy = configuration.isLazyLoadingEnabled();
//        }
//
//        public ResultMapping.Builder javaType(Class<?> javaType) {
//            this.resultMapping.javaType = javaType;
//            return this;
//        }
//
//        public ResultMapping.Builder jdbcType(JdbcType jdbcType) {
//            this.resultMapping.jdbcType = jdbcType;
//            return this;
//        }
//
//        public ResultMapping.Builder nestedResultMapId(String nestedResultMapId) {
//            this.resultMapping.nestedResultMapId = nestedResultMapId;
//            return this;
//        }
//
//        public ResultMapping.Builder nestedQueryId(String nestedQueryId) {
//            this.resultMapping.nestedQueryId = nestedQueryId;
//            return this;
//        }
//
//        public ResultMapping.Builder resultSet(String resultSet) {
//            this.resultMapping.resultSet = resultSet;
//            return this;
//        }
//
//        public ResultMapping.Builder foreignColumn(String foreignColumn) {
//            this.resultMapping.foreignColumn = foreignColumn;
//            return this;
//        }
//
//        public ResultMapping.Builder notNullColumns(Set<String> notNullColumns) {
//            this.resultMapping.notNullColumns = notNullColumns;
//            return this;
//        }
//
//        public ResultMapping.Builder columnPrefix(String columnPrefix) {
//            this.resultMapping.columnPrefix = columnPrefix;
//            return this;
//        }
//
//        public ResultMapping.Builder flags(List<ResultFlag> flags) {
//            this.resultMapping.flags = flags;
//            return this;
//        }
//
//        public ResultMapping.Builder composites(List<ResultMapping> composites) {
//            this.resultMapping.composites = composites;
//            return this;
//        }
//
//        public ResultMapping.Builder lazy(boolean lazy) {
//            this.resultMapping.lazy = lazy;
//            return this;
//        }
//
//        public ResultMapping build() {
//            this.resultMapping.flags = Collections.unmodifiableList(this.resultMapping.flags);
//            this.resultMapping.composites = Collections.unmodifiableList(this.resultMapping.composites);
//            this.resolveTypeHandler();
//            this.validate();
//            return this.resultMapping;
//        }
//
//        private void validate() {
//            if (this.resultMapping.nestedQueryId != null && this.resultMapping.nestedResultMapId != null) {
//                throw new IllegalStateException("Cannot define both nestedQueryId and nestedResultMapId in property " + this.resultMapping.property);
//            } else if (this.resultMapping.nestedResultMapId == null && this.resultMapping.column == null && this.resultMapping.composites.isEmpty()) {
//                throw new IllegalStateException("Mapping is missing column attribute for property " + this.resultMapping.property);
//            } else {
//                if (this.resultMapping.getResultSet() != null) {
//                    int numColumns = 0;
//                    if (this.resultMapping.column != null) {
//                        numColumns = this.resultMapping.column.split(",").length;
//                    }
//
//                    int numForeignColumns = 0;
//                    if (this.resultMapping.foreignColumn != null) {
//                        numForeignColumns = this.resultMapping.foreignColumn.split(",").length;
//                    }
//
//                    if (numColumns != numForeignColumns) {
//                        throw new IllegalStateException("There should be the same number of columns and foreignColumns in property " + this.resultMapping.property);
//                    }
//                }
//
//            }
//        }
//
//        public ResultMapping.Builder column(String column) {
//            this.resultMapping.column = column;
//            return this;
//        }
//    }
//}
