package io.github.michaelfedora.fedoraseconomy.database;

/**
 * Created by Michael on 3/18/2016.
 */
public enum DatabaseQuery {
    NAME("owner", "varchar(255)"),
    DATA("data", "other");

    public final String v;
    public final String dbtype;

    DatabaseQuery(String dbentry, String dbtype) {
        this.v = dbentry;
        this.dbtype = dbtype;
    }

    static String makeConstructor() {
        StringBuilder constructor = new StringBuilder("(");
        int count = 0;
        for(DatabaseQuery q : values()) {
            constructor.append(q.v).append(" ").append(q.dbtype);
            if(++count < values().length)
                constructor.append(", ");
        }
        constructor.append(")");

        return constructor.toString();
    }
}
